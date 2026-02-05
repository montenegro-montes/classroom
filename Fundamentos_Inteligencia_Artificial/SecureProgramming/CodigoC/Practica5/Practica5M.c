#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>

struct data {
 char name[64];
};

struct fp {
 void (*fp)();
};

int ganadora()
{
 printf("nivel superado\n");
}

void perdedora()
{
 printf("nivel no ha sido superado\n");
}

int main(int argc, char **argv)
{
 struct data *d;
 struct fp *f;

 d = malloc(sizeof(struct data));
 f = malloc(sizeof(struct fp));
 f->fp = perdedora;

 printf("data esta en %p, fp esta en %p, %p\n", d, f, f->fp);
 printf("Buffer: %d Estructura: %d Funcion: %d Puntero función: %d \n", sizeof(d->name), sizeof(d), sizeof(f),sizeof(f->fp));

 strcpy(d->name, argv[1]);

 f->fp();

}
