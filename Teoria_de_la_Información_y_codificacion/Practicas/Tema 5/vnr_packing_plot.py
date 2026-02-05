
#!/usr/bin/env python3
import argparse
import numpy as np
import matplotlib.pyplot as plt
from math import comb

def V(n, r):
    return sum(comb(n, i) for i in range(r+1))

def compute_series(n):
    rs = np.arange(0, n+1)
    Vnr = np.array([V(n, int(r)) for r in rs], dtype=float)
    C_upper = (2.0**n) / Vnr
    return rs, Vnr, C_upper

def main():
    p = argparse.ArgumentParser(description="Plot V(n,r) and packing bound |C| for given n; highlight r.")
    p.add_argument("n", type=int, help="block length n")
    p.add_argument("-r", "--highlight", type=int, default=None, help="highlight this r")
    p.add_argument("--annotate-all", action="store_true", help="annotate all bars with |C| upper bound")
    #p.add_argument("-o", "--output", default=None, help="output PNG path")
    args = p.parse_args()
    n = args.n
    r_highlight = args.highlight
    rs, Vnr, C_up = compute_series(n)
    colors = ['#888888'] * len(rs)
    if r_highlight is not None and 0 <= r_highlight <= n:
        colors[r_highlight] = '#d62728'
    plt.figure()
    plt.bar(rs, Vnr, color=colors)
    plt.title(f"V(n,r) y cota |C| (n={n})")
    plt.xlabel("r (errores correguibles)")
    plt.ylabel("V(n,r)")
    plt.yscale('log')
    #plt.grid(True, axis='y', which='both')
    if args.annotate_all or n <= 12:
        for i, (x, y, cup) in enumerate(zip(rs, Vnr, C_up)):
            label = f"|C|≤{cup:.0f}" if cup < 1e5 else f"|C|≤{cup:.2e}"
            plt.text(x, y*1.05, label, rotation=90, ha='center', va='bottom', fontsize=8)
    elif r_highlight is not None and 0 <= r_highlight <= n:
        x = r_highlight
        y = Vnr[r_highlight]
        cup = C_up[r_highlight]
        label = f"|C|≤{cup:.0f}" if cup < 1e7 else f"|C|≤{cup:.2e}"
        plt.text(x, y*1.05, label, rotation=90, ha='center', va='bottom', fontsize=10, fontweight='bold')
    plt.tight_layout()
    #out = args.output or f"Vnr_plot_n{n}_r{r_highlight if r_highlight is not None else 'none'}.png"
    #plt.savefig(out, dpi=180)
    plt.show()

if __name__ == "__main__":
    main()
