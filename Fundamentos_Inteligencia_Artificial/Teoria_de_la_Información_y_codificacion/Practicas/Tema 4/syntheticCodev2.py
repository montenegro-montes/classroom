#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse, random, math, sys
from typing import List

def parse_probs(s: str) -> List[float]:
    try:
        vals = [float(x) for x in s.split(",")]
    except Exception:
        raise argparse.ArgumentTypeError("No se pudieron parsear las probabilidades (usa comas).")
    if any(p < 0 for p in vals):
        raise argparse.ArgumentTypeError("Probabilidades negativas no permitidas.")
    total = sum(vals)
    if total <= 0:
        raise argparse.ArgumentTypeError("La suma de probabilidades debe ser > 0.")
    # Normalizamos si no suman exactamente 1 (tolerancia conveniente)
    if abs(total - 1.0) > 1e-9:
        vals = [p / total for p in vals]
        print("⚠️  Aviso: probabilidades normalizadas para sumar 1.", file=sys.stderr)
    return vals

def entropy(p):
    return -sum(pi * math.log2(pi) for pi in p if pi > 0)

def main():
    ap = argparse.ArgumentParser(
        description="Genera un texto sintético con alfabeto pequeño y control de entropía."
    )
    ap.add_argument("-o", "--output", default="synthetic_alphabet.txt", help="Fichero de salida (UTF-8).")
    ap.add_argument("-n", "--length", type=int, default=20000, help="Longitud del texto a generar.")
    ap.add_argument("--alphabet", default="ABCDE",
                    help="Alfabeto a usar (cadena con símbolos distintos). Ej: 'ABCDE'.")
    ap.add_argument("--seed", type=int, default=1234, help="Semilla RNG para reproducibilidad.")

    grp = ap.add_mutually_exclusive_group()
    grp.add_argument("--preset", choices=["max","mid","min"],
                     help="max=uniforme (máxima entropía), mid=media, min=muy sesgada (mínima entropía).")
    grp.add_argument("--probs", type=parse_probs,
                     help="Probabilidades separadas por comas (se normalizan si no suman 1). Ej: 0.4,0.25,0.2,0.1,0.05")

    ap.add_argument("--show-stats", action="store_true",
                    help="Muestra frecuencias teóricas, observadas y entropía.")
    args = ap.parse_args()

    # Alfabeto
    alphabet = list(args.alphabet)
    k = len(alphabet)
    if k < 2:
        raise SystemExit("El alfabeto debe tener al menos 2 símbolos.")
    if len(set(alphabet)) != k:
        raise SystemExit("El alfabeto contiene símbolos repetidos.")

    # Probabilidades
    if args.probs is not None:
        probs = args.probs
        if len(probs) != k:
            raise SystemExit(f"Nº de probabilidades ({len(probs)}) != tamaño del alfabeto ({k}).")
    else:
        # Presets en función de k
        if args.preset == "max":
            probs = [1.0 / k] * k
        elif args.preset == "min":
            # una muy grande y el resto pequeñas (suma 1)
            big = 0.96 if k >= 5 else 0.90
            rest = (1.0 - big) / (k - 1)
            probs = [big] + [rest] * (k - 1)
        else:
            # "mid" por defecto: si k==5 usa el ejemplo clásico; si no, una distribución razonable
            if k == 5:
                probs = [0.40, 0.25, 0.20, 0.10, 0.05]
            else:
                # distribución en rampa decreciente y normalizada
                weights = list(reversed(range(1, k + 1)))
                total = sum(weights)
                probs = [w / total for w in weights]

    # Validación final
    if len(probs) != k:
        raise SystemExit(f"Nº de probabilidades ({len(probs)}) != tamaño del alfabeto ({k}).")
    if any(p < 0 for p in probs):
        raise SystemExit("Todas las probabilidades deben ser >= 0.")
    s = sum(probs)
    if abs(s - 1.0) > 1e-9:
        probs = [p / s for p in probs]  # normaliza por si acaso

    # Generación
    random.seed(args.seed)
    data = ''.join(random.choices(alphabet, probs, k=args.length))

    with open(args.output, "w", encoding="utf-8") as f:
        f.write(data)

    print(f"✅ Generado {args.output} con {args.length} símbolos.")
    if args.show_stats:
        H = entropy(probs)
        print("\n— Configuración —")
        print("Alfabeto:", "".join(alphabet))
        print("Probabilidades teóricas:")
        for a, p in zip(alphabet, probs):
            print(f"  {a}: {p:.6f} ({p*100:.2f}%)")
        print(f"Entropía teórica H = {H:.4f} bits/símbolo")

        # Observadas
        from collections import Counter
        cnt = Counter(data)
        print("\nFrecuencias observadas:")
        for a in alphabet:
            p_obs = cnt[a] / args.length
            print(f"  {a}: {cnt[a]:6d}  ({p_obs*100:6.2f}%)")
        H_obs = entropy([cnt[a]/args.length for a in alphabet if cnt[a] > 0])
        print(f"\nEntropía observada H_obs = {H_obs:.4f} bits/símbolo")

if __name__ == "__main__":
    main()
