package numerosLongLibro;
import java.util.Random;


public class UniversalidadLibro {

	 
	
	/**********************************************
	 * Codifica la primera sentencia del código pasado por parámetro
	 * 
	 * @param s - Código While
	 * @return
	 */
	public  long codi(int s[][]){ 
		return(codi(s,0));
	}
	
	/**********************************************
	 * Codifica la sentencia index del código while pasado por parámetro
	 * 
	 * @param s - Código While
	 * @param index
	 * @return
	 */
	public  long codi(int s[][],int index){ 
		 long sol    = 0;
		 int i = s[index][1];
		 int j = s[index][2];
				 
			switch(s[index][0]) {
			 case 0:sol= 5*(i-1); break;
			 case 1:sol= 5*Codificaciones.Cantor(i-1,j-1)+1; break;
			 case 2:sol= 5*Codificaciones.Cantor(i-1,j-1)+2; break;
			 case 3:sol= 5*Codificaciones.Cantor(i-1,j-1)+3; break;
			 case 4:sol= 5*Codificaciones.Cantor(i-1,Codi(s,index+1,1))+4; break;
			}
			 
		 return sol;
	}
	
	/**********************************************
	 * Imprime el código de una Sentencia
	 * @param s
	 * @return
	 */
	public  String codiString(int s[]){ 
		 String  sol  = "";
		 int i = s[1];
		 int j = s[2];
		
			switch(s[0]) {
			 case 0:sol= "$X_"+i+":=0$"; break;
			 case 1:sol= "$X_"+i+":=X_"+j+"$"; break;
			 case 2:sol= "$X_"+i+":=X_"+j+"+1$"; break;
			 case 3:sol= "$X_"+i+":=X_"+j+"-1$"; break;
			 case 4:sol= "while $X_"+i+"\\neq$0 do "; break;
			}
			 
		 return sol;
	}
	/**********************************************
	 * Imprimer el código de un programa While
	 * @param s
	 * @return
	 */
	public  String CodiString(int s[][]){ 
		 String  sol  = "";
		
			
		for (int i=0;i<s.length;i++) {
			
			switch(s[i][0]) {
			 case 0:
			 case 1:
			 case 2:
			 case 3:sol= sol+ "\n"+ codiString(s[i]);  break;
			 case 4:sol= sol+"\nwhile $X_"+i+"\\neq$0 do \n\t"+ codiString(s[i+1])+ " \nod "; i++; break;
			 				//FALLA SI WHILE ES LA ULTIMA 
			}
		}	 
		 return sol;
	}
	
	/**********************************************
	 * Codifica un grupo de sentencias.
	 * 
	 * @param s
	 * @return
	 */
	public  long Codi(int s[][]){
		 int len =s.length;
				 
		 return Codi(s,0,len);
	}
	
	/**********************************************
	 * Codifica un código desde el inicio dado por index y un número de sentencias dado por num.
	 * @param s
	 * @param index
	 * @param num
	 * @return
	 */
	private  long Codi(int s[][],int index,int num){ 
		 long  sol  = 0;
		 int lon = index+num;
		 long  [] codi;
		
		 //codi = new int [lon-1]; //WHILE ?????
		 
		if (s[index][0]==4)  codi = new long [lon-1];
		else  codi = new long [lon];
		
         int indexCodi=0;	

			for (int i=index;i<lon;i++) {
				codi[indexCodi]= codi (s,i);
				if (s[i][0]==4) i+=s[i][3];
				indexCodi++;
			}
			 
			sol= Codificaciones.Godel (codi)-1;
			
		 return sol;
	}
	/******************************************************
	 * Genera una sentencia de forma aleatoria
	 * @param r
	 * @return
	 */

	public  int [] GeneraSentencia(Random r) {
		
		int [] while_sentencia= new int [4];
		int mayorIndice=3;
		
				for (int j=0;j<4;j++) {
					switch(j) {
					  case 0: while_sentencia [j] = r.nextInt(4); break;	
					  case 1:												          //I - No vale el cero en los indices
					  case 2: while_sentencia [j] = r.nextInt(mayorIndice)+1; break; // J - No vale el cero en los indices
					  case 3: break; //While (1)
					}
			}
			return while_sentencia;
		}
	/******************************************************
	 * Genera dos sentencias distintas una de otra de forma aleatoria
	 * @param r
	 * @return
	 */

	
	public  int [][] GeneraCodigo2Distinto(Random r) {
		
		int longitudCodigo = 2;
		int [][] while_code= new int [longitudCodigo] [4];
		
		    while_code [0] = GeneraSentencia(r);
			long codi = codi(while_code,0); 
			long codi2;
					
			do {
				 while_code [1] = GeneraSentencia(r);
				 codi2     		= codi(while_code,1); 
			}
			while (codi==codi2);
			
		
			return while_code;
      }

	
	/**********************************************
	 * Genera un código de una longitud dada e incluye o no sentencias while
	 * 
	 * @param r - Generación aleatoria
	 * @param longitudCodigo - Longitud del código generado
	 * @param whileInclude - Determina sentencias while o no.
	 * @return
	 */

	
	public  int [][] GeneraCodigo(Random r,int longitudCodigo,boolean whileInclude) {
	
	int [][] while_code= new int [longitudCodigo] [4];
	
	
		for (int i=0;i<longitudCodigo;i++) {
			for (int j=0;j<4;j++) {
				switch(j) {
				  case 0: if (whileInclude && (i<longitudCodigo-1)) while_code [i][j] = r.nextInt(5); 
				  	      else while_code [i][j] = r.nextInt(4); break;	
				  case 1:
				  case 2: while_code [i][j] = r.nextInt(3)+1; break; //No vale el cero en los indices
				  case 3: break; //While (1)
				}
			}
		}
		return while_code;
	}
	
	/**********************************************
	 * Genero código de longitud @longitudCodigo que no modifique la variable X1. 
	 * Por lo que devuelve FQ=0
	 * 
	 * @param longitudCodigo
	 * @param r
	 * @return
	 */

	public  int [][] GeneraCodigoFQ0(Random r,int longitudCodigo) {
		
		int [][] while_code= new int [longitudCodigo] [4];
		
			long indice = Codificaciones.nextLong(r,2,4);
		
			for (int i=0;i<longitudCodigo;i++) {
				for (int j=0;j<4;j++) {
					switch(j) {
					  case 0: while_code [i][j] = r.nextInt(4); break;	
					  case 1:												//I - 
					  case 2: while_code [i][j] = (int) indice; break; // J - 2 o 3 no modifico la entrads
					  case 3: break; //While (1)
					}
				}
			}
			return while_code;
		}
	/**********************************************
	 *  Calcula el mayor índice de las variables utilizados en un código
	 * @param s
	 * @return
	 */
	
	public int calculateK(int s[][]){
		int k=0;
		int indice_i=0,indice_j=0;
		
		for (int i=0;i<s.length;i++) {
			indice_i = s[i][1];
			if (indice_i > k) k = indice_i;
			indice_j = s[i][2];
			if (indice_j > k) k = indice_j;
		}
		
		return k;
	}
	/**********************************************
	 * CODI de un programa while
	 * @param n
	 * @param p
	 * @param s
	 * @return
	 */

	public  long CODI(int n,int p, int s[][]){ 
		long sol=0;
		int k = calculateK(s);
		int valor2 = p - Math.max(n,k);
		
		sol = Codificaciones.CantorN(n,valor2,Codi(s));
		return sol;
	}
	
	/*public  long Codi(int n,int p, long z){ 
		long sol=0;

		//sol = Cantor(n,z);
		return sol;
	}*/
	
	/**********************************************
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		UniversalidadLibro uni = new UniversalidadLibro();
		
		Random r = new Random();
		int [][] while_code= uni.GeneraCodigo2Distinto(r);
		System.out.println("Codi: "+ uni.CodiString(while_code)+ " Codi ="+ uni.Codi(while_code));
		System.out.println("k: "+ uni.calculateK(while_code));
				
				
		/*  int maxValor=20; 
	      int nMax=3;

	      Random r = new Random();
	      int n= 2;//r.nextInt(nMax)+1;
	      
	      long [] valores = new long[n];
	      for(int i=0;i<n;i++) valores[i] =  nextLong (r,maxValor);
	     
	      long sol  = CantorN(valores);
	      
	      String valoresS="";
	      for(int i=0;i<n-1;i++) valoresS=valoresS+valores[i]+",";
	      valoresS=valoresS+valores[n-1];
	      
	      System.out.println("Valor de sigma "+n+" ("+valoresS+")="+sol);
	     
	      
	      long [] valoresSol = new long[n];
	      for(int i=0;i<n;i++) {
	    	  valoresSol[i] =  DeCantorN(sol,n,i+1);
	    	  System.out.println("X"+(i+1)+"= "+valoresSol[i]);
	      }
	      */
	  
		
			
		
	/*	
		//Parametros 0-tipo 1-variable i 2- variable j 3- en while número de sentencias dentro, sin sentido demas
		//while X1 != 0 do X1 :=X1+1 od
	 */
		/*ejemplo1();
		ejemplo2();
		ejemplo3();
		ejemplo4();*/
		//ejemploWhile();
		
		//GeneraCodigo();
		
		//System.out.println(CodiString(GeneraCodigo(5,true)));
		
		//int [][] while_code= GeneraCodigo(3,false);
		//System.out.println("Codi: "+ CodiString(while_code)+ "\n="+ Codi(while_code));

		
	}

}
