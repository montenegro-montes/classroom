#!/usr/bin/env python3
# hamming.py — Hamming(2^m-1, 2^m-1-m): H, encode, decode (1-bit correction)
# Uso:
#   python3 hamming.py 4
#   python3 hamming.py 4 --encode 10110011001
#   python3 hamming.py 4 --random --seed 123
#   python3 hamming.py 4 --random --flip-rand         # 1 bit aleatorio
#   python3 hamming.py 4 --random --flip-rand 3       # 3 bits aleatorios
#   python3 hamming.py 4 --encode 10110011001 --flip 7
#   python3 hamming.py 4 --decode 011001110011001
#   python3 hamming.py 4 --selftest --tries 50 --seed 1

import sys
import numpy as np
import random

# ----------- básicos -----------
def compute_n_k(m: int):
    if m < 2:
        raise ValueError("m must be >= 2")
    n = 2**m - 1
    k = n - m
    return n, k

def build_H(m: int) -> np.ndarray:
    """
    Construye H con convención INTERNA LSB-first:
      - fila 0 = LSB, fila m-1 = MSB
      - columna j = bits de j (1..n)
    """
    n, _ = compute_n_k(m)
    H = np.zeros((m, n), dtype=int)
    for j in range(1, n + 1):
        for bit in range(m):  # bit=0 es LSB
            H[bit, j - 1] = (j >> bit) & 1
    return H

def parity_positions(m: int):
    return [1 << i for i in range(m)]  # 1,2,4,8,...

def data_positions(n: int, par_pos):
    return [i for i in range(1, n + 1) if i not in par_pos]

# ----------- utilidades bits -----------
def bits_from_bin_str(s: str) -> list[int]:
    s = s.strip().replace("_", "").replace(" ", "")
    if not s or any(ch not in "01" for ch in s):
        raise ValueError("binary string must contain only 0/1")
    return [int(ch) for ch in s]

def to_bin_str(bits: list[int]) -> str:
    return "".join(str(b) for b in bits)

def random_bits(k: int) -> list[int]:
    return [random.getrandbits(1) for _ in range(k)]

# ----------- encoder -----------
def encode_hamming(m: int, data_bits: list[int]) -> list[int]:
    n, k = compute_n_k(m)
    if len(data_bits) != k:
        raise ValueError(f"data must have {k} bits for m={m}")
    par_pos = parity_positions(m)
    dat_pos = data_positions(n, par_pos)
    c = [0] * (n + 1)  # índice 1..n
    for bit, pos in zip(data_bits, dat_pos):
        c[pos] = bit
    # Paridad par: para cada p, XOR de posiciones con ese bit activo
    for p in par_pos:
        acc = 0
        for i in range(1, n + 1):
            if i & p:
                acc ^= c[i]
        c[p] = acc
    return c[1:]

def extract_data_bits_from_code(m: int, code_bits: list[int]) -> list[int]:
    n, k = compute_n_k(m)
    if len(code_bits) != n:
        raise ValueError(f"code must have {n} bits for m={m}")
    par_pos = parity_positions(m)
    return [code_bits[i - 1] for i in data_positions(n, par_pos)]

# ----------- síndrome y decodificación -----------
def syndrome(H: np.ndarray, code_bits: list[int]) -> np.ndarray:
    v = np.array(code_bits, dtype=int).reshape(-1, 1)
    s = (H @ v) % 2
    return s.flatten()  # **LSB-first** interno

def syndrome_to_position(s: np.ndarray) -> int:
    # Interpreta s como LSB-first (s[0] = 2^0)
    pos = 0
    for i, b in enumerate(s.tolist()):
        if b:
            pos += (1 << i)
    return pos  # 0 = sin error; 1..n = posición errónea

def decode_hamming(m: int, code_bits: list[int]):
    H = build_H(m)
    s = syndrome(H, code_bits)
    err_pos = syndrome_to_position(s)
    corrected = code_bits[:]
    corrected_pos = None
    if err_pos != 0:
        if 1 <= err_pos <= len(code_bits):
            corrected[err_pos - 1] ^= 1
            corrected_pos = err_pos
    data_bits = extract_data_bits_from_code(m, corrected)
    return {
        "syndrome": s.tolist(),
        "error_position": corrected_pos,  # None si no hay error
        "corrected_code": corrected,
        "data_bits": data_bits,
    }

# ----------- impresión -----------
def pretty_print_H(H: np.ndarray):
    """
    Imprime H con **MSB arriba** solo para visualización,
    manteniendo la convención interna LSB-first.
    """
    m, n = H.shape
    H_print = H[::-1, :]  # invertimos filas para mostrar MSB arriba
    print("\nH (columnas 1..n):")
    header = "     " + " ".join(f"{j:>2}" for j in range(1, n + 1))
    #print(header)
    for i in range(m):
        print(f"r{i+1:>2}:  " + " ".join(str(int(x)) for x in H_print[i, :]))

# ----------- self-test -----------
def selftest(m: int, tries: int, seed: int | None = None) -> bool:
    """
    Para cada intento:
      - genera data aleatorio
      - codifica, y para cada posición p en 1..n:
          voltea p, decodifica y comprueba que error_position == p
    """
    if seed is not None:
        random.seed(seed)
        np.random.seed(seed)
    n, k = compute_n_k(m)
    ok = True
    for t in range(tries):
        data = random_bits(k)
        code = encode_hamming(m, data)
        for p in range(1, n + 1):
            code_err = code[:]
            code_err[p - 1] ^= 1
            res = decode_hamming(m, code_err)
            if res["error_position"] != p:
                print(f"[FAIL] try={t} pos={p} -> got {res['error_position']}, expected {p}  "
                      f"syndrome={''.join(map(str, res['syndrome']))}")
                ok = False
                return ok
    print(f"[OK] selftest passed for m={m} with {tries} random messages.")
    return ok

# ----------- CLI -----------
def main(argv):
    if len(argv) < 2:
        print("Usage: python3 hamming.py <m> "
              "[--encode <kbits> | --random] "
              "[--seed <int>] "
              "[--flip <pos> | --flip-rand [count]] "
              "[--decode <nbits>] "
              "[--selftest [--tries N]]")
        sys.exit(1)

    m = int(argv[1])
    n, k = compute_n_k(m)
    H = build_H(m)

    print(f"m = {m}")
    print(f"n = 2^{m} - 1 = {n}")
    print(f"k = n - m = {k}")
    pretty_print_H(H)

    # Semilla opcional
    seed_val = None
    if "--seed" in argv:
        sidx = argv.index("--seed")
        seed_val = int(argv[sidx + 1])
        random.seed(seed_val)
        np.random.seed(seed_val)
        print(f"\n[seed set to {seed_val}]")

    # Self-test opcional
    if "--selftest" in argv:
        tries = 20
        if "--tries" in argv:
            tidx = argv.index("--tries")
            tries = int(argv[tidx + 1])
        ok = selftest(m, tries, seed_val)
        if not ok:
            sys.exit(2)

    # Datos de entrada: --encode o --random
    data_bits = None
    if "--encode" in argv:
        idx = argv.index("--encode")
        data_bits = bits_from_bin_str(argv[idx + 1])
        if len(data_bits) != k:
            if len(data_bits) < k:
                data_bits = [0] * (k - len(data_bits)) + data_bits
            else:
                raise ValueError(f"Data has {len(data_bits)} bits, but k={k}")
        print("\n== Encoding input ==")
        print(f"data ({k} bits):   {to_bin_str(data_bits)}")

    elif "--random" in argv:
        data_bits = random_bits(k)
        print("\n== Encoding input (random) ==")
        print(f"data ({k} bits):   {to_bin_str(data_bits)}")

    # Si tenemos datos, codificamos y opcionalmente introducimos errores
    if data_bits is not None:
        code = encode_hamming(m, data_bits)
        print(f"code ({n} bits):   {to_bin_str(code)}")

        # Errores: --flip o --flip-rand
        error_positions = []
        if "--flip" in argv:
            fidx = argv.index("--flip")
            pos = int(argv[fidx + 1])
            if not (1 <= pos <= n):
                raise ValueError(f"--flip position must be in 1..{n}")
            code[pos - 1] ^= 1
            error_positions.append(pos)

        elif "--flip-rand" in argv:
            ridx = argv.index("--flip-rand")
            # valor opcional "count"
            count = 1
            if ridx + 1 < len(argv) and argv[ridx + 1].isdigit():
                count = int(argv[ridx + 1])
            count = max(1, min(count, n))
            error_positions = random.sample(range(1, n + 1), count)
            for p in error_positions:
                code[p - 1] ^= 1

        if error_positions:
            error_positions.sort()
            print(f"* Flipped positions: {error_positions}")
            print(f"code with errors: {to_bin_str(code)}")
            if len(error_positions) > 1:
                print("! Warning: Hamming corrige solo 1 error; resultado puede no corregirse completamente.")
                return 
                    
        # Decodificar lo que tengamos (con o sin errores)
        res = decode_hamming(m, code)
        s = "".join(map(str, res["syndrome"]))
        print("\n== Decoding ==")
        print(f"syndrome: {s}  (LSB-first)")
        if res["error_position"] is None:
            if error_positions:
                print("no single-bit correction detected (posible múltiple error)")
            else:
                print("no error detected")
        else:
            print(f"single-bit error at position {res['error_position']} (corrected)")
        print(f"code with errors: {to_bin_str(code)}")    
        print(f"corrected code:   {to_bin_str(res['corrected_code'])}")
        print(f"recovered data ({k} bits): {to_bin_str(res['data_bits'])}")

    # Decodificación directa de una palabra de n bits
    if "--decode" in argv:
        idx = argv.index("--decode")
        code_bits = bits_from_bin_str(argv[idx + 1])
        if len(code_bits) != n:
            raise ValueError(f"Provided code has {len(code_bits)} bits, expected {n}")
        res = decode_hamming(m, code_bits)
        s = "".join(map(str, res["syndrome"]))
        print("\n== Decoding (direct) ==")
        print(f"input code:   {to_bin_str(code_bits)}")
        print(f"syndrome:     {s}  (LSB-first)")
        if res["error_position"] is None:
            print("no error detected (or multiple errors not correctable)")
        else:
            print(f"single-bit error at position {res['error_position']} (corrected)")
        print(f"corrected:    {to_bin_str(res['corrected_code'])}")
        print(f"data ({k}):   {to_bin_str(res['data_bits'])}")

if __name__ == "__main__":
    main(sys.argv)
