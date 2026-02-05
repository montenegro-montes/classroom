package numerosLongLibro;

import java.math.BigInteger;
import java.util.Random;

public class Codificaciones {

	
	long nextLong(Random rng, long n) {
		long x = 1;
		long y = n;
		long number = x+((long)(rng.nextDouble()*(y-x)));
		
		return number;
	}
	
	 static long nextLong(Random rng, long inf, long sup) {
		long x = inf;
		long y = sup;
		long number = x+((long)(rng.nextDouble()*(y-x)));
		
		return number;
	}
	
	
	/********************************************
	 * 
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static long Cantor(long x, long y){ 
		 long sol=0;

		 sol= (x+y) * (x+y+1) /2 +y;		 
		return sol;
	} 
	
	/**********************************************
	 * 
	 * 
	 * @param par
	 * @return
	 */
	
	public  static long CantorN(long ... par){ 
		 long sol=0;
		 int lon = par.length; 

		 switch(lon) {
		     case 1: sol=par[0]; break;
		     case 2: sol= Cantor (par[0],par[1]); break;
		     default:   
	           sol= Cantor(par[lon-2],par[lon-1]);
		         
	          for (int i=lon-3;i>=0;i--) {
	              sol = Cantor(par[i],sol);
	          }
	          break;
		 }
		 return sol;
	} 
	
	

	/**********************************************
	 * 
	 * 
	 * @param z
	 * @return
	 */
	
	public static long d(long z ){ 
		long sol=0;
	
		
		while (Cantor (sol,0)<=z) {
			sol++;
		}
		sol--;
		return sol;
	}
	
	/**********************************************
	 * 
	 * 
	 * @param z
	 * @return
	 */
	private static long DeCantor2Comp(long z ){ 
	
		long sol=0;
		
		sol = z - Cantor(d(z),0);
		
		return sol;
	}
	
	/**********************************************
	 * 
	 * 
	 * @param z
	 * @return
	 */
	private static long DeCantor1Comp(long z ){ 
		
		long sol=0;
		
		sol = d(z) - DeCantor2Comp(z);
		
		return sol;
	}

	/**********************************************
	 * 
	 * 
	 * @param z
	 * @param k
	 * @param i
	 * @return
	 */
	public static long DeCantor(long z, int k, int i ){ 
		long sol=0;

		if (i==1) sol=DeCantor1Comp(z);
		else {
				sol= DeCantor (DeCantor2Comp( z ), k-1,i-1);
		}
		if (i==1 & k==1) sol=z; 
		
		return sol;
	}
	
	
	
	/**********************************************
	 * 
	 * 
	 * @param par
	 * @return
	 */
	public static int L(long ... par){ 
		 int sol= par.length;
		 
		 return sol;
	}
	
	/**********************************************
	 * 
	 * 
	 * @param par
	 * @return
	 */
	public static long Godel(long ... par){ 
		
		 long sol    = 0;
		 int length = par.length;
		 long l = L(par);
		 
		 String parS=""+par[0];
		 for (int i=1;i<par.length;i++) {
			 parS+=","+par[i];
		 }

		 if (length!=0) {
			sol= Cantor(l-1,CantorN(par))+1;
		 }
		 //System.out.println("\t god ("+parS+")="+sol);

		 return sol;
	}
	/**********************************************
	 * 
	 * 
	 * @param z
	 * @return
	 */
	public  static long l(long z){ 
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
	
	public static long DeGodel(long z, int k){ 
		 long sol    = 0;
		
		 int l = (int) l(z);
		 System.out.println("l: "+l);
		 
		 if ((z>0) & k<=l){
			sol= DeCantor (DeCantor(z-1,2,2),l,k);
		 }
		 return sol;
	}
	/**********************************************
	 * Verifica que  la función Cantor (z) == Decantor (z)
	 * para vectores de longitud 3 a 5 de números aleatorios.
	 * 
	 * @return Verdadero si la codificación y decodificación es correcta
	 */
	
	private static boolean CheckCantor() {
		boolean ret = true;
		Random r = new Random();
		
		int len  = (int)nextLong(r,3,5);
		
		long [] cantor = new long[len];
		long solCantor=0;
		
		for (int i=0;i<len;i++)  cantor [i] = nextLong(r,0,10);

		solCantor = CantorN(cantor);
		
		long [] decantor = new long[len];
		
		for (int i=0;i<len;i++) decantor [i] = DeCantor(solCantor,len,i+1);

		for (int i=0;i<len;i++) ret&=(cantor [i] == decantor [i]);
		
		return ret;
	}
	
	/**********************************************
	 * Verifica que  la función Godel (z) == DeGodel (z)
	 * para vectores de longitud 3 a 5 de números aleatorios.
	 * 
	 * @return Verdadero si la codificación y decodificación es correcta
	 */
	private static boolean CheckGodel() {
		boolean ret = true;
		Random r = new Random();
		
		int len  = (int)nextLong(r,3,5);
		
		long [] godel = new long[len];
		long solGodel=0;
		
		for (int i=0;i<len;i++)  godel [i] = nextLong(r,0,10);

		solGodel = Godel(godel);
		
		long [] deGodel = new long[len];
		
		for (int i=0;i<len;i++) deGodel [i] = DeGodel (solGodel,i+1);

		for (int i=0;i<len;i++) ret&=(godel [i] == deGodel [i]);
		
		return ret;
	}
	
	/**********************************************
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		System.out.println("CheckCantor: "+CheckCantor());
		System.out.println("CheckGodel:  "+CheckGodel());
		
		long sol= Godel ( 10, 5, 7);
		System.out.println("sol: "+sol);
		long solDecator = DeGodel(sol,1);
		long solDecator2 = DeGodel(sol,2);
		long solDecator3 = DeGodel(sol,3);
		System.out.println("Decantor_2_1: "+solDecator);
		System.out.println("Decantor_2_2: "+solDecator2);
		System.out.println("Decantor_2_3: "+solDecator3);
	}
}
