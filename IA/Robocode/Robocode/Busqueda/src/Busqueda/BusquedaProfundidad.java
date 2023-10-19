package Busqueda;

import java.util.ArrayList;
import java.util.LinkedList;

import Acciones.Accion;
import Acciones.FuncionTranscion;
import Acciones.FuncionTranscionOcho;
import Main.Casilla;
import Main.Configuracion;
import Main.Problema;

public class BusquedaProfundidad  extends Busqueda{

	FuncionTranscion _fa;
	
	/* LIFO - Pila */
	
	public BusquedaProfundidad(Problema p, FuncionTranscion fa) {
		super(p);
		_fa = fa;
		name = "Profundidad "+ fa.getName();
	}

	
	public boolean ejecutar() {		
		Casilla 				actual = null;
		ArrayList <Casilla> 	sucesores;
		
		abierto.push	(_inicial);
		
		boolean find		=	false;
		
		while (!abierto.empty() & !find ) {
			
			//actual = (Casilla) abierto.pop();
			actual = (Casilla) abierto.remove(0);
			nodosExpandidos++;
			cerrados.add(actual);  					
			
			if (actual.equals(_final)) 
				find = true;
			
			else{
				sucesores = _fa.calcularEstados(actual,_p);
				
				for (Casilla e: sucesores) {
					
					if ( !cerrados.contains(e)) {
							abierto.push(e);
					}
				}
				//System.out.println("abierto:" +abierto);
				if (abierto.size() > this.maxAbiertos)
					  maxAbiertos = abierto.size();
			}
		}	
		
	    
		 if (find) {
			 //System.out.println("SOLUCION "+actual);
			 
			 Casilla working = actual;
			 Casilla _padre  = working.getPadre();
			 
			 while (_padre != null) { 
				 	Accion a = new Accion (_padre,working);
				 	acciones_solucion.addFirst(a);
				 	working  = _padre;
				 	_padre   = working.getPadre();
			 }
	    	 	 
		 }
	
		
		return find;
	}
	

	
	public static void main(String[] args) {
		
		
		Configuracion cfg = new Configuracion();	
		Problema nuevoProblema = new Problema (cfg);
		nuevoProblema.imprimeObstaculos();
		
		
		//List <Casilla> obs = nuevoProblema.getObstaculos();
		//Casilla c_inicial = nuevoProblema.getInicial();
		//Casilla c_final   = nuevoProblema.getFinal();
		//System.out.println("final: " +c_final+ " \n");
		
		BusquedaProfundidad b = new BusquedaProfundidad(nuevoProblema,new FuncionTranscionOcho());
		System.out.println("Encontrado  "+ b.ejecutar());
		
		@SuppressWarnings("unused")
		LinkedList <Accion> camino = b.getCamino();
		
	}
		
		
}
