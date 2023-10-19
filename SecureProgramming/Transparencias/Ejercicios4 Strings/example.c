#include <stdio.h>
#include <stdlib.h>
#include <string.h>


int main(){

	int i=1001;
	char A=65, h[10];
	strcpy (h,"Hola");

	printf("Enteros: (%d) (%5d) (%x)  (%5x)\n", i,i,i,i);
	printf("Caracter: %c; String %s\n", A, h);
	printf("Puntero: %p\n", h);


	printf("Sin argumentos: %x %x %x\n");

}
