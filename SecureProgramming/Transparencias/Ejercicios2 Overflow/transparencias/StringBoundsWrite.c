#include <stdio.h>

int main(){

int array[5];
int i;

  for (i=0;i<2000;i++){
    array[i] = 10; 
    printf("%x:  %d \n", &array[i],array[i]); 
  }

}
