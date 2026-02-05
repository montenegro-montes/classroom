#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
LZW encoder/decoder (UTF-8 text) con tabla de pasos.

Formato binario (.lzw):
  [4 bytes]    magic: b"LZW1"
  [4 bytes]    m = nº símbolos alfabeto inicial (uint32)
  Repetido m veces:
    [4 bytes]  len_i = longitud en bytes del símbolo i (uint32)
    [len_i]    símbolo i en UTF-8
  [8 bytes]    n = nº de códigos emitidos (uint64)
  Repetido n veces:
    [4 bytes]  code_j (uint32)
"""

from __future__ import annotations
import argparse
import sys
from pathlib import Path
from typing import Dict, List, Tuple, Iterable
import struct
import json

MAGIC = b"LZW1"

# ------------------------- Utilidades I/O binario -------------------------

def write_lzw(path: Path, alphabet: List[str], codes: List[int]) -> None:
    with path.open("wb") as f:
        f.write(MAGIC)
        f.write(struct.pack("<I", len(alphabet)))
        for s in alphabet:
            b = s.encode("utf-8")
            f.write(struct.pack("<I", len(b)))
            f.write(b)
        f.write(struct.pack("<Q", len(codes)))
        for c in codes:
            f.write(struct.pack("<I", c))

def read_lzw(path: Path) -> Tuple[List[str], List[int]]:
    with path.open("rb") as f:
        magic = f.read(4)
        if magic != MAGIC:
            raise ValueError("Formato no reconocido (magic)")
        (m,) = struct.unpack("<I", f.read(4))
        alphabet = []
        for _ in range(m):
            (ln,) = struct.unpack("<I", f.read(4))
            b = f.read(ln)
            alphabet.append(b.decode("utf-8"))
        (n,) = struct.unpack("<Q", f.read(8))
        codes = [struct.unpack("<I", f.read(4))[0] for _ in range(n)]
    return alphabet, codes

# ----------------------------- Núcleo LZW ---------------------------------

def lzw_encode(text: str, show_steps: bool=False, table_digits: int=80, max_rows: int|None=None) -> Tuple[List[str], List[int]]:
    """
    Devuelve (alphabet, codes). El alfabeto inicial es la lista de símbolos únicos
    en orden de primera aparición.
    """
    if not text:
        return [], []

    # Alfabeto inicial: símbolos únicos en orden de aparición
    seen = set()
    alphabet: List[str] = []
    for ch in text:
        if ch not in seen:
            seen.add(ch)
            alphabet.append(ch)

    # Diccionario: cadena -> código
    dict_enc: Dict[str, int] = {s: i for i, s in enumerate(alphabet)}
    next_code = len(dict_enc)

    w = ""
    codes: List[int] = []
    steps_printed = 0

    def print_step(i: int, w_s: str, k_s: str, out_code: int|None, new_entry: str|None):
        nonlocal steps_printed
        if not show_steps:
            return
        if steps_printed == 0:
            header = f"{'i':>4} | {'w':<{table_digits}} | {'k':^5} | {'emit':>6} | {'add to dict':<{table_digits}}"
            line = "-" * len(header)
            print(header)
            print(line)
        w_disp = w_s.replace("\n", "\\n")
        k_disp = k_s.replace("\n", "\\n")
        add_disp = (new_entry or "").replace("\n", "\\n")
        print(f"{i:4d} | {w_disp[:table_digits]:<{table_digits}} | {k_disp:^5} | {out_code if out_code is not None else '':>6} | {add_disp[:table_digits]:<{table_digits}}")
        steps_printed += 1

    i = 1
    for k in text:
        if (w + k) in dict_enc:
            w = w + k
            print_step(i, w, k, None, None)
        else:
            # Emitimos código de w y añadimos w+k al diccionario
            codes.append(dict_enc[w] if w else dict_enc[k])  # w vacío solo ocurriría al inicio
            new_entry = w + k
            dict_enc[new_entry] = next_code
            next_code += 1
            print_step(i, w, k, codes[-1], new_entry)
            w = k
        i += 1

    if w:
        codes.append(dict_enc[w])
        print_step(i, w, "", codes[-1], None)

    # Limitar filas impresas (si se pide)
    if show_steps and max_rows is not None:
        # Ya imprimimos en vivo; esta opción se deja por compatibilidad con la firma
        pass

    return alphabet, codes


def lzw_decode(alphabet: List[str], codes: List[int]) -> str:
    if not codes:
        return ""

    # Diccionario decodificador: código -> cadena
    dict_dec: Dict[int, str] = {i: s for i, s in enumerate(alphabet)}
    next_code = len(dict_dec)

    # Primer símbolo
    prev = dict_dec[codes[0]]
    out = [prev]

    for code in codes[1:]:
        if code in dict_dec:
            entry = dict_dec[code]
        elif code == next_code:
            # Caso especial KwKwK (entrada aún no insertada)
            entry = prev + prev[0]
        else:
            raise ValueError(f"Código inválido durante decodificación: {code}")

        out.append(entry)
        # Añadir nueva entrada: prev + primer char de entry
        dict_dec[next_code] = prev + entry[0]
        next_code += 1
        prev = entry

    return "".join(out)

# ------------------------------- CLI --------------------------------------

def cmd_encode(args: argparse.Namespace) -> None:
    text = Path(args.input).read_text(encoding="utf-8")
    alphabet, codes = lzw_encode(text, show_steps=args.show_steps, table_digits=args.colw, max_rows=None)

    # Guardar binario
    out_bin = Path(args.output) if args.output else Path(args.input).with_suffix(".lzw")
    write_lzw(out_bin, alphabet, codes)

    if args.output_json:
        meta = {
            "alphabet": alphabet,
            "num_codes": len(codes),
            "codes_sample": codes[:min(50, len(codes))]
        }
        Path(args.output_json).write_text(json.dumps(meta, ensure_ascii=False, indent=2), encoding="utf-8")

    print(f"[OK] LZW codificado: {len(codes)} códigos, alfabeto={len(alphabet)}. Guardado en: {out_bin}")

def cmd_decode(args: argparse.Namespace) -> None:
    alphabet, codes = read_lzw(Path(args.input))
    text = lzw_decode(alphabet, codes)
    if args.output:
        Path(args.output).write_text(text, encoding="utf-8")
        print(f"[OK] Texto decodificado escrito en: {args.output}")
    else:
        sys.stdout.write(text)

def cmd_inspect(args: argparse.Namespace) -> None:
    alphabet, codes = read_lzw(Path(args.input))
    print(json.dumps({
        "alphabet_size": len(alphabet),
        "num_codes": len(codes),
        "alphabet_preview": alphabet[:min(50, len(alphabet))],
        "codes_preview": codes[:min(50, len(codes))]
    }, ensure_ascii=False, indent=2))

def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description="Codificación LZW (UTF-8) con tabla de pasos.")
    sub = p.add_subparsers(dest="cmd", required=True)

    pe = sub.add_parser("encode", help="Codificar un fichero de texto UTF-8 a .lzw")
    pe.add_argument("input", help="Fichero de entrada (UTF-8)")
    pe.add_argument("-o", "--output", help="Fichero binario de salida (.lzw)")
    pe.add_argument("--output-json", help="Escribe un JSON con metadatos (alfabeto, nº códigos)")
    pe.add_argument("--show-steps", action="store_true", help="Muestra la tabla i, w, k, emit, add-to-dict")
    pe.add_argument("--colw", type=int, default=20, help="Ancho de columna para mostrar w / add-to-dict")
    pe.set_defaults(func=cmd_encode)

    pd = sub.add_parser("decode", help="Decodificar un .lzw a texto UTF-8")
    pd.add_argument("input", help="Fichero .lzw")
    pd.add_argument("-o", "--output", help="Fichero de salida (UTF-8). Si se omite, imprime por stdout")
    pd.set_defaults(func=cmd_decode)

    pi = sub.add_parser("inspect", help="Muestra información del archivo .lzw")
    pi.add_argument("input", help="Fichero .lzw")
    pi.set_defaults(func=cmd_inspect)

    return p

def main(argv: Iterable[str] | None = None) -> None:
    parser = build_parser()
    args = parser.parse_args(argv)
    args.func(args)

if __name__ == "__main__":
    main()
