package numerosLongLibro;

import java.util.Random;

public class TestUniversalidadLibro extends UniversalidadLibro {

	/************************************************************
	 * 
	 * @param while_code
	 * @param sol
	 * @return
	 */

	private  boolean TestEjemploCodi(int [][] while_code, long sol) {
		long Codi =	Codi(while_code);
		
		return Codi==sol;
	}

	/************************************************************
	 * 		
	 * 
	 * @return
	 */
	
	public  boolean TestEjemplosCodi() {
		
		//Codi(X1 := X1 –1 ; X1 := 0) == 34
		int [][] while_code= new int [2] [4];
			while_code[0][0]= 3; while_code[0][1]= 1; while_code[0][2]= 1;  while_code[0][3]= 0;
			while_code[1][0]= 0; while_code[1][1]= 1; while_code[1][2]= 1;  while_code[0][3]= 0;
		
		boolean sol = TestEjemploCodi (while_code,34); 
		
		//Codi(X1 := 0 ; X1 := X1 –1) ==64 .	
		while_code[0][0]= 0; while_code[0][1]= 1; while_code[0][2]= 1; while_code[0][3]= 0;
		while_code[1][0]= 3; while_code[1][1]= 1; while_code[1][2]= 1; while_code[0][3]= 0;
		 sol &= TestEjemploCodi (while_code,64); 

		 while_code= new int [3] [4];
		// Codi( X1:=X1 –1 ; X1:=0 ; X1:=0 ) == 42 
		 while_code[0][0]= 3; while_code[0][1]= 1; while_code[0][2]= 1; while_code[0][3]= 0;
		 while_code[1][0]= 0; while_code[1][1]= 1; while_code[1][2]= 1; while_code[1][3]= 0;
		 while_code[2][0]= 0; while_code[2][1]= 1; while_code[2][2]= 1; while_code[2][3]= 0;
		 sol &= TestEjemploCodi (while_code,42); 
		 
		 while_code= new int [4] [4];
		//Codi( X1:=X1 –1 ; X1:=0 ; X1:=0  ; X1:=0) ==24 .

		 while_code[0][0]= 2; while_code[0][1]= 1; while_code[0][2]= 1; while_code[0][3]= 0;
		 while_code[1][0]= 0; while_code[1][1]= 1; while_code[1][2]= 1; while_code[1][3]= 0;
		 while_code[2][0]= 0; while_code[2][1]= 1; while_code[2][2]= 1; while_code[2][3]= 0;
		 while_code[3][0]= 0; while_code[3][1]= 1; while_code[3][2]= 1; while_code[3][3]= 0;
		sol &= TestEjemploCodi (while_code,24);
		return sol;
	}
	
	
	
	/******************************************************
	 * NO funciona por ahora 
	 */
	/*public  void ejemploWhile() {
		
		int [][] while_code2= new int [2] [4];

		while_code2= new int [2] [4];
		while_code2[0][0]= 4; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 1;
		while_code2[1][0]= 2; while_code2[1][1]= 1; while_code2[1][2]= 1; while_code2[1][3]= 0;
	
		System.out.println("Codi: "+ Codi(while_code2));

	}*/
	
	/******************************************************
	 * 
	 * @param while_code
	 * @param n
	 * @param p
	 * @param sol
	 * @return
	 */

	private  boolean TestEjemploCODI( int n, int p, int [][] while_code, long sol) {
		long Codi =	CODI(n,p,while_code);
		
		return Codi==sol;
	}
	
	/******************************************************
	 * CODI( 1, 2, X1 := X1 )  = 53 

	 */
    public  boolean TestEjemplosCODI() {
		
		int [][] while_code= new int [1] [4];

		while_code[0][0]= 1; while_code[0][1]= 1; while_code[0][2]= 1; while_code[0][3]= 0;
		return TestEjemploCODI(1,2,while_code,53);
	
		//System.out.println("Codi: "+ CodiString(while_code)+"\nCODI: "+ CODI(1,2,while_code));
		
	}
	/******************************************************
	 * 
	 * 
	 * 
	 */

	
	public void tipoTestCodi_CODI () {
	    
		  Random r = new Random();
		  long  codi=0,CODI=0;
		  int [][] code=null;
		  int    k  =0;
		  
		  while(codi<=0 | CODI<=0) {
		    code =GeneraCodigo2Distinto(r);
            k= calculateK(code);            // Cálculo mayor indice en el código
		    codi  = Codi(code);
		    CODI  = CODI(1,k,code);
		  }
		  
		  String [] codeString = new String[2];
		  codeString [0] = codiString(code[0]);
		  codeString [1] = codiString(code[1]);
				  
	      String [] solucion   = new String [6];
	      String [] incorrecta = new String [6];
	      
	      long Degodel1 = Codificaciones.DeGodel(codi+1,1);
	      long Degodel2 = Codificaciones.DeGodel(codi+1,2);

	      int numSolucion= r.nextInt(solucion.length); //Qué válida selecciono.
	      solucion[0] = "degod ("+(codi+1)+",1)= "+Degodel1  ;
	      solucion[1] = "degod ("+(codi+1)+",2)= "+Degodel2;
	      solucion[2] = "degod ("+(codi+1)+",1)= codi ("+ codeString [0]+")";
	      solucion[3] = "degod ("+(codi+1)+",2)= codi ("+ codeString [1]+")";
	      solucion[4] = "$\\sigma^1_{31}$ ("+CODI+") = 1 - "+ Codificaciones.DeCantor(CODI,3,1);
	      solucion[5] = "$\\sigma^1_{33}$ ("+CODI+")= "+codi+" "+Codificaciones.DeCantor(CODI,3,3);
	      
	      int numIncorrecta= r.nextInt(incorrecta.length);
	      incorrecta[0] = "degod ("+(codi+1)+",2)= "+Degodel1;
	      incorrecta[1] = "degod ("+(codi+1)+",1)= "+Degodel2;
	      incorrecta[2] = "degod ("+(codi+1)+",1)= codi ("+ codeString [1]+")";
	      incorrecta[3] = "degod ("+(codi+1)+",2)= codi ("+ codeString [0]+")";
	      incorrecta[4] = "$\\sigma^1_{33}$ (CODI(Q)) = 1";
	      incorrecta[5] = "$\\sigma^1_{31}$ (CODI(Q)) = "+codi;
	      
        System.out.println("Sea Q=(1,"+k+",s) y s: "+codeString [0]+" ; "+codeString [1]+" Codi(s)= "+ codi+" CODI(s)= "+ CODI+", entonces:");

        //Sea Q=(1,<%= k %>,s) y s: <%= codeString [0]%> ;  <%= codeString [1]%> y Codi(s)=  <%= code2N%>, entonces:
        
      //  System.out.println(solucion[numSolucion]);
      //  System.out.println(incorrecta[numIncorrecta]);
      //  System.out.println(incorrecta[(numIncorrecta+1)%incorrecta.length]);
        
        for (int i=0;i<solucion.length;i++) {
        	System.out.println(solucion[i]);
        }
        
	}
	
	
	public static void main(String[] args) {
		TestUniversalidadLibro uniLibro = new TestUniversalidadLibro();
		
		System.out.println("TESTS Básicos: "+uniLibro.TestEjemplosCodi());
		System.out.println("TEST  CODI: "+uniLibro.TestEjemplosCODI());
		
		uniLibro.tipoTestCodi_CODI();
	}

}
