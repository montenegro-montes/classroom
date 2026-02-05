#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse, json, math
from collections import Counter
from typing import List, Dict, Tuple

# ---------- utilidades n-gramas (no solapados) ----------

def tokenize(text: str, n: int) -> Tuple[List[str], str]:
    tokens = [text[i:i+n] for i in range(0, len(text) - n + 1, n)]
    tail = text[len(tokens)*n:]
    return tokens, tail

# ---------- construcción del modelo global ----------

def build_model(tokens: List[str]) -> Tuple[List[str], Dict[str, int], Dict[str, float]]:
    freq = Counter(tokens)
    alphabet = sorted(freq.keys())                # orden lexicográfico estable
    total = sum(freq.values())
    probs = {t: freq[t] / total for t in alphabet}
    return alphabet, freq, probs

def cumulative_a(alphabet: List[str], probs: Dict[str,float]) -> Dict[str,float]:
    a = {}
    acc = 0.0
    for t in alphabet:
        a[t] = acc
        acc += probs[t]
    return a

# ---------- tabla y códigos c(X) ----------

def code_table(alphabet: List[str], probs: Dict[str,float]) -> List[Dict]:
    a = cumulative_a(alphabet, probs)
    table = []
    for t in alphabet:
        P = probs[t]
        invP = 1.0 / P
        nP = math.ceil(math.log2(invP))
        n  = nP + 1
        two_n_a = (2 ** n) * a[t]
        c = math.floor(two_n_a) + 1                    # c - 1 <= 2^n a < c
        code = format(c, '0{}b'.format(n))             # binario con n bits
        table.append({
            "X": t, "P": P, "a": a[t], "invP": invP,
            "nP": nP, "n": n, "c": c, "code": code
        })
    return table

def codebook_from_table(table: List[Dict]) -> Dict[str, Dict]:
    return {row["X"]: {"n": row["n"], "code": row["code"]} for row in table}

# ---------- empaquetado/desempaquetado de bits ----------

def pack_bits_to_bytes(bitstring: str) -> bytes:
    if not bitstring:
        return b"\x00"
    pad = (8 - (len(bitstring) % 8)) % 8
    bitstring_padded = bitstring + ("0" * pad)
    b = int(bitstring_padded, 2).to_bytes(len(bitstring_padded)//8, "big")
    return bytes([pad]) + b  # primer byte = nº de ceros de padding al final

def unpack_bytes_to_bits(blob: bytes) -> str:
    if not blob:
        return ""
    pad = blob[0]
    data = blob[1:]
    bits = bin(int.from_bytes(data, "big"))[2:]
    needed = len(data) * 8
    if len(bits) < needed:
        bits = "0" * (needed - len(bits)) + bits
    if pad:
        bits = bits[:-pad]
    return bits

# ---------- codificar / decodificar secuencia ----------

def encode_tokens(tokens: List[str], codebook: Dict[str, Dict]) -> str:
    return "".join(codebook[t]["code"] for t in tokens)

def decode_bits_greedy(bitstring: str, table: List[Dict]) -> List[str]:
    # construir trie (diccionario) para búsqueda prefijo
    trie = {}
    END = "__END__"
    for row in table:
        node = trie
        for b in row["code"]:
            node = node.setdefault(b, {})
        node[END] = row["X"]

    out = []
    node = trie
    for b in bitstring:
        if b not in node:
            raise ValueError("Bitstream no decodificable: prefijo desconocido")
        node = node[b]
        if "__END__" in node:
            out.append(node["__END__"])
            node = trie
    return out

# ---------- persistencia del modelo ----------

def save_model_json(path: str, n: int, table: List[Dict], tail: str):
    payload = {
        "version": 1,
        "ngram": n,
        "tail": tail,
        "table": table
    }
    with open(path, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2, ensure_ascii=False)

def load_model_json(path: str) -> Tuple[int, List[Dict], str]:
    with open(path, "r", encoding="utf-8") as f:
        p = json.load(f)
    return int(p["ngram"]), p["table"], p.get("tail", "")

# ---------- CLI ----------

def cmd_encode(args):
    with open(args.input, "r", encoding="utf-8") as f:
        text = f.read()
    original_bytes = len(text.encode("utf-8"))  # tamaño real en bytes del origen

    tokens, tail = tokenize(text, args.ngram)
    if not tokens:
        raise SystemExit("No hay tokens (¿texto vacío o ngram mayor que el tamaño?)")

    alphabet, freq, probs = build_model(tokens)
    table = code_table(alphabet, probs)
    codebook = codebook_from_table(table)

    # (opcional) mostrar tabla con más precisión
    if args.show_table:
        d = args.table_digits
        fmtP = "{:." + str(d) + "f}"
        fmta = "{:." + str(d) + "f}"
        fmtInv = "{:." + str(max(1, d-1)) + "f}"
        hdr = f"{'X':<{args.colw}}  {'P':>{d+4}}  {'a':>{d+4}}  {'1/P':>{max(5,d+2)}}  {'nP':>3}  {'n':>3}  {'c':>6}  c(X)"
        print(hdr)
        for r in table:
            print(f"{r['X']:<{args.colw}}  "
                  f"{fmtP.format(r['P']).rjust(d+4)}  "
                  f"{fmta.format(r['a']).rjust(d+4)}  "
                  f"{fmtInv.format(r['invP']).rjust(max(5,d+2))}  "
                  f"{r['nP']:>3}  {r['n']:>3}  {r['c']:>6}  {r['code']}")

    bitstream = encode_tokens(tokens, codebook)
    blob = pack_bits_to_bytes(bitstream)
    compressed_bytes = len(blob)

    # métricas
    ratio = compressed_bytes / original_bytes if original_bytes else 0.0   # < 1 mejor que original
    bpc   = (compressed_bytes * 8) / len(text) if len(text) else 0.0       # bits por carácter original
    avg_bits_per_token = (compressed_bytes * 8) / len(tokens)

    out_bin  = args.output or (args.input + f".ng{args.ngram}.barc")
    out_json = (args.output_model
                or (args.input + f".ng{args.ngram}.barc.model.json"))

    with open(out_bin, "wb") as f:
        f.write(blob)
    save_model_json(out_json, args.ngram, table, tail)

    print(f"✅ Codificado {len(tokens)} datagramas (n={args.ngram}).")
    print(f"   Original : {original_bytes} bytes")
    print(f"   Comprim.: {compressed_bytes} bytes (solo bitstream, modelo aparte)")
    print(f"   Ratio    : {ratio:.4f} (comprimido/original)  → {'mejor' if ratio<1 else 'peor'}")
    print(f"   bpc      : {bpc:.3f} bits/caracter  |  bits por token: {avg_bits_per_token:.3f}")
    print(f"➡ Bitstream: {out_bin}")
    print(f"➡ Modelo   : {out_json}")

def cmd_decode(args):
    n, table, tail = load_model_json(args.model)
    with open(args.input, "rb") as f:
        blob = f.read()
    bits = unpack_bytes_to_bits(blob)
    tokens = decode_bits_greedy(bits, table)
    text = "".join(tokens) + tail

    out_txt = args.output or (args.input + ".decoded.txt")
    with open(out_txt, "w", encoding="utf-8") as f:
        f.write(text)

    print(f"✅ Decodificados {len(tokens)} datagramas (n={n}).")
    print(f"➡ Texto: {out_txt}")

def build_cli():
    ap = argparse.ArgumentParser(
        description=(
            "Codificación por datagramas (n-gramas no solapados) "
            "siguiendo el método c(X) tal que c/2^n ∈ [a, a+P). "
            "Permite codificar un fichero de texto, generar el modelo de probabilidades, "
            "y decodificar posteriormente."
        )
    )
    sub = ap.add_subparsers(dest="cmd", required=True, help="Subcomandos disponibles")

    # ------------------------- ENCODE -------------------------
    pe = sub.add_parser(
        "encode",
        help="Codifica un fichero de texto en un flujo binario (.barc) y genera el modelo JSON asociado."
    )

    pe.add_argument(
        "input",
        help=(
            "Ruta al fichero de texto de entrada (UTF-8). "
            "Ejemplo: sample.txt"
        ),
    )
    pe.add_argument(
        "--ngram",
        type=int,
        default=2,
        help=(
            "Tamaño del datagrama (n-grama no solapado). "
            "Por ejemplo, 1=caracter individual, 2=pares de símbolos, 3=trigramas, etc."
        ),
    )
    pe.add_argument(
        "-o", "--output",
        help=(
            "Nombre del fichero binario de salida (.barc). "
            "Si no se especifica, se genera automáticamente a partir del nombre del fichero original."
        ),
    )
    pe.add_argument(
        "--output-model",
        help=(
            "Nombre del fichero JSON donde se almacenará el modelo (alfabeto, probabilidades, "
            "valores a(X), códigos c(X), etc.). "
            "Si no se indica, se crea automáticamente con el mismo prefijo que el fichero de entrada."
        ),
    )
    pe.add_argument(
        "--show-table",
        action="store_true",
        help=(
            "Muestra por consola la tabla de códigos con columnas X, P, a, 1/P, nP, n, c y c(X). "
            "Permite inspeccionar la asignación de códigos antes de generar el binario."
        ),
    )
    pe.add_argument(
        "--table-digits",
        type=int,
        default=6,
        help=(
            "Número de decimales a mostrar en las columnas P y a dentro de la tabla (--show-table). "
            "Por defecto 6."
        ),
    )
    pe.add_argument(
        "--colw",
        type=int,
        default=6,
        help=(
            "Ancho de columna reservado para la visualización del símbolo X "
            "en la tabla (--show-table). Ajustar si los datagramas son largos."
        ),
    )
    pe.set_defaults(func=cmd_encode)

    # ------------------------- DECODE -------------------------
    pd = sub.add_parser(
        "decode",
        help="Decodifica un fichero binario (.barc) utilizando el modelo JSON generado durante la codificación."
    )
    pd.add_argument(
        "input",
        help=(
            "Ruta al fichero binario codificado (.barc) que se desea decodificar."
        ),
    )
    pd.add_argument(
        "--model",
        required=True,
        help=(
            "Ruta al fichero JSON del modelo generado durante la codificación. "
            "Debe corresponder al mismo fichero de entrada utilizado en 'encode'."
        ),
    )
    pd.add_argument(
        "-o", "--output",
        help=(
            "Ruta del fichero de texto de salida (UTF-8) con el texto reconstruido. "
            "Si no se especifica, se crea automáticamente con el sufijo '.decoded.txt'."
        ),
    )
    pd.set_defaults(func=cmd_decode)

    return ap


def main():
    ap = build_cli()
    args = ap.parse_args()
    args.func(args)

if __name__ == "__main__":
    main()
