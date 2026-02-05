package numerosLongTransparencias;
import java.util.Random;



public class TestUniversalidadApuntes {

	

/**********************************************
 * 
 * 
 * 
 * 	
 */
	public  void GeneraTest1(UniversalidadApuntes universal) {
	
		 int maxValor=11; 

	      Random r = new Random();
	      int n= 3;
	      
	      long [] valores = new long[n];
	      for(int i=0;i<n;i++) valores[i] =  universal.nextLong (r,maxValor);
	     
	      long sol  = universal.CantorN(valores);
	      
	      char sigma = 0x03C3;

	      System.out.println("Sea: "+sigma+"^"+3+"_1 (x,y,v)=z, z="+sol);
	      
	      String [] solucion = new String [6];
	      String [] incorrecta = new String [6];
	      
	      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
	      solucion[0] = "x="+sigma+"^"+1+"_31 ("+sol+")"+" y="+sigma+"^"+1+"_32 ("+sol+")"+" v= "+sigma+"^"+1+"_33 ("+sol+")";
	      solucion[1] = "x="+valores[0]+" y="+sigma+"^"+1+"_32 ("+sol+")"+" v= "+sigma+"^"+1+"_33 ("+sol+")";
	      solucion[2] = "x="+sigma+"^"+1+"_31 ("+sol+")"+" y="+valores[1]+" v= "+sigma+"^"+1+"_33 ("+sol+")";
	      solucion[3] = "x="+sigma+"^"+1+"_31 ("+sol+")"+" y="+sigma+"^"+1+"_32 ("+sol+")"+" v= "+valores[2];
	      solucion[4] = "x="+valores[0]+" y="+valores[1]+" v="+valores[2];
	      solucion[5] = "x="+sigma+"^"+1+"_1("+valores[0]+") y="+sigma+"^"+1+"_1("+valores[1]+") v="+sigma+"^"+1+"_1("+valores[2]+")";
	      
	      int numIncorrecta= r.nextInt(incorrecta.length);
	      incorrecta[0] = "x="+sigma+"^"+3+"_1 ("+sol+")"+" y="+sigma+"^"+3+"_1 ("+sol+")"+" v="+sigma+"^"+3+"_1 ("+sol+")"; 
	      incorrecta[1] = "x="+sigma+"^"+1+"_31 ("+valores[0]+")"+" y="+sigma+"^"+1+"_32 ("+valores[1]+")"+" v="+sigma+"^"+1+"_33 ("+valores[2]+")";
	      incorrecta[2] = "x="+sigma+"^"+1+"_1 ("+sol+")"+" y="+sigma+"^"+1+"_1 ("+sol+")"+" v="+sigma+"^"+1+"_1 ("+sol+")"; 
	      incorrecta[3] = "x="+sigma+"^"+1+"_13 ("+valores[0]+")"+" y="+sigma+"^"+1+"_23 ("+valores[1]+")"+" v="+sigma+"^"+1+"_33 ("+valores[2]+")";
	      incorrecta[4] = "x="+sigma+"^"+2+"_1 ("+valores[0]+","+sol+")"+" y="+sigma+"^"+2+"_1 ("+valores[1]+","+sol+")"+" v="+sigma+"^"+2+"_1 ("+valores[2]+","+sol+")";
	      incorrecta[5] = "x="+sigma+"^"+3+"_1 ("+valores[0]+","+valores[1]+","+sol+")"+" y="+sigma+"^"+3+"_1 ("+valores[1]+","+valores[2]+","+sol+")"+" v="+sigma+"^"+3+"_1 ("+valores[0]+","+valores[2]+","+sol+")";

	      System.out.println("\n-----------Respuestas ---------");
	      System.out.println("* a) "+solucion[numSolucion]);
	      System.out.println("- b) "+incorrecta[numIncorrecta]);
	      System.out.println("- c) "+incorrecta[(numIncorrecta+1)%incorrecta.length]);

	}
	
	/**********************************************
	 * 
	 * 
	 * 
	 * 	
	 */
	
	public  void GeneraTest2(UniversalidadApuntes universal) {
		
			 int maxValor=5; 
	
		      Random r = new Random();
		      int n= 4;
		      
		      long [] valores = new long[n];
		      for(int i=0;i<n;i++) valores[i] =  universal.nextLong (r,0,maxValor);
		     
		      long sol  =  universal.CantorN(valores);
		      
		      char sigma = 0x03C3;
		      
		      
		      String [] enunciado = new String [n];
		      int numEnunciado= r.nextInt(n); //Qué parametro selecciono.
		      for (int i=0;i<n;i++) {
		    	  enunciado[i]=valores[i]+"";
		    	  if (numEnunciado==i) enunciado[i]="x";
		      }
	
	
		      System.out.println("Sea: "+sigma+"^"+4+"_1 ("+enunciado[0]+","+enunciado[1]+","+enunciado[2]+","+enunciado[3]+")=z, z="+sol);
		      
		      String [] solucion = new String [3];
		      String [] incorrecta = new String [5];
		      
		      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
		      solucion[0] = "x="+valores[numEnunciado];
		      solucion[1] = "x="+sigma+"^"+1+"_4"+(numEnunciado+1)+"("+sol+")";
		      solucion[2] = "x<"+sol+"";
		   
		      long base=(valores[numEnunciado]+universal.nextLong (r,7,15));
		      int numIncorrecta= r.nextInt(incorrecta.length);
		      incorrecta[0] = "x="+base;
		      incorrecta[1] = "x="+(base+universal.nextLong (r,1,10));
		      incorrecta[2] = "x="+sigma+"^"+1+"_4("+valores[0]+")";
		      incorrecta[3] = "x="+sigma+"^"+1+"_4("+sol+")";
		      incorrecta[4] = "x="+sigma+"^"+1+"_4"+((numEnunciado+2))+"("+sol+")";
	
		      
		      System.out.println("\n----------- Respuestas ---------");
		      System.out.println("* a) "+solucion[numSolucion]);
		      System.out.println("- b) "+incorrecta[numIncorrecta]);
		      System.out.println("- c) "+incorrecta[(numIncorrecta+1)%incorrecta.length]);

	}
	
	/**********************************************
	 * 
	 * 
	 * 
	 * 	
	 */
	
	public  void GeneraTest3(UniversalidadApuntes universal) {
		
		 int maxValor=245; 

	      Random r = new Random();
	      int n= 1;
	      
	      long [] valores = new long[n];
	      valores[0] = universal.nextLong (r,5,maxValor);
	     
	      long sol  = valores[0];
	      
	      char sigma = 0x03C3;
	      char pi= 0x03C0;
	      char theta= 0x03B8;
	      
	      System.out.println("Sea: "+sigma+"^"+1+"_1 ("+valores[0]+")=z");
	      
	      String [] solucion = new String [5];
	      String [] incorrecta = new String [5];
	      
	      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
	      solucion[0] = "z="+sol;
	      solucion[1] = "z="+pi+"^1_1("+sol+")";
	      solucion[2] = "z="+pi+"^2_1("+sol+","+(sol+universal.nextLong (r,1,15))+")";
	      solucion[3] = "z="+sigma+"("+(sol-1)+")";
	      solucion[4] = "z="+sigma+"("+sigma+"("+(sol-2)+"))";
	      
	      int numIncorrecta= r.nextInt(incorrecta.length);
	      incorrecta[0] = "z="+0;
	      incorrecta[1] = "z<"+sol;
	      incorrecta[2] = "z="+pi+"^2_2("+sol+","+(sol+universal.nextLong (r,1,15))+")";
	      incorrecta[3] = "z="+theta+"()";
	      incorrecta[4] = "z<="+(sol-1);
	      
	      System.out.println("\n----------- Respuestas ---------");
	      System.out.println("* a) "+solucion[numSolucion]);
	      System.out.println("- b) "+incorrecta[numIncorrecta]);
	      System.out.println("- c) "+incorrecta[(numIncorrecta+1)%incorrecta.length]);

	}
	
	public  void GeneraTest4(UniversalidadApuntes universal) {
		

	      Random r = new Random();
	      
	      	
			  int [][] code = universal.GeneraCodigo2Distinto(r);
			  long  code2N  = universal.code2N(code);
			  String [] codeString = new String[2];
			  codeString [0] = universal.codiString(code[0]);
			  codeString [1] = universal.codiString(code[1]);
					  
			  System.out.println("Sea Q=(1,c) y c: "+codeString [0]+";"+codeString [1]+ " y code2N(c)= "+ code2N);
			  
			  String [] solucion = new String [6];
		      String [] incorrecta = new String [6];
		      
		      long Degodel1 = universal.DeGodel(code2N+1,1);
		      long Degodel2 = universal.DeGodel(code2N+1,2);

		      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
		      solucion[0] = "$\\gamma$ ("+(code2N+1)+",1)= "+Degodel1;
		      solucion[1] = "$\\gamma$ ("+(code2N+1)+",2)= "+Degodel2;
		      solucion[2] = "$\\gamma$ ("+(code2N+1)+",1)= sent2N ("+ codeString [0]+")";
		      solucion[3] = "$\\gamma$ ("+(code2N+1)+",2)= sent2N ("+ codeString [1]+")";
		      solucion[4] = "$\\sigma^1_{21}$ (while2N(Q)) = 1";
		      solucion[5] = "$\\sigma^1_{22}$ (while2N(Q)) = "+code2N;
		      
		      int numIncorrecta= r.nextInt(incorrecta.length);
		      incorrecta[0] = "$\\gamma$ ("+(code2N+1)+",2)= "+Degodel1;
		      incorrecta[1] = "$\\gamma$ ("+(code2N+1)+",1)= "+Degodel2;
		      incorrecta[2] = "$\\gamma$ ("+(code2N+1)+",1)= sent2N ("+ codeString [1]+")";
		      incorrecta[3] = "$\\gamma$ ("+(code2N+1)+",2)= sent2N ("+ codeString [0]+")";
		      incorrecta[4] = "$\\sigma^1_{22}$ (while2N(Q)) = 1";
		      incorrecta[5] = "$\\sigma^1_{21}$ (while2N(Q)) = "+code2N;
		      
		      
		      System.out.println("\n----------- Respuestas ---------");
		      System.out.println("* a) "+solucion[numSolucion]);
		      System.out.println("- b) "+incorrecta[numIncorrecta]);
		      System.out.println("- c) "+incorrecta[(numIncorrecta+1)%incorrecta.length]);
		      
		   /*   long while2N = universal.while2N(1,code2N);
		      long d1=universal.DeCantor (while2N,1);
		      long d2=universal.DeCantor (while2N,2);
		      
		      System.out.println("-Wile "+while2N + " d1:"+d1+" d2:"+d2);*/

		
	}
	
	public  void GeneraTest5(UniversalidadApuntes universal) {
		

	      Random r = new Random();
	      
	      	
			  int [][] code = universal.GeneraCodigoFQ0(2,r);
			  long  g  = universal.while2N(1,code);
			  String [] codeString = new String[2];
			  codeString [0] = universal.codiString(code[0]);
			  codeString [1] = universal.codiString(code[1]);
			  
			  int x1 = r.nextInt(1000);
			  int x2 = r.nextInt(1000);

			  
			  System.out.println("Sea el Q(1,s) siendo s: "+codeString [0]+";"+codeString [1]+" y $X_1="+x1+"$ y while2N(Q)="+g+", entonces:");
			  
			  String [] solucion = new String [2];
		      String [] incorrecta = new String [4];
		      

		      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
		      solucion[0] = "$H^1$("+g+","+x1+") es verdadero";
		      solucion[1] = "$F_Q("+x1+") \\in  \\mathbb{N}$";


		      int numIncorrecta= r.nextInt(incorrecta.length);
		      incorrecta[0] = "$H^1$("+g+","+x1+") es falso" ;
		      incorrecta[1] = "$F_Q("+x1+") \\notin  \\mathbb{N}$";
		      incorrecta[2] = "$H$("+g+","+x1+","+x2+") es falso" ;
		      incorrecta[3] = "$F_Q("+x1+","+x2+") \\notin \\mathbb{N}$";
		      
		      System.out.println("\n----------- Respuestas ---------");
		      System.out.println("* a) "+solucion[numSolucion]);
		      System.out.println("- b) "+incorrecta[numIncorrecta]);
		      System.out.println("- c) "+incorrecta[(numIncorrecta+1)%incorrecta.length]);
		      	  
		
	}		
				


	
	/**********************************************
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		

	}

}
