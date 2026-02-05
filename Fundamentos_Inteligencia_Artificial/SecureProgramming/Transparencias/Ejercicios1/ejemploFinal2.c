#include<stdio.h>
#include<malloc.h>

void FUNCION_1();
void FUNCION_2();

//char s1[]="Hola"; //initialized read-write area DATA
int i;		   //uninitialized DATA segment	
const int x=1;     //initialized read-write area DATA

int main(){
 static int TEMP=0; 			//uninitialized DATA segment
 char *p = (char *) malloc (sizeof(char)); //heap segment
 FUNCION_1();
 return 0;
}
void FUNCION_1(){
    int a;		//initialized in stack frame of FUNCION_1
    FUNCION_2(); 	// FUNCION_2 stack frame 
}
void FUNCION_2(){
    int b;	 	//initialized in stack frame of FUNCION_2
}

