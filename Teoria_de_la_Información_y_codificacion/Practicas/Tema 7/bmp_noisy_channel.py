#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
bmp_noisy_channel.py
- Lee un BMP (24-bit, BI_RGB sin comprimir) y aplica ruido a los PIXELES.
- Mantiene el encabezado BMP + DIB intactos para que cualquier visor abra el fichero.
- Canales: BSC (p por bit), Gilbert-Elliott (ráfagas).
- Mide BER real y nº de bytes modificados.

Uso:
  python3 bmp_noisy_channel.py in.bmp out.bmp --channel bsc --p 0.01 --seed 123
  python3 bmp_noisy_channel.py in.bmp out.bmp --channel ge --pg 1e-5 --pb 1e-2 --a 1e-4 --b 5e-3 --seed 42
"""

import argparse
import struct
import sys
import numpy as np
from typing import Tuple

# ---------------- BMP helpers ----------------

class BMPInfo:
    def __init__(self, header: bytes, dib: bytes, pixel_offset: int, width: int, height: int,
                 bpp: int, compression: int, image_size: int, row_stride: int):
        self.header = header
        self.dib = dib
        self.pixel_offset = pixel_offset
        self.width = width
        self.height = height
        self.bpp = bpp
        self.compression = compression
        self.image_size = image_size
        self.row_stride = row_stride

def read_bmp(path: str) -> Tuple[BMPInfo, bytearray, bytes]:
    with open(path, "rb") as f:
        data = f.read()

    if len(data) < 54:
        raise ValueError("Archivo demasiado pequeño para ser BMP válido.")

    # BMP header (14 bytes)
    header = data[:14]
    sig = header[:2]
    if sig != b"BM":
        raise ValueError("Firma BMP no reconocida (esperado 'BM').")

    file_size, reserved1, reserved2, pixel_offset = struct.unpack("<IHHI", header[2:14])
    if file_size != len(data):
        # Algunos escritores no actualizan; no lo tratamos como error fatal.
        pass

    # DIB header: asumimos BITMAPINFOHEADER (40 bytes)
    dib = data[14:54]
    dib_size = struct.unpack("<I", dib[:4])[0]
    if dib_size < 40:
        raise ValueError("DIB header no soportado (<40 bytes).")

    # Parse BITMAPINFOHEADER
    width, height, planes, bpp, compression, img_size, xppm, yppm, clr_used, clr_imp = struct.unpack("<iiHHIIIIII", dib[4:4+36])

    if bpp != 24:
        raise ValueError(f"Solo se soporta BMP de 24 bits por pixel. Encontrado: {bpp}.")
    if compression != 0:
        raise ValueError(f"Solo se soporta BMP BI_RGB (sin compresión). compression={compression}")

    # Tamaño de imagen (puede venir 0 → lo calculamos)
    row_stride = ((width * 3 + 3) // 4) * 4  # filas alineadas a 4 bytes
    calc_img_size = abs(height) * row_stride
    if img_size == 0:
        img_size = calc_img_size
    else:
        # algunos escritorios redondean distinto; confiamos en el offset + tamaño
        img_size = max(img_size, calc_img_size)

    pixel_end = pixel_offset + img_size
    if pixel_end > len(data):
        raise ValueError("El tamaño de datos de pixeles excede el archivo.")

    pixel_bytes = bytearray(data[pixel_offset:pixel_end])
    prefix = data[:pixel_offset]  # header + paletas (si existieran)
    return BMPInfo(header, dib, pixel_offset, width, height, bpp, compression, img_size, row_stride), pixel_bytes, prefix

def write_bmp(path: str, prefix: bytes, pixel_bytes: bytearray, total_len_hint: int = None):
    out = bytearray(prefix) + pixel_bytes
    # Actualizamos file size en cabecera (offset 2..6 little endian)
    struct.pack_into("<I", out, 2, len(out))
    with open(path, "wb") as f:
        f.write(out)

# --------------- Canales de ruido ----------------

def bsc_apply(pixel_bytes: np.ndarray, p: float, rng: np.random.Generator) -> Tuple[np.ndarray, int, int]:
    """
    Aplica un canal BSC por bit sobre pixel_bytes (vector uint8).
    Devuelve: bytes_noisy, bits_flipped, bytes_changed
    """
    nbytes = pixel_bytes.size
    # Genera máscara de bits a flipar con Bernoulli(p) por bit
    # Creamos (nbytes, 8) booleans y empaquetamos a uint8
    flips_bool = rng.random((nbytes, 8)) < p
    # empaquetar: bit0 = 1<<0, bit1 = 1<<1, ...
    bit_weights = (1 << np.arange(8, dtype=np.uint8))
    mask = (flips_bool * bit_weights).sum(axis=1).astype(np.uint8)
    noisy = (pixel_bytes ^ mask).astype(np.uint8)

    bits_flipped = int(flips_bool.sum())
    bytes_changed = int((mask != 0).sum())
    return noisy, bits_flipped, bytes_changed

def gilbert_elliott_apply(pixel_bytes: np.ndarray, pg: float, pb: float, a: float, b: float,
                          rng: np.random.Generator) -> Tuple[np.ndarray, int, int]:
    """
    Canal de Gilbert–Elliott por bit (Good/Bad):
      - En estado G, bit flip prob pg
      - En estado B, bit flip prob pb
      - Transiciones: P(G->B)=a, P(B->G)=b
    Genera máscara por bit sobre todos los bits del vector.
    """
    nbytes = pixel_bytes.size
    nbits = nbytes * 8

    # Simulación del estado como cadena de Markov
    state = 0  # 0=G, 1=B (arrancamos en buen estado)
    states = np.empty(nbits, dtype=np.uint8)
    u = rng.random(nbits)
    for i in range(nbits):
        states[i] = state
        if state == 0:
            if u[i] < a:
                state = 1
        else:
            if u[i] < b:
                state = 0

    # Prob. de flip por bit según estado
    flips = rng.random(nbits)
    flips_bool = np.where(states == 0, flips < pg, flips < pb)

    # Reempaquetar a bytes
    flips_bool = flips_bool.reshape(nbytes, 8)
    bit_weights = (1 << np.arange(8, dtype=np.uint8))
    mask = (flips_bool * bit_weights).sum(axis=1).astype(np.uint8)
    noisy = (pixel_bytes ^ mask).astype(np.uint8)

    bits_flipped = int(flips_bool.sum())
    bytes_changed = int((mask != 0).sum())
    return noisy, bits_flipped, bytes_changed

# --------------- Métricas ----------------

def ber(bits_flipped: int, total_bits: int) -> float:
    return bits_flipped / float(total_bits) if total_bits else 0.0

# --------------- Main ----------------

def parse_args():
    ap = argparse.ArgumentParser(description="Aplica canal con ruido a un BMP 24bpp sin comprimir.")
    ap.add_argument("in_bmp")
    ap.add_argument("out_bmp")
    ap.add_argument("--channel", choices=["bsc", "ge"], default="bsc", help="Tipo de canal: bsc (binario simétrico) o ge (Gilbert–Elliott).")
    # BSC
    ap.add_argument("--p", type=float, default=0.01, help="Probabilidad de error por bit (BSC).")
    # GE
    ap.add_argument("--pg", type=float, default=1e-5, help="Prob. de flip en estado Bueno (GE).")
    ap.add_argument("--pb", type=float, default=1e-2, help="Prob. de flip en estado Malo (GE).")
    ap.add_argument("--a", type=float, default=1e-4, help="Transición G->B (GE).")
    ap.add_argument("--b", type=float, default=5e-3, help="Transición B->G (GE).")
    # misc
    ap.add_argument("--seed", type=int, default=None, help="Semilla RNG para reproducibilidad.")
    return ap.parse_args()

def main():
    args = parse_args()
    rng = np.random.default_rng(args.seed)

    # Leer BMP
    info, pixel_bytes, prefix = read_bmp(args.in_bmp)
    pix = np.frombuffer(pixel_bytes, dtype=np.uint8)

    total_bits = pix.size * 8

    # Aplicar canal
    if args.channel == "bsc":
        noisy, bits_flipped, bytes_changed = bsc_apply(pix, args.p, rng)
        channel_desc = f"BSC(p={args.p})"
    else:
        noisy, bits_flipped, bytes_changed = gilbert_elliott_apply(pix, args.pg, args.pb, args.a, args.b, rng)
        channel_desc = f"GE(pg={args.pg}, pb={args.pb}, a={args.a}, b={args.b})"

    # Guardar BMP ruidoso
    noisy_bytes = bytearray(noisy.tobytes())
    write_bmp(args.out_bmp, prefix, noisy_bytes)

    # Métricas
    ber_value = ber(bits_flipped, total_bits)
    print(f"[OK] Canal: {channel_desc}")
    print(f"     Imagen: {info.width}x{abs(info.height)} px, 24 bpp, stride={info.row_stride}, bytes_pixeles={pix.size}")
    print(f"     Bits totales: {total_bits:,}")
    print(f"     Bits alterados: {bits_flipped:,}  -> BER={ber_value:.6e}")
    print(f"     Bytes alterados: {bytes_changed:,}")
    print(f"[OUT] Guardado: {args.out_bmp}")

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"[ERROR] {e}", file=sys.stderr)
        sys.exit(1)
