import random

# Definición del alfabeto y sus probabilidades
alphabet = ['A', 'B', 'C', 'D', 'E']
probs = [0.40, 0.25, 0.20, 0.10, 0.05]

# Longitud del texto
length = 20000

# Generación reproducible
random.seed(1234)
data = ''.join(random.choices(alphabet, probs, k=length))

# Guardar en archivo
with open("synthetic_alphabet5.txt", "w", encoding="utf-8") as f:
    f.write(data)

print("✅ Generado synthetic_alphabet5.txt con", length, "símbolos.")
print("Frecuencias teóricas:")
for a,p in zip(alphabet,probs):
    print(f"  {a}: {p*100:.1f}%")
