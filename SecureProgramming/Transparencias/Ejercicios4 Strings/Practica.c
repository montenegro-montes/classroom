#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int main(int argc, char **argv){
        char buf[1024];
        strcpy(buf, argv[1]);
        printf(buf);
        printf("\n");
        exit(0);
}
