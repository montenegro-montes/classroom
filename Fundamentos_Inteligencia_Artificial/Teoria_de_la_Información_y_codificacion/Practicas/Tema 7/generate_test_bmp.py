#!/usr/bin/env python3
from PIL import Image, ImageDraw, ImageFont
import argparse
import sys

def parse_args():
    ap = argparse.ArgumentParser(description="Crea un BMP sin comprimir.")
    ap.add_argument("--size", type=int, default=512, help="Tamaño de la imagen.")
    ap.add_argument("--out", default="test_pattern.bmp", help="Fichero de salida.")

    return ap.parse_args()


def main():
    args = parse_args()
    size = args.size
    output = args.out

    generate_test_bmp(output, (size,size))
    

def generate_test_bmp(filename="test_pattern.bmp", size=(512, 512)):
    w, h = size
    img = Image.new("RGB", (w, h))
    draw = ImageDraw.Draw(img)

    # 4 franjas horizontales de colores
    bands = [
        (255, 0, 0),   # rojo
        (0, 255, 0),   # verde
        (0, 0, 255),   # azul
        (255, 255, 255) # blanco
    ]
    band_height = h // len(bands)
    for i, color in enumerate(bands):
        draw.rectangle([0, i * band_height, w, (i+1) * band_height], fill=color)

    # Cuadrícula para detectar errores
    for x in range(0, w, 8):
        draw.line((x, 0, x, h), fill=(0, 0, 0))
    for y in range(0, h, 8):
        draw.line((0, y, w, y), fill=(0, 0, 0))

   
    img.save(filename, "BMP")
    print(f"[OK] Imagen BMP de prueba guardada en: {filename}")

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"[ERROR] {e}", file=sys.stderr)
        sys.exit(1)
