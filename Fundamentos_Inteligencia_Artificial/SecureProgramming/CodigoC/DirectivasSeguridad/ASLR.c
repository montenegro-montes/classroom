#include <stdio.h>
    void main() {
            register int i asm("esp");
            register int j asm("ebp");
            printf("$esp = %#010x - $ebp = %#010x \n", i,j);
}
