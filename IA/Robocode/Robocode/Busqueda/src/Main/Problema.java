package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Problema {

	
	int numPixelFila	;
	int numPixelCol;
	int tamCelda;       
	
	//número de obstáculos
	private int _numObstaculos ;
	
	//semilla para el generador de números aleatorios
	private long _semilla ;  
	
	private int nFil;
	private int nCol;
	
	private Casilla [] [] 	mallaNodos;
	List <Casilla> 		obstaculos;
	Casilla 			nodoInicial, nodoFinal;
	
	Random consAleatorio;
	
	public Problema (Configuracion cfg){
		
		
		_numObstaculos 	= cfg.getNumObstaculos();
		_semilla 	= cfg.getSemilla();
		
		consAleatorio 	= new Random(_semilla);
		obstaculos 	= new ArrayList<Casilla> ();
		
		numPixelFila = cfg.getNumPixelFila();
		numPixelCol  = cfg.getNumColumna();
		tamCelda 	 = cfg.getTamCelda();
		
		nFil 		= cfg.getNumFila();
		nCol 		= cfg.getNumColumna();
		
		mallaNodos = new Casilla [nFil] [nCol];
		
		inicializaMalla();
		
		int filaAleatorio,columnaAleatorio;
		
		for (int i=0;i<_numObstaculos;) {
			 filaAleatorio    = consAleatorio.nextInt(nFil);
			 columnaAleatorio = consAleatorio.nextInt(nCol);

			 if (posicionLibre(filaAleatorio,columnaAleatorio)){
				 mallaNodos[filaAleatorio][columnaAleatorio].ponerObstaculo();
				 obstaculos.add(new Casilla (filaAleatorio,columnaAleatorio));
				 i++;
			 }
		}
		
		
		puntoInicial_Final(true);
		puntoInicial_Final(false);
					

		imprimirMalla();	
		
	}
	
	private void puntoInicial_Final(boolean inicial){
		int filaAleatorio,columnaAleatorio;

		do {
			 filaAleatorio    = consAleatorio.nextInt(nFil);
			 columnaAleatorio = consAleatorio.nextInt(nCol);
			}
		while (!posicionLibre(filaAleatorio,columnaAleatorio));  
			
		if (inicial) {
				nodoInicial = new Casilla (filaAleatorio,columnaAleatorio);
				mallaNodos[filaAleatorio][columnaAleatorio].ponerPuntoInicial();
		}
		else {
				mallaNodos[filaAleatorio][columnaAleatorio].ponerPuntoFinal();
				nodoFinal = new Casilla (filaAleatorio,columnaAleatorio);
				nodoFinal.ponerPuntoFinal();
		}

	}
	
	private void imprimirMalla(){
		
		

		System.out.print("     ");
		for (int c = 0; c < nCol; c++){	
			if (c<10)
				System.out.print(" 0"+ c +" ");
			else System.out.print(" "+ c +" ");
		}
		System.out.println();

		
		for (int f = 0; f < nFil; f++){
			
			if (f<10)
				System.out.print(" 0"+ f +" ");
			else System.out.print(" "+ f +" ");
			
			for (int c = 0; c < nCol; c++){	
				if (mallaNodos [f][c].esObstaculo()) 
					System.out.print("  X ");
				else 
					if (mallaNodos [f][c].esPuntoInicial()) 
						System.out.print("  I ");
					else 
						if (mallaNodos [f][c].esPuntoFinal()) 
							System.out.print("  F ");
						else 	System.out.print("  - ");
			}
			
			System.out.println();
		}
		
	}
	

	private void inicializaMalla() {
		
		for (int f = 0; f < nFil; f++){
			for (int c = 0; c < nCol; c++){
				mallaNodos [f] [c] = new Casilla(f,c);
			}
		}
	}

	

	private boolean posicionLibre(int fila, int columna) {
		
		return (mallaNodos[fila] [columna].esLibre() );
				
	}
	
	public double getFilaPosicionIncial () {
		return nodoInicial.posicionFilaRobot();
	}

	public double getColumnaPosicionIncial () {
		return nodoInicial.posicionColumnaRobot();
	}
	
	public Casilla getObstaculo(int i) {
		return obstaculos.get(i);
	}
	
	public double getFilaObstaculo (int i) {
		return getObstaculo (i).posicionFilaRobot();
	}
	
	public double getColumnaObstaculo (int i) {
		return getObstaculo (i).posicionColumnaRobot();
	}
	
	
	
	public void imprimeObstaculos() {
		
		for (int i=0;i<_numObstaculos;i++) {
			System.out.println(obstaculos.get(i) 
					+ " F: "+getObstaculo (i).posicionFilaRobot() 
					+ " C: "+ getObstaculo (i).posicionColumnaRobot());
			
		}
		
	}
	
	public Casilla getInicial() {
		return nodoInicial;
	}
	
	public Casilla getFinal() {
		return nodoFinal;
	}
	
	public List <Casilla> 	getObstaculos(){
		return obstaculos;
	}
	
	public Casilla [] [] getMalla (){
		return mallaNodos;
	}
	
	public int getnFila() {
		return nFil;
	}
	
	public int getnColumna() {
		return nCol;
	}
}
