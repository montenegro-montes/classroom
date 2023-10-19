#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

int copier(char *str) {
        char buffer[1024];
        strcpy(buffer, str);
}

void error(const char *msg){
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[]){
     int sockfd, newsockfd, portno;
     socklen_t clilen;

     char buffer[4096], reply[5100];
     struct sockaddr_in serv_addr, cli_addr;
     int n;
     sockfd = socket(AF_INET, SOCK_STREAM, 0);
     
      if (sockfd < 0) error("ERROR apertura socket");

     bzero((char *) &serv_addr, sizeof(serv_addr));
     portno = 4001;
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;
     serv_addr.sin_port = htons(portno);

	if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) error("ERROR en binding");
  
     listen(sockfd,5);
     clilen = sizeof(cli_addr);

     newsockfd = accept(sockfd,(struct sockaddr *) &cli_addr, &clilen);
    
 	if (newsockfd < 0)    error("ERROR en accept");
    
     while (1) {
        n = write(newsockfd,"Bienvenido a mi servidor!  Escribe un mensaje!\n",47);
        bzero(buffer,4096);
        n = read(newsockfd,buffer,4095);
        if (n < 0) error("ERROR leyendo del socket");
      
        copier(buffer);

        printf("El mensaje es: %s\n",buffer);
        strcpy(reply, "Obtuve el siguiente mensaje: ");
        strcat(reply, buffer);
        n = write(newsockfd, reply, strlen(reply));
        if (n < 0) error("ERROR escribiendo en el socket");
      }

     close(newsockfd);
     close(sockfd);
     return 0;
}
