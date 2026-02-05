#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
bmp_ecc_channel.py  (patched with CSV reporting and robust bit ordering)
- Canal ruidoso sobre BMP 24bpp (sin compresión).
- ECC seleccionable: none | hamming74 | bch15 | golay23
- Codifica (sistemático), pasa por canal BSC (p), decodifica (síndrome con tabla de coset leaders).
- Salidas: *_noecc.bmp (ruido directo) y *_ecc.bmp (con corrección).
- CSV opcional por bloque con estadísticas: --csv report.csv
"""

import argparse, struct, sys, itertools, csv
from typing import Tuple, Dict, Optional, List
import numpy as np

# ---------- Utilidades BMP ----------
class BMPInfo:
    def __init__(self, pixel_offset, width, height, bpp, compression, image_size, row_stride, prefix_len):
        self.pixel_offset = pixel_offset
        self.width = width
        self.height = height
        self.bpp = bpp
        self.compression = compression
        self.image_size = image_size
        self.row_stride = row_stride
        self.prefix_len = prefix_len

def read_bmp(path: str) -> Tuple[BMPInfo, bytearray, bytes]:
    data = open(path, "rb").read()
    if len(data) < 54 or data[:2] != b"BM":
        raise ValueError("BMP inválido o no 'BM'.")
    file_size, _, _, pixel_offset = struct.unpack("<IHHI", data[2:14])
    dib = data[14:54]
    dib_size = struct.unpack("<I", dib[:4])[0]
    if dib_size < 40:
        raise ValueError("DIB < 40 no soportado.")
    width, height, planes, bpp, compression, img_size, *_ = struct.unpack("<iiHHIIIIII", dib[4:4+36])
    if bpp != 24:
        raise ValueError(f"Solo 24bpp soportado. bpp={bpp}")
    if compression != 0:
        raise ValueError(f"Solo BI_RGB (sin compresión). compression={compression}")
    row_stride = ((width*3 + 3)//4)*4
    calc = abs(height)*row_stride
    if img_size == 0: img_size = calc
    pixel_end = pixel_offset + img_size
    if pixel_end > len(data):
        raise ValueError("Inconsistencia de tamaño de pixeles.")
    pixels = bytearray(data[pixel_offset:pixel_end])
    prefix = data[:pixel_offset]
    info = BMPInfo(pixel_offset, width, height, bpp, compression, img_size, row_stride, len(prefix))
    return info, pixels, prefix

def write_bmp(path: str, prefix: bytes, pix: bytearray):
    out = bytearray(prefix) + pix
    struct.pack_into("<I", out, 2, len(out))  # file size
    open(path, "wb").write(out)

# ---------- Bits helpers (MSB -> LSB) ----------
def int_to_bits(x: int, width: int) -> np.ndarray:
    """MSB primero: bit width-1 ... bit 0."""
    return np.array([(x >> (width - 1 - i)) & 1 for i in range(width)], dtype=np.uint8)

def bits_to_int(bits: np.ndarray) -> int:
    """Interpreta una secuencia MSB->LSB como entero."""
    v = 0
    for b in bits:
        v = (v << 1) | int(b)
    return v

def bits_from_bytes(b: bytes) -> np.ndarray:
    return np.unpackbits(np.frombuffer(b, dtype=np.uint8))

def bytes_from_bits(bits: np.ndarray) -> bytes:
    pad = (-len(bits)) % 8
    if pad:
        bits = np.concatenate([bits, np.zeros(pad, dtype=np.uint8)])
    return np.packbits(bits).tobytes()

# ---------- Canal (BSC) ----------
def bsc_bits(bits: np.ndarray, p: float, rng: np.random.Generator) -> Tuple[np.ndarray, int]:
    flips = rng.random(bits.shape) < p
    out = np.bitwise_xor(bits, flips.astype(np.uint8))
    return out, int(flips.sum())

# ---------- Utilidades de polinomios binarios ----------
def deg(x: int) -> int:
    return x.bit_length()-1 if x else -1

def mod_rem(poly: int, gen: int) -> int:
    """Resto de poly / gen en GF(2) con representación de bits (LSB = x^0)."""
    dg, p = deg(gen), poly
    while deg(p) >= dg:
        p ^= gen << (deg(p)-dg)
    return p

def weight(x: int) -> int:
    return x.bit_count()

# ---------- CSV helpers ----------
def write_block_csv(path: str, rows: List[dict], fields: List[str]):
    with open(path, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=fields)
        w.writeheader()
        w.writerows(rows)

def blocks_hamming(enc_bits: np.ndarray, noisy_bits: np.ndarray, n: int) -> np.ndarray:
    assert len(enc_bits) == len(noisy_bits)
    nb = len(enc_bits) // n
    enc = enc_bits.reshape(nb, n)
    noi = noisy_bits.reshape(nb, n)
    return (enc ^ noi).sum(axis=1)

# ---------- Clase base ECC ----------
class ECC:
    name: str
    n: int
    k: int
    t: int
    g: int        # generador polinómico (bits, LSB=x^0)
    m: int        # deg(g)

    def __init__(self):
        self.syndrome_table: Dict[int, int] = {}  # s -> error pattern
        self._build_syndrome_table()

    def encode_block(self, msg_bits: int) -> int:
        """ sistemático: c(x)=m(x)*x^(n-k) + (m x^(n-k) mod g) """
        shifted = msg_bits << (self.n - self.k)
        r = mod_rem(shifted, self.g)
        return shifted ^ r

    def decode_block(self, recv: int) -> Tuple[int, int, bool, int]:
        """ Devuelve (codeword_corr, err_weight, uncorrectable?, syndrome). """
        s = mod_rem(recv, self.g)
        if s == 0:
            return recv, 0, False, s
        e = self.syndrome_table.get(s, None)
        if e is None:
            return recv, 0, True, s
        corr = recv ^ e
        return corr, weight(e), False, s

    def pack_bits_to_blocks(self, bits: np.ndarray) -> Tuple[np.ndarray, int]:
        """
        Empaqueta el stream de bits (MSB->LSB) en bloques de k bits,
        codifica a n bits (sistemático), y devuelve el stream de n-bits concatenados.
        Retorna (enc_bits, pad_k).
        """
        L = len(bits)
        pad_k = (-L) % self.k
        if pad_k:
            bits = np.concatenate([bits, np.zeros(pad_k, dtype=np.uint8)])

        enc_chunks = []
        for i in range(0, len(bits), self.k):
            m_chunk = bits[i:i+self.k]               # k bits MSB->LSB
            m_int = bits_to_int(m_chunk)             # entero del mensaje
            c_int = self.encode_block(m_int)         # entero del codeword sistemático
            c_bits = int_to_bits(c_int, self.n)      # n bits MSB->LSB
            enc_chunks.append(c_bits)

        enc_bits = np.concatenate(enc_chunks) if enc_chunks else np.array([], dtype=np.uint8)
        return enc_bits, pad_k

    # ---- tabla de síndromes con coset leaders de peso <= t ----
    def _build_syndrome_table(self):
        self.m = deg(self.g)
        self.syndrome_table = {0: 0}
        for w in range(1, self.t+1):
            for positions in itertools.combinations(range(self.n), w):
                e = 0
                for pos in positions:
                    # Representamos el bloque como entero cuyo MSB es x^(n-1)
                    e |= (1 << (self.n-1 - pos))
                s = mod_rem(e, self.g)
                # Guarda el líder mínimo si no existe
                if s not in self.syndrome_table or weight(e) < weight(self.syndrome_table[s]):
                    self.syndrome_table[s] = e

    def decode_stream(self, enc_noisy_bits: np.ndarray, pad_k: int) -> Tuple[np.ndarray, int, int, list]:
        """
        Decodifica el stream concatenado de n-bits por bloque.
        Devuelve (msg_bits_decodificados, corrected_blocks, uncorrectable_blocks, stats_rows).
        """
        assert len(enc_noisy_bits) % self.n == 0, "Longitud no múltiplo de n."
        nb = len(enc_noisy_bits) // self.n

        msg_chunks = []
        corr_blocks = 0
        uncor = 0
        stats_rows = []

        for i in range(nb):
            blk_bits = enc_noisy_bits[i*self.n:(i+1)*self.n]     # n bits MSB->LSB
            recv_int  = bits_to_int(blk_bits)
            corr_int, ew, is_unc, s = self.decode_block(recv_int)
            if ew > 0:
                corr_blocks += 1
            if is_unc:
                uncor += 1
            # Extrae los k bits sistemáticos (más significativos)
            m_int = corr_int >> (self.n - self.k)
            m_bits = int_to_bits(m_int, self.k)
            msg_chunks.append(m_bits)

            stats_rows.append({
                "block": i,
                "syndrome_hex": f"{s:#0{(self.m//4)+3}x}",
                "syndrome_bits": "".join(str(b) for b in int_to_bits(s, self.m)),
                "err_weight": ew,
                "corrected": 1 if (ew > 0 and not is_unc) else 0,
                "uncorrectable": 1 if is_unc else 0,
                "valid_msg_bits": self.k  # se ajusta fuera si hay padding en último bloque
            })

        msg_bits = np.concatenate(msg_chunks) if msg_chunks else np.array([], dtype=np.uint8)
        if pad_k:
            msg_bits = msg_bits[:-pad_k]
            if stats_rows:
                stats_rows[-1]["valid_msg_bits"] = self.k - pad_k
        return msg_bits, corr_blocks, uncor, stats_rows

# ---------- ECC concretos ----------
class Hamming74(ECC):
    def __init__(self):
        self.name = "Hamming(7,4,3)"
        self.n, self.k, self.t = 7, 4, 1
        # g(x)=x^3 + x + 1 = 0b1_0111 (LSB=x^0)
        self.g = int("10111", 2)
        super().__init__()

class BCH15(ECC):
    def __init__(self):
        self.name = "BCH(15,7,5)"
        self.n, self.k, self.t = 15, 7, 2
        # g(x)=x^8 + x^7 + x^6 + x^4 + 1
        self.g = (1<<8)|(1<<7)|(1<<6)|(1<<4)|1
        super().__init__()

class Golay2312(ECC):
    def __init__(self):
        self.name = "Golay(23,12,7)"
        self.n, self.k, self.t = 23, 12, 3
        # generador estándar: x^11 + x^9 + x^7 + x^6 + x^5 + x + 1
        self.g = (1<<11)|(1<<9)|(1<<7)|(1<<6)|(1<<5)|(1<<1)|1
        super().__init__()

# ---------- CLI ----------
def parse_args():
    ap = argparse.ArgumentParser(description="Canal ruidoso + ECC sobre BMP 24bpp.")
    ap.add_argument("in_bmp")
    ap.add_argument("--outbase", default="out", help="Prefijo de salida.")
    ap.add_argument("--ecc", choices=["none","hamming74","bch15","golay23"], default="bch15")
    ap.add_argument("--p", type=float, default=0.02, help="Prob. de bit-flip (BSC).")
    ap.add_argument("--seed", type=int, default=123)
    ap.add_argument("--csv", default=None, help="Ruta CSV para informe por bloque (opcional).")
    return ap.parse_args()

# ---------- Main ----------
def main():
    args = parse_args()
    rng = np.random.default_rng(args.seed)

    # Lee BMP
    info, pix, prefix = read_bmp(args.in_bmp)
    orig_bits = bits_from_bytes(bytes(pix))

    # -------- Baseline sin ECC --------
    noisy_bits, flips = bsc_bits(orig_bits, args.p, rng)
    noisy_pix = bytearray(bytes_from_bits(noisy_bits)[:len(pix)])
    write_bmp(f"{args.outbase}_noecc.bmp", prefix, noisy_pix)

    # -------- ECC seleccionado --------
    if args.ecc == "none":
        print(f"[BASELINE] BSC p={args.p:.4g}  bits={len(orig_bits):,}  flips={flips:,}  BER={flips/len(orig_bits):.6e}")
        print(f"[OUT] {args.outbase}_noecc.bmp")
        return

    if args.ecc == "hamming74":
        ecc = Hamming74()
    elif args.ecc == "bch15":
        ecc = BCH15()
    else:
        ecc = Golay2312()

    # Encode (y guarda también los k-bits originales por bloque para métricas exactas)
    enc_bits, pad_k = ecc.pack_bits_to_blocks(orig_bits)

    nb_blocks = len(enc_bits)//ecc.n
    # reconstruimos los k bits originales por bloque
    tmp = orig_bits.copy()
    pad_k_for_orig = (-len(tmp)) % ecc.k
    if pad_k_for_orig:
        tmp = np.concatenate([tmp, np.zeros(pad_k_for_orig, dtype=np.uint8)])
    orig_msg_chunks = [tmp[i:i+ecc.k] for i in range(0, len(tmp), ecc.k)]
    orig_msg_chunks = orig_msg_chunks[:nb_blocks]
    if pad_k:
        # el último bloque solo tiene k-pad_k bits válidos
        orig_msg_chunks[-1] = orig_msg_chunks[-1][:ecc.k - pad_k]

    # Canal sobre los bloques codificados
    enc_noisy_bits, flips_ecc = bsc_bits(enc_bits, args.p, rng)
    flip_counts = blocks_hamming(enc_bits, enc_noisy_bits, ecc.n)

    # Decode (ahora devuelve también stats por bloque)
    msg_bits, corr_blocks, uncor, stats_rows = ecc.decode_stream(enc_noisy_bits, pad_k)

    # Reconstruye los k-bits decodificados por bloque para comparar exactitud bloque a bloque
    dec_msg_chunks = []
    idx = 0
    for bi in range(nb_blocks):
        valid_k = ecc.k if (bi < nb_blocks-1 or pad_k == 0) else (ecc.k - pad_k)
        dec_msg_chunks.append(msg_bits[idx:idx+valid_k])
        idx += valid_k

    # Completa CSV por bloque con exactitud/errores de mensaje y flips del canal
    exact_blocks = 0
    wrong_blocks = 0
    for i, row in enumerate(stats_rows):
        row["flips_in_block"] = int(flip_counts[i])
        exp_bits = orig_msg_chunks[i]
        got_bits = dec_msg_chunks[i]
        # mismatch en bits de mensaje (solo los válidos en el último bloque)
        m_err = int(np.sum(exp_bits != got_bits))
        row["msg_bit_errors"] = m_err
        row["is_exact"] = 1 if m_err == 0 else 0
        if m_err == 0:
            exact_blocks += 1
        else:
            wrong_blocks += 1

    # Reconstruye imagen a partir de bits de mensaje decodificados
    rec_pix = bytearray(bytes_from_bits(msg_bits)[:len(pix)])
    write_bmp(f"{args.outbase}_ecc.bmp", prefix, rec_pix)

    # Métricas globales
    total_bits = len(orig_bits)
    ber_noecc = flips/total_bits
    diff_after = np.bitwise_xor(bits_from_bytes(bytes(rec_pix)), orig_bits).sum()
    ber_after = diff_after/total_bits

    print(f"[ECC] {ecc.name}  n={ecc.n},k={ecc.k},t={ecc.t}, deg(g)={deg(ecc.g)}")
    print(f"[CANAL] BSC p={args.p:.4g}  seed={args.seed}")
    print(f"[IMG] {info.width}x{abs(info.height)} px, bytes_pix={len(pix):,}, bits={total_bits:,}")
    print(f"[NO ECC] flips={flips:,}  BER={ber_noecc:.6e}  -> out: {args.outbase}_noecc.bmp")
    print(f"[WITH ECC] encoded_blocks={nb_blocks}, flips_on_encoded={flips_ecc:,}")
    print(f"           corrected_blocks={corr_blocks:,}, uncorrectable_blocks={uncor:,}")
    print(f"[QUALITY]  exact_blocks={exact_blocks:,}, wrong_blocks={wrong_blocks:,}")
    print(f"           post-decode bit errors vs original = {diff_after:,}  (BER={ber_after:.6e})")
    print(f"[OUT] {args.outbase}_ecc.bmp")

    # CSV (opcional)
    if args.csv:
        fields = ["block","flips_in_block","syndrome_hex","syndrome_bits",
                  "err_weight","corrected","uncorrectable",
                  "is_exact","msg_bit_errors","valid_msg_bits"]
        write_block_csv(args.csv, stats_rows, fields)
        print(f"[CSV] Escrito informe por bloque: {args.csv}")

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"[ERROR] {e}", file=sys.stderr)
        sys.exit(1)
