#include <stdio.h>
#include <string.h>


  void funcion(char *str) {
	char buffer[100];
   }

    void main() {
            register int i asm("esp");
            register int j asm("ebp");
            printf("$esp = %#010x - $ebp = %#010x \n", i,j);
	    printf("Main = %#010x - Funcion = %#010x \n",main,funcion);
}
