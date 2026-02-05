package numerosLongTransparencias;
import java.util.Random;


public class UniversalidadApuntes {

	public long nextLong(Random rng, long n) {
		long x = 1;
		long y = n;
		long number = x+((long)(rng.nextDouble()*(y-x)));
		
		return number;
	}
	
	public long nextLong(Random rng, long inf, long sup) {
		long x = inf;
		long y = sup;
		long number = x+((long)(rng.nextDouble()*(y-x)));
		
		return number;
	}
	
	/********************************************
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public  long Cantor(long x, long y){ 
		 long sol=0;

		 sol= (x+y) * (x+y+1) /2 +y;
		 return sol;
		} 
	/**********************************************
	 * 
	 * @param par
	 * @return
	 */

	
	public  long CantorN(long ... par){ 
		 long sol=0;
		 int lon = par.length; 

		 switch(lon) {
		     case 1: sol=par[0]; break;
		     case 2: sol= Cantor (par[0],par[1]); break;
		     default:    
		    	 long[] par2 = new long[lon-1]; 
		    	 System.arraycopy(par,0,par2,0,lon-1);
	             sol= Cantor(CantorN(par2),par[lon-1]);
	       
		 }
		 return sol;
		} 

	/**********************************************
	 * 
	 * @param z
	 * @return
	 */
	public  long d(long z ){ 
		long sol=0;
	
		
		while (Cantor (sol,0)<=z) {
			sol++;
		}
		sol--;
		return sol;
	}
	/**********************************************
	 * 
	 * @param z
	 * @return
	 */
	public  long DeCantor2Comp(long z ){ 
	
		long sol=0;
		
		sol = z - Cantor(d(z),0);
		
		return sol;
	}
	/**********************************************
	 * 
	 * @param z
	 * @return
	 */
	public  long DeCantor1Comp(long z ){ 
		
		long sol=0;
		
		sol = d(z) - DeCantor2Comp(z);
		
		return sol;
	}
	/**********************************************
	 * 
	 * @param z
	 * @param componente
	 * @return
	 */


	public  long DeCantor(long z, int componente ){ 
		
		long sol=0;
		switch(componente) {
		     case 1: sol = DeCantor1Comp( z ); break;
	 	     case 2: sol=  DeCantor2Comp( z ); break;
		}
		
		return sol;
	}
	/**********************************************
	 * 
	 * @param z
	 * @param k
	 * @param i
	 * @return
	 */
	
	public  long DeCantorN(long z, int k, int p ){ 
		long sol=0;

		
		if (k==1) sol=z;
		else if ((p>=1) & (p<k)) sol= DeCantorN (DeCantor1Comp( z ), k-1,p);
		else if (p==k)  sol=DeCantor2Comp(z);		
		
		return sol;
	}
	
	/**********************************************
	 * 
	 * @param par
	 * @return
	 */
	public  int L(long ... par){ 
		 int sol= par.length;
		 
		 return sol;
	}
	
	/**********************************************
	 * 
	 * @param par
	 * @return
	 */
	public  long Godel(long ... par){ 
		
		 long sol    = 0;
		 int length = par.length;
		 long l = L(par);
		
		// for (int i=0;i<l;i++)
		 //  System.out.print(par[i]+", ");

		 //System.out.println("");
		 
		 if (length!=0) {
			sol= Cantor(l-1,CantorN(par))+1;
		 }

		 return sol;
	}
	/**********************************************
	 * 
	 * @param z
	 * @return
	 */
	public  long l(long z){ 
		 long sol    = 0;
	
		 if (z>0) {
			 sol = DeCantor1Comp(z-1)+1;
		 }
		 return sol;
	}
	
	/**********************************************
	 * 
	 * @param z
	 * @param k
	 * @return
	 */
	
	public  long DeGodel(long z, int k){ 
		 long sol    = 0;
		
		 int l = (int) l(z);
		 
		 if ((z>0) & k<=l){
			sol= DeCantorN (DeCantor(z-1,2),l,k);
		 }
		 return sol;
	}
	/**********************************************
	 * 
	 * @param s
	 * @return
	 */
	public  long sent2N(int s[][]){ 
		return(sent2N(s,0));
	}
	/**********************************************
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public  long sent2N(int s[][],int index){ 
		 long sol    = 0;
		 int i = s[index][1];
		 int j = s[index][2];
		
		// System.out.println("codi "+codiString(s[index])+"=");
		 
			switch(s[index][0]) {
			 case 0:sol= 5*(i-1); break;
			 case 1:sol= 5*Cantor(i-1,j-1)+1; break;
			 case 2:sol= 5*Cantor(i-1,j-1)+2; 
			 			// System.out.println("\t 5*\u03C3("+(i-1)+","+(j-1)+")+2");
			 			// System.out.println("\t codi "+codiString(s[index])+ " sol= "+sol);
			 			 break;
			 case 3:sol= 5*Cantor(i-1,j-1)+3; break;
			/* case 4:
				 	System.out.println("\t 5*\u03C3("+(i-1)+",Codi("+codiString(s[index+1]) +")+4");
				 	 
				 	sol= 5*Cantor(i-1,Codi(s,index+1,1))+4; 
				 	System.out.println("\t codi "+codiString(s[index])+ " sol= "+sol);
			 		break;*/
			}
			 
		 return sol;
	}
	
	/**********************************************
	 * 
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
			 //case 4:sol= "while $X_"+i+"\\neq$0 do "; break;
			}
			 
		 return sol;
	}
	/**********************************************
	 * 
	 * @param s
	 * @return
	 */
	public  String CodiString(int s[][]){ 
		 String  sol  = codiString(s[0]);
		
			
		for (int i=1;i<s.length;i++) {
			
			switch(s[i][0]) {
			 case 0:
			 case 1:
			 case 2:
			 case 3:  sol= sol+ "; "+ codiString(s[i]); 
			 		break;
			// case 4:sol= sol+"\nwhile $X_"+i+"\\neq$0 do \n\t"+ codiString(s[i+1])+ " \nod "; i++; break;
			 				//FALLA SI WHILE ES LA ULTIMA 
			}
		}	 
		 return sol;
	}
	
	/**********************************************
	 * 
	 * @param s
	 * @return
	 */
	public  long code2N(int s[][]){
		 int len =s.length;
				 
		 return code2N(s,0,len);
	}
	
	/**********************************************
	 * 
	 * @param s
	 * @param index
	 * @param num
	 * @return
	 */
	public  long code2N(int s[][],int index,int num){ 
		 long  sol  = 0;
		 int lon = index+num;
		 long  [] codi;
		
		 //codi = new int [lon-1]; //WHILE ?????
		 
		if (s[index][0]==4)  codi = new long [lon-1];
		else  codi = new long [lon];
		
         int indexCodi=0;	
         

			for (int i=index;i<lon;i++) {
				//System.out.println("code2N("+ codiString(s[i])+")"  );
				codi[indexCodi]= sent2N (s,i);
				if (s[i][0]==4) i+=s[i][3];
				indexCodi++;
			}
			 
			sol= Godel (codi)-1;
			
		 return sol;
	}
	
	/******************************************************
	 * 
	 */
	public  void ejemploWhile() {
		
		int [][] while_code2= new int [2] [4];

		while_code2= new int [2] [4];
		while_code2[0][0]= 4; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 1;
		while_code2[1][0]= 2; while_code2[1][1]= 1; while_code2[1][2]= 1; while_code2[1][3]= 0;
	
		System.out.println("code2N: "+ code2N(while_code2));

	}
	
	public  void ejemplo1() {
		//Codi(X1 := X1 –1 ; X1 := 0)
		System.out.println("********* EJEMPLO 1 ******");
				
		int [][] while_code2= new int [2] [4];
			while_code2[0][0]= 3; while_code2[0][1]= 1; while_code2[0][2]= 1;  while_code2[0][3]= 0;
			while_code2[1][0]= 0; while_code2[1][1]= 1; while_code2[1][2]= 1;  while_code2[0][3]= 0;
			
			
			System.out.println("code2N: "+ CodiString(while_code2)+ "\n="+ code2N(while_code2));
	}
	
	public  void ejemplo2() {
		//Codi(X1 := 0 ; X1 := X1 –1) .
		System.out.println("********* EJEMPLO 2 ******");
		int [][] while_code2= new int [2] [4];

			while_code2[0][0]= 0; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 0;
			while_code2[1][0]= 3; while_code2[1][1]= 1; while_code2[1][2]= 1; while_code2[0][3]= 0;
			
		
			System.out.println("code2N: "+ CodiString(while_code2)+ "\n="+ code2N(while_code2));
	}
	
	public  void ejemplo3() {
		//Codi( X1:=X1 –1 ; X1:=0 ; X1:=0 ) .
		System.out.println("********* EJEMPLO 3 ******");
		int [][] while_code2= new int [3] [4];

		while_code2[0][0]= 3; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 0;
		while_code2[1][0]= 0; while_code2[1][1]= 1; while_code2[1][2]= 1; while_code2[1][3]= 0;
		while_code2[2][0]= 0; while_code2[2][1]= 1; while_code2[2][2]= 1; while_code2[2][3]= 0;
		
		System.out.println("code2N: "+ CodiString(while_code2)+ "\n="+ code2N(while_code2));
	}
	
	
	public  void ejemplo4() {
		//Codi( X1:=X1 –1 ; X1:=0 ; X1:=0  ; X1:=0) .
		System.out.println("********* EJEMPLO 4 ******");
		int [][] while_code2= new int [4] [4];

		while_code2[0][0]= 2; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 0;
		while_code2[1][0]= 0; while_code2[1][1]= 1; while_code2[1][2]= 1; while_code2[1][3]= 0;
		while_code2[2][0]= 0; while_code2[2][1]= 1; while_code2[2][2]= 1; while_code2[2][3]= 0;
		while_code2[3][0]= 0; while_code2[3][1]= 1; while_code2[3][2]= 1; while_code2[3][3]= 0;

		System.out.println("Codi: "+ CodiString(while_code2)+ "\n="+ code2N(while_code2));
	}
	
	
	public  int [][] GeneraCodigo(int longitudCodigo,boolean whileInclude) {
	
	int [][] while_code= new int [longitudCodigo] [4];
	int mayorIndice=3;
	
		Random r = new Random();
	
		for (int i=0;i<longitudCodigo;i++) {
			for (int j=0;j<4;j++) {
				switch(j) {
				  case 0: if (whileInclude && (i<longitudCodigo-1)) while_code [i][j] = r.nextInt(5); //TIPO
				  	      else while_code [i][j] = r.nextInt(4); break;	
				  case 1:												//I - 
				  case 2: while_code [i][j] = r.nextInt(mayorIndice)+1; break; // J - No vale el cero en los indices
				  case 3: break; //While (1)
				}
			}
		}
		return while_code;
	}
	
   public  int [] GeneraSentencia(Random r) {
		
		int [] while_sentencia= new int [4];
		int mayorIndice=3;
		
				for (int j=0;j<4;j++) {
					switch(j) {
					  case 0: while_sentencia [j] = r.nextInt(4); break;	
					  case 1:												//I - 
					  case 2: while_sentencia [j] = r.nextInt(mayorIndice)+1; break; // J - No vale el cero en los indices
					  case 3: break; //While (1)
					}
			}
			return while_sentencia;
		}
	
	public  int [][] GeneraCodigo2Distinto(Random r) {
		
		int longitudCodigo = 2;
		int [][] while_code= new int [longitudCodigo] [4];
		
		    while_code [0] = GeneraSentencia(r);
			long sent2N = sent2N(while_code,0); 
			long sent2N2;
					
			do {
				 while_code [1] = GeneraSentencia(r);
				 sent2N2 = sent2N(while_code,1); 
			}
			while (sent2N==sent2N2);
			
		
			return while_code;
      }
	
	public  int [][] GeneraCodigoFQ0(int longitudCodigo,Random r) {
		
		int [][] while_code= new int [longitudCodigo] [4];
		
		
			long indice = nextLong(r,2,4);
		
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
	
	public  long senttype(long z){ 
		return z%5;
	}
	
	public  long LHS(long z){ 
		long sol=0;
		
		long type = senttype(z);
		
		if (type==0) 
			sol=1+(z/5);
		else 
			sol=1+DeCantor((z-type)/5,1);
		
		return sol;
	}
	
	public  long RHS(long z){ 
		long sol=0;
		
		long type = senttype(z);
		
		
			sol=1+DeCantor((z-type)/5,2);
		
		return sol;
	}
	
	public  long while2N(int n,int s[][]){ 
		long sol=0;

		sol = Cantor(n,code2N(s));
		return sol;
	}
	
	public  long while2N(int n,long z){ 
		long sol=0;

		sol = Cantor(n,z);
		return sol;
	}

	/**********************************************
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		UniversalidadApuntes universal = new UniversalidadApuntes(); 

		
		int [][] while_code1= new int [1] [4];
		int [][] while_code2= new int [1] [4];
		
		
		/*while_code1[0][0]= 2; while_code1[0][1]= 1; while_code1[0][2]= 1; while_code1[0][3]= 1;
		while_code2[0][0]= 1; while_code2[0][1]= 1; while_code2[0][2]= 1; while_code2[0][3]= 0;
		
		long z=universal.sent2N(while_code1);
		long z2=universal.sent2N(while_code2);
		
		System.out.println("sent2N: "+ universal.CodiString(while_code1)+ "\n="+ universal.sent2N(while_code1));
		System.out.println("sent2N: "+ universal.CodiString(while_code2)+ "\n="+ universal.sent2N(while_code2));

		System.out.println("sent2N: "+ z + " typo: " +universal.senttype(z));
		System.out.println("sent2N: "+ z2 + " typo: " +universal.senttype(z2));

		System.out.println("LHS: "+ universal.LHS(z) + " RHS: " +universal.RHS(z));
		System.out.println("LHS: "+ universal.LHS(z2) + " RHS: " +universal.RHS(z2));
		
		System.out.println("Check: "+ ((universal.LHS(z)-universal.LHS(z2))+(universal.RHS(z)-universal.RHS(z2))) );
		
		System.out.println("Cantor: "+ universal.Cantor(universal.LHS(z),universal.RHS(z)));
		System.out.println("Cantor: "+ universal.Cantor(universal.LHS(z2),universal.RHS(z2)));

		System.out.println("Cantor: "+ universal.CantorN(universal.senttype(z),universal.LHS(z),universal.RHS(z)));
		System.out.println("Cantor: "+ universal.CantorN(universal.senttype(z2),universal.LHS(z2),universal.RHS(z2)));
*/
		
		 /* int maxValor=11; 
	      int nMax=3;

	      Random r = new Random();
	      int n= 3;//r.nextInt(nMax)+2;
	      
	      long [] valores = new long[n];
	      for(int i=0;i<n;i++) valores[i] =  nextLong (r,maxValor);
	     
	      long sol  = CantorN(valores);
	      
	      String valoresS="";
	      for(int i=0;i<n-1;i++) valoresS=valoresS+valores[i]+",";
	      valoresS=valoresS+valores[n-1];
	      
	      char sigma = 0x03C3;
	      
	      System.out.println(sigma+"^"+3+"_1 ("+valoresS+")="+sol);
	      
	      
	      long [] valoresSol = new long[n];
	      for(int i=0;i<n;i++) {
	    	  valoresSol[i] =  DeCantorN(sol,n,i+1);
	    	  
	    	  System.out.println("X_"+(i+1)+"= "+valoresSol[i]);
	      }
	      
	      long sol2  = CantorN(valoresSol);
	      
	      System.out.println("Sol2: "+sol2);*/
		
	      
	      	   
	      
		/*long [] cantor = {10,5};
		long solCantor = CantorN(cantor);
		System.out.println(solCantor);
		
		long [] decantor = new long[2];
		decantor[0]=DeCantor1Comp(solCantor);
		decantor[1]=DeCantor2Comp(solCantor);
		System.out.println(decantor[0]+","+decantor[1]);*/
		
		/*long [] godel = {5,3,1,10};
		long solGodel = Godel(godel);
		System.out.println(solGodel);
		
		long [] deGodel = new long[4];
		deGodel[0]=DeGodel(solGodel,1);
		deGodel[1]=DeGodel(solGodel,2);
		deGodel[2]=DeGodel(solGodel,3);
		deGodel[3]=DeGodel(solGodel,4);
		System.out.println(deGodel[0]+" "+deGodel[1]+" "+deGodel[2]+" "+deGodel[3]);
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

		long deGodel = universal.DeGodel(3430889,1);
		long deGodel2 = universal.DeGodel(3430888,1);
		
		System.out.println("deGodel(3430889): "+ deGodel+ " deGodel(3430888): "+ deGodel2);

		while_code1[0][0]= 0; while_code1[0][1]= 3; while_code1[0][2]= 1; while_code1[0][3]= 1;
		System.out.println("sent2N: "+ universal.CodiString(while_code1)+ "\n="+ universal.sent2N(while_code1));

	}

}
