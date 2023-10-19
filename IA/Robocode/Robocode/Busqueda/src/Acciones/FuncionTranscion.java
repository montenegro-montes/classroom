package Acciones;

import java.util.ArrayList;

import Main.Casilla;
import Main.Problema;

public abstract class  FuncionTranscion {
	
	static int nfilas, nColumnas;
	static Casilla [][] _mallaNodos;
	String name;
	  
		public   abstract  ArrayList <Casilla> calcularEstados(Casilla actual, Problema p);
	
		protected static boolean valido (int f, int c) {
			return ((f>=0) && (f<nfilas) && (c>=0) && (c<nColumnas));
		}
		
		protected static boolean obstaculo (int f, int c) {
			boolean obs = false;
			
			if (valido(f,c)) {
				obs = _mallaNodos [f] [c].esObstaculo();
			}
			return obs;
		}
		
		public String getName() {
			return name;
		}
	   
}
