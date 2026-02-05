#include <string.h>
 #include <stdio.h>

	int copier(char *str);
 
	void main(int argc, char *argv[]) {
		copier(argv[1]);
		printf("Done!\n");
	}

	int copier(char *str) {
		char buffer[200];
		strcpy(buffer, str);
	}
