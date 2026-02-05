#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse, math, random
from collections import Counter
import matplotlib.pyplot as plt

# ---------- Diccionarios de codificación ----------
CODE = {'A': '00', 'B': '01', 'C': '10'}  # '11' sin usar
DECODE = {v: k for k, v in CODE.items()}

# ---------- Funciones de entropía ----------
def binary_entropy(p: float) -> float:
    if p <= 0.0 or p >= 1.0: 
        return 0.0
    return -(p*math.log2(p) + (1-p)*math.log2(1-p))

def entropy_from_counts(counts: Counter) -> float:
    n = sum(counts.values())
    H = 0.0
    for c in counts.values():
        if c:
            p = c / n
            H -= p * math.log2(p)
    return H

# ---------- Canal y codificación ----------
def encode_string(s: str) -> str:
    return ''.join(CODE[ch] for ch in s)

def decode_bits(bits: str) -> str:
    if len(bits) % 2:
        raise ValueError("Longitud no múltiplo de 2.")
    out = []
    for i in range(0, len(bits), 2):
        pair = bits[i:i+2]
        out.append(DECODE.get(pair, '?'))  # '11' -> '?'
    return ''.join(out)

def bsc(bits: str, e: float, seed: int | None = None) -> str:
    if seed is not None:
        random.seed(seed)
    out = []
    for b in bits:
        flip = random.random() < e
        out.append(('1' if b == '0' else '0') if flip else b)
    return ''.join(out)

# ---------- Gráfica de capacidad con punto experimental ----------
def plot_capacity_curve(e_exp, I_emp):
    es = [i / 100 for i in range(0, 51)]  # de 0 a 0.5
    gammas = [1 - binary_entropy(e) for e in es]

    plt.figure(figsize=(6, 4))
    plt.plot(es, gammas, lw=2, color="navy", label=r"Capacidad Canal")

    # Añadimos el punto experimental
    plt.scatter([e_exp], [I_emp], color="red", s=80, zorder=5, label="Punto experimental")
    
    # Añadimos etiqueta con valores
    plt.text(e_exp + 0.015, I_emp + 0.02,
             f"e={e_exp:.2f}\nI={I_emp:.3f} bits",
             color="darkred", fontsize=9,
             bbox=dict(facecolor='white', alpha=0.7, boxstyle='round,pad=0.3'))

    # Configuración de la gráfica
    plt.title("Capacidad del Canal BSC")
    plt.xlabel("Probabilidad de error e")
    plt.ylabel("Capacidad γ = 1 - h(e) ")
    plt.grid(True, alpha=0.3)
    plt.legend(loc="upper right")

    # Asegura que el punto se vea (ajusta límites)
    ymin = min(0, I_emp - 0.05)
    ymax = max(1.05, I_emp + 0.1)
    plt.ylim(ymin, ymax)
    plt.xlim(0, 0.52)

    plt.tight_layout()
    plt.show()


# ---------- Informes ----------
def detailed_entropy(x_bits: str):
    print(f"\n    == Detalle del cálculo de H({x_bits}) ==")
    counts = Counter(x_bits)
    total = len(x_bits)
    H = 0.0
    print(f"{'x':>5} {'freq':>6} {'p(x)':>8} {'log2(1/p)':>12} {'p*log2(1/p)':>14}")
    for x in ('0', '1'):
        c = counts.get(x, 0)
        p = c / total if total else 0.0
        val = 0.0 if p == 0 else p * math.log2(1 / p)
        H += val
        lg = 0.0 if p == 0 else math.log2(1 / p)
        print(f"{x:>5} {c:6d} {p:8.4f} {lg:12.4f} {val:14.4f}")
    print(f"{'':>5} {'':6} {'':8} {'TOTAL ->':>12} {H:14.4f}")
    return H

def print_entropy_report(x_bits: str, y_bits: str, e_param: float):
    n = len(x_bits)
    cx, cy = Counter(x_bits), Counter(y_bits)
    cxy = Counter(zip(x_bits, y_bits))

    HX = entropy_from_counts(cx)
    HY = entropy_from_counts(cy)
    HXY = entropy_from_counts(cxy)
    HY_given_X_emp = HXY - HX
    I_emp = HX - HY_given_X_emp 

    print("\n== Entropías empíricas (bits) ==")
    print(f"\nH(X) o H(p)  = {HX:.4f}")

    detailed_entropy(x_bits)
    
    print(f"H(Y)  o H(q) = {HY:.4f}")
    detailed_entropy(y_bits)

    print(f"\n\nIncertidumbre total del sistema completo (entrada + salida).")     
    print(f"H(X,Y) o H(t).   = {HXY:.4f}")
    
    print(f"\n\nSi ya conozco la salida del canal Y, ¿cuánta duda me queda sobre cuál fue la entrada 𝑋?.")    
    print(f"H(Y|X) o H(p∣q) o H(Γ;p)  = {HY_given_X_emp:.4f}")
    print(f"\n\nInformación mutua entre entrada y salida del canal.")    
    print(f"fΓ(p)  = {I_emp:.4f}")

    # --- Teoría (Teoremas 2 y 4) ---
    p1 = x_bits.count('1') / n
    q1 = p1 * (1 - e_param) + (1 - p1) * e_param
    HX_th = binary_entropy(p1)
    HY_th = binary_entropy(q1)
    He = binary_entropy(e_param)
    Hgamma_th = HX_th + He - HY_th
    I_th =  HX_th - Hgamma_th
    C = 1 - He

    print("\n\n== Teoría BSC ==")
    print(f"p=({1-p1:.4f},{p1:.4f})  e={e_param:.4f}  q=({1-q1:.4f},{q1:.4f})")
    print(f"\nh(p)=H(X)={HX_th:.4f}   h(e)={He:.4f}   h(q)=H(Y)={HY_th:.4f}")
    
    print(f"\nH(Γ;p)=h(p)+h(e)-h(q) = {Hgamma_th:.4f}       fΓ(p)= H(p)-H(Γ;p)= {I_th:.4f}")
    print(f"\nCapacidad γ = 1 - h(e) = {C:.4f} bits/canal")

    print("\n--------\n")
      
    print(f"I_emp={I_emp:.4f}  → {'No excede la capacidad del canal' if I_emp <= C else '¡Excede Capacidad Canal!'}")

    # Mostrar curva + punto experimental
    plot_capacity_curve(e_param, I_emp)

# ---------- Matriz de confusión simbólica ----------
def confusion_matrix_symbols(ref: str, hyp: str):
    labels = ['A', 'B', 'C', '?']
    mat = {r: {h: 0 for h in labels} for r in labels}
    for r, h in zip(ref, hyp):
        mat[r][h] += 1
    print("\n== Matriz de confusión (símbolos) ==")
    header = "     " + " ".join(f"{l:>4}" for l in labels)
    print(header)
    for r in labels:
        row = f"{r:>4} " + " ".join(f"{mat[r][h]:>4}" for h in labels)
        print(row)


def print_bsc_matrix(x_bits: str, y_bits: str, e_param: float):
    # Conteos por pares (X,Y)
    from collections import Counter
    cxy = Counter(zip(x_bits, y_bits))
    n00 = cxy.get(('0','0'), 0)
    n01 = cxy.get(('0','1'), 0)
    n10 = cxy.get(('1','0'), 0)
    n11 = cxy.get(('1','1'), 0)

    n0 = n00 + n01  # veces que X=0
    n1 = n10 + n11  # veces que X=1

    # Probabilidades condicionales (filas normalizadas)
    p00 = n00 / n0 if n0 else 0.0
    p01 = n01 / n0 if n0 else 0.0
    p10 = n10 / n1 if n1 else 0.0
    p11 = n11 / n1 if n1 else 0.0

    # Matriz teórica del BSC(e)
    te00, te01 = (1 - e_param), e_param
    te10, te11 = e_param, (1 - e_param)

    print("\n== Matriz del canal estimada Γ̂ (condicional Y|X) ==")
    #print("  (filas: X=0, X=1; columnas: Y=0, Y=1)")
    print(f"conteos: [[{n00:4d}, {n01:4d}], \n          [{n10:4d}, {n11:4d}]]   (X=0:{n0}, X=1:{n1})")
    print(f"Γ̂ = [[{p00:6.4f}, {p01:6.4f}], \n     [{p10:6.4f}, {p11:6.4f}]]")

    # Comparación con la teórica
    print("\nMatriz BSC teórica con e parámetro:")
    print(f"Γ  = [[{te00:6.4f}, {te01:6.4f}], \n      [{te10:6.4f}, {te11:6.4f}]]")

    print(f"\nEl error total observado es aproximadamente { (n01 + n10) / (n0+n1) :.4f} vs e parámetro {e_param:.4f}")

# ---------- Main ----------
def run(msg: str, e: float, seed: int | None):
    x_bits = encode_string(msg)
    y_bits = bsc(x_bits, e, seed)
    decoded = decode_bits(y_bits)

    flips = sum(1 for xb, yb in zip(x_bits, y_bits) if xb != yb)
    print("\n\n== Parámetros ==")
    print(f"Mensaje: {msg} | e(BSC)={e:.3f} | seed={seed}")
    print("\n== Codificación ==")
    print("Mapa: A->00, B->01, C->10 ('11' → '?')")
    print(f"X bits ({len(x_bits)}): {x_bits}")
    print(f"Y bits ({len(y_bits)}): {y_bits}")
    print(f"Bit flips observados: {flips}/{len(x_bits)} (~{flips/len(x_bits):.4f})")
    print(f"\nEntrada decodificada : {msg}")
    print(f"Salida decodificada  : {decoded}")
    sym_err = sum(1 for a, b in zip(msg, decoded) if a != b)
    print(f"Errores de símbolo   : {sym_err}/{len(msg)} (~{sym_err/len(msg):.4f})")
    

    print_bsc_matrix(x_bits, y_bits, e)



    confusion_matrix_symbols(msg, decoded)
    print_entropy_report(x_bits, y_bits, e)
    

# ---------- Ejecución ----------
if __name__ == "__main__":
    ap = argparse.ArgumentParser(description="BSC demo con curva de capacidad y punto experimental.")
    ap.add_argument("msg", help="Cadena sobre {A,B,C}, p.ej. 'ABACAB'")
    ap.add_argument("-e", "--error", type=float, default=0.1, help="Probabilidad de error del BSC (0..1)")
    ap.add_argument("--seed", type=int, help="Semilla RNG")
    args = ap.parse_args()
    run(args.msg, args.error, args.seed)
