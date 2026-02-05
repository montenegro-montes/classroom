#include <string.h>
#include <stdio.h>
#include <stdlib.h>

int bufferOverflow(char *name, char *cmd);

int  main(){
            char campeon[200];
            printf("¿Quien ganara la champion?\n");
            scanf("%s", campeon);
            bufferOverflow(campeon, "date");
}

 int bufferOverflow(char *name, char *cmd){
   char comando[11];
   char buffer[11];
   printf("Direccion buffer comando : %x\n", comando);
   printf("Direccion buffer equipo: %x\n", buffer);
   strcpy(comando, cmd);
   strcpy(buffer, name);
   printf("Enhorabuena acertaste, %s!\n", buffer);
   printf("Ejecutando  comando: %s\n", comando);

   system(comando);


}
