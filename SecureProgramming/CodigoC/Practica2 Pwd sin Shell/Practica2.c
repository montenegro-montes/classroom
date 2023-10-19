#include <stdio.h>

int test_pw()
{
        char pin[10];
        int x=15, i;
        printf("Enter password: ");
        gets(pin);
        for (i=0; i<10; i+=2) x = (x & pin[i]) | pin[i+1];
        if (x == 48) return 0;
        else return 1;
}

void main()
{
        if (test_pw()) printf("Fail!\n");
        else printf("You win!\n");
}
