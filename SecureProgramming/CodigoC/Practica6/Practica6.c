#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <time.h>

struct estructura {
 int prioridad;
 char *nombre;
};

void ganadora()
{
 printf("tenemos un ganador  @ %d\n", time(NULL));
}

int main(int argc, char **argv)
{
 struct estructura  *i1, *i2;

 i1 = malloc(sizeof(struct estructura));
 i1->prioridad = 1;
 i1->nombre = malloc(8);

 i2 = malloc(sizeof(struct estructura));
 i2->prioridad = 2;
 i2->nombre = malloc(8);

 strcpy(i1->nombre, argv[1]);
 strcpy(i2->nombre, argv[2]);

 printf("y eso es todo amigos!\n");
}
