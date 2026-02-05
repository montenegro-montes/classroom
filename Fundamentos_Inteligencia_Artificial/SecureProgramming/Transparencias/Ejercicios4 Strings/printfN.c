#include <stdio.h>
#include <stdlib.h>

	void funcion(char *cadena){

	 int n_chars = 0;
         printf("%s%n", cadena, &n_chars);
	 printf("\nN %d\n",n_chars);
	}


  int main( int argc, char *argv[] ){

	int n_chars = 0;

	funcion("Hola Mundo");
	funcion("Prueba");
	return 0;
  }
