#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
hamming_neighborhoods.py
Dado m, calcula n=2^m-1, k=n-m y trabaja con el código Hamming(n,k):
- Genera codewords (todas o una muestra).
- Construye vecindarios de radio 1 (centro + n vecinos).
- Pinta una cuadrícula con los vecindarios seleccionados.
- Exporta una tabla CSV con los miembros de cada vecindario.

Uso ejemplos:
  python3 hamming_neighborhoods.py --m 3 --png ham7.png --csv ham7.csv
  python3 hamming_neighborhoods.py --m 4 --sample 25 --seed 1 --png sample.png
  python3 hamming_neighborhoods.py --m 4 --codeword 011001110011001 --png uno.png
  python3 hamming_neighborhoods.py --m 4 --csv all.csv --no-plot --all

Por defecto pinta hasta 16 vecindarios (o los que indiques con --sample).
"""

import math
import csv
import argparse
import random
from typing import List, Dict, Iterable, Tuple, Optional

import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

# ---------- utilidades básicas ----------

def compute_n_k(m: int) -> Tuple[int, int]:
    if m < 2:
        raise ValueError("m debe ser >= 2")
    n = (1 << m) - 1
    k = n - m
    return n, k

def parity_positions(m: int) -> List[int]:
    # posiciones 1,2,4,8,...,2^(m-1)
    return [1 << i for i in range(m)]

def data_positions(n: int, par_pos: List[int]) -> List[int]:
    return [i for i in range(1, n + 1) if i not in par_pos]

def encode_hamming(m: int, data_bits: List[int]) -> List[int]:
    """Codifica datos (k bits) colocando paridades en 1,2,4,.. y usando paridad par."""
    n, k = compute_n_k(m)
    if len(data_bits) != k or any(b not in (0, 1) for b in data_bits):
        raise ValueError(f"data_bits debe tener {k} bits 0/1 para m={m}")
    ppos = parity_positions(m)
    dpos = data_positions(n, ppos)
    c = [0] * (n + 1)               # 1-based
    for b, pos in zip(data_bits, dpos):
        c[pos] = b
    # paridades
    for p in ppos:
        acc = 0
        for i in range(1, n + 1):
            if i & p:
                acc ^= c[i]
        c[p] = acc
    return c[1:]                     # 0 no usado

def bits_to_str(bits: List[int]) -> str:
    return "".join("1" if b else "0" for b in bits)

def str_to_bits(s: str) -> List[int]:
    s = s.strip().replace(" ", "").replace("_", "")
    if not s or any(ch not in "01" for ch in s):
        raise ValueError("codeword/binario debe contener solo 0/1")
    return [1 if ch == "1" else 0 for ch in s]

def flip(vec: List[int], pos1: int) -> List[int]:
    v = vec[:]
    v[pos1 - 1] ^= 1
    return v

# ---------- generación de codewords ----------

def all_codewords(m: int) -> Iterable[List[int]]:
    """Genera todas las 2^k palabras codificadas (orden ascendente del mensaje)."""
    n, k = compute_n_k(m)
    for x in range(1 << k):
        data = [(x >> (k - 1 - i)) & 1 for i in range(k)]  # MSB..LSB
        yield encode_hamming(m, data)

def sampled_codewords(m: int, sample: int, seed: Optional[int]) -> List[List[int]]:
    """Toma 'sample' codewords al azar (sin reemplazo) de las 2^k posibles."""
    n, k = compute_n_k(m)
    total = 1 << k
    if sample >= total:
        return list(all_codewords(m))
    rng = random.Random(seed)
    # muestreamos índices de mensajes y codificamos
    idxs = rng.sample(range(total), sample)
    idxs.sort()
    cws = []
    for x in idxs:
        data = [(x >> (k - 1 - i)) & 1 for i in range(k)]
        cws.append(encode_hamming(m, data))
    return cws

# ---------- vecindarios ----------

def neighborhood(cw: List[int]) -> List[str]:
    """Devuelve [centro + n flips] como strings ordenados (incluye el centro)."""
    n = len(cw)
    members = {bits_to_str(cw)}
    for p in range(1, n + 1):
        members.add(bits_to_str(flip(cw, p)))
    return sorted(members)

def neighborhoods_map(codewords: Iterable[List[int]]) -> Dict[str, List[str]]:
    return {bits_to_str(cw): neighborhood(cw) for cw in codewords}

# ---------- dibujo ----------

def _auto_sizes(m: int):
    # tamaños agradables según m (m=3,4,5..)
    center_sz = max(6.0, 11.0 - m)       # 8..6
    neigh_sz  = max(2.0, 4.5 - 0.5*m)    # 3.0 (m=3) -> 2.0 (m>=5)
    label_fs  = max(7, 11 - m)           # 8..7
    cell_pad  = 0.06
    return center_sz, neigh_sz, label_fs, cell_pad

def _auto_rings(n: int, max_per_ring: int = 18) -> tuple[list[int], list[float]]:
    """
    Reparte n vecinos en 'rings' anillos con como mucho max_per_ring por anillo.
    Devuelve (counts, radii).
    """
    import math
    rings = max(1, math.ceil(n / max_per_ring))
    base = n // rings
    extra = n % rings
    counts = [base + (1 if i < extra else 0) for i in range(rings)]
    # radios entre 0.22 y 0.36
    if rings == 1:
        radii = [0.28]
    else:
        lo, hi = 0.22, 0.36
        radii = [lo + (hi - lo) * i / (rings - 1) for i in range(rings)]
    return counts, radii

def plot_grid(neigh_keys: list[str], out_png: str, m_hint: int | None = None):
    """
    Dibuja una cuadrícula automática:
      - 2 filas si hay más de 8 vecindarios; si no, cuadrada aprox.
      - vecinos repartidos automáticamente en anillos para evitar solapamiento.
      - tamaños de marcadores y fuente auto-escalados por m.
    """
    import math
    if not neigh_keys:
        return
    s = len(neigh_keys)

    # grid: dos filas si s>8 (tu petición), si no cuadrada
    if s > 8:
        rows, cols = 2, math.ceil(s / 2)
    else:
        cols = math.ceil(math.sqrt(s))
        rows = math.ceil(s / cols)

    # n y m (si no lo pasan, se infiere de la longitud del codeword)
    n = len(neigh_keys[0])
    m = m_hint if m_hint is not None else int(math.log2(n + 1))

    center_sz, neigh_sz, label_fs, cell_pad = _auto_sizes(m)
    counts, radii = _auto_rings(n)

    # figura
    import matplotlib.pyplot as plt
    from matplotlib.patches import Rectangle
    fig_w = max(6, cols * 2.2)
    fig_h = max(6, rows * 2.2)
    fig, ax = plt.subplots(figsize=(fig_w, fig_h))

    cell_w = cell_h = 1.0
    ax.add_patch(Rectangle((0, 0), cols * cell_w, rows * cell_h, fill=False))
    for r in range(rows):
        for c in range(cols):
            x = c * cell_w
            y = (rows - 1 - r) * cell_h
            ax.add_patch(Rectangle((x, y), cell_w, cell_h, fill=False))

    # dibujar cada celda
    import math as _math
    for idx, cw_str in enumerate(neigh_keys):
        r = idx // cols
        c = idx % cols
        cx = c * cell_w + cell_w / 2.0
        cy = (rows - 1 - r) * cell_h + cell_h / 2.0

        # centro
        ax.plot(cx, cy, marker="o", markersize=center_sz)

        # vecinos por anillos
        for cnt, radius in zip(counts, radii):
            if cnt <= 0:
                continue
            for k in range(cnt):
                theta = 2 * _math.pi * (k / cnt)
                px = cx + radius * _math.cos(theta)
                py = cy + radius * _math.sin(theta)
                ax.plot(px, py, marker="o", markersize=neigh_sz)

        # etiqueta
        ax.text(c * cell_w + cell_pad, (rows - 1 - r) * cell_h + cell_pad,
                cw_str, fontsize=label_fs, ha="left", va="bottom")

    ax.set_xlim(0, cols * cell_w)
    ax.set_ylim(0, rows * cell_h)
    ax.set_aspect("equal", adjustable="box")
    ax.axis("off")

    fig.savefig(out_png, dpi=220, bbox_inches="tight")
    plt.close(fig)


# ---------- CSV ----------

def save_csv(neigh: Dict[str, List[str]], out_csv: str):
    rows = []
    for center in sorted(neigh.keys()):
        members = neigh[center]
        rows.append({
            "codeword": center,
            "neighborhood_size": len(members),
            "members": " ".join(members)
        })
    with open(out_csv, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=["codeword", "neighborhood_size", "members"])
        w.writeheader()
        w.writerows(rows)

# ---------- CLI ----------

def parse_args():
    ap = argparse.ArgumentParser(description="Vecindarios de Hamming(2^m-1, 2^m-1-m)")
    ap.add_argument("--m", type=int, required=True, help="parámetro m (>=2)")
    ap.add_argument("--png", type=str, default="neighborhoods.png", help="ruta de imagen de salida")
    ap.add_argument("--csv", type=str, default="neighborhoods.csv", help="ruta CSV (si se desea exportar)")
    ap.add_argument("--sample", type=int, default=16, help="nº de vecindarios a dibujar/exportar")
    ap.add_argument("--seed", type=int, default=None, help="semilla de aleatoriedad para el muestreo")
    ap.add_argument("--codeword", type=str, default=None,
                    help="si se indica, pinta/expone únicamente el vecindario de este codeword (cadena 0/1 de longitud n)")
    ap.add_argument("--all", action="store_true", help="exportar TODOS los vecindarios al CSV (ignora --sample)")
    ap.add_argument("--no-plot", dest="no_plot", action="store_true",
                    help="no generar imagen (solo CSV/console)")    
    return ap.parse_args()

def main():
    args = parse_args()
    n, k = compute_n_k(args.m)
    total_vecindarios = 1 << k  # 2^k
    print(f"m={args.m}  n=2^{args.m}-1={n}  k=n-m={k}  codewords=2^{k}={total_vecindarios}")

    # --- Selección de codewords para trabajar ---
    if args.codeword:
        # Vecindario de un codeword específico
        cw_bits = str_to_bits(args.codeword)
        if len(cw_bits) != n:
            raise ValueError(f"--codeword debe tener longitud n={n}")
        sel = [cw_bits]
        muestreados = 1
        completo = False
    else:
        # ¿Todos o muestra?
        if args.all or (args.sample is not None and args.sample >= total_vecindarios):
            # ¡Cuidado con tamaños grandes!
            sel = list(all_codewords(args.m))
            muestreados = total_vecindarios
            completo = True
        else:
            # Muestra: valor por defecto si no se pasa --sample
            if args.sample is None:
                # política por defecto según m para que salga bien en la figura
                if args.m == 3:
                    sample = 16
                elif args.m == 4:
                    sample = 100
                else:
                    sample = 10
            else:
                sample = args.sample
            sample = min(sample, total_vecindarios)
            sel = sampled_codewords(args.m, sample, args.seed)
            muestreados = len(sel)
            completo = (muestreados == total_vecindarios)

            if sample>100:
                print(f"Demasiados vecinos: {sample}. El máximo es 100.")
                return 

    # --- Construir vecindarios ---
    neigh = neighborhoods_map(sel)

    # --- CSV (opcional) ---
    if args.csv:
        save_csv(neigh, args.csv)
        print(f"[CSV] guardado en: {args.csv}  (vecindarios: {len(neigh)})")

    # --- Imagen (opcional) ---
    if not args.no_plot:
        plot_grid(list(neigh.keys()), args.png, m_hint=args.m)
        print(f"[PNG] guardado en: {args.png}  (vecindarios dibujados: {len(neigh)})")

    # --- Resumen global ---
    if args.codeword:
        print(f"\n[INFO] Total de vecindarios posibles (2^{k}) : {total_vecindarios:,}")
        print(f"[INFO] Vecindarios generados/muestreados: 1 (vecindario para el codeword especificado)")
    elif completo:
        print(f"\n[INFO] Se han generado TODOS los vecindarios ({total_vecindarios:,}).")
    else:
        porcentaje = (muestreados / total_vecindarios) * 100.0
        print(f"\n[INFO] Total de vecindarios posibles (2^{k}): {total_vecindarios:,}")
        print(f"[INFO] Vecindarios generados /muestreados: {muestreados} ({porcentaje:.2f}% del total)")

    # --- Resumen por consola (primeros) ---
    print("\nResumen (hasta 5):")
    for i, (center, members) in enumerate(sorted(neigh.items())):
        if i >= 5:
            print("... (ver CSV para el listado completo)")
            break
        print(f"  {center} | size={len(members)} | members={', '.join(members)}")

if __name__ == "__main__":
    main()
