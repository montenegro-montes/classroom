package Busqueda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Acciones.Accion;
import Acciones.FuncionTranscion;
import Acciones.FuncionTranscionOcho;
import Heursiticas.Heuristic;
import Heursiticas.HeuristicManhattan;
import Main.Casilla;
import Main.Configuracion;
import Main.Problema;

public class BusquedaAStar extends Busqueda {

	 Heuristic 			_heuristic;
	 FuncionTranscion 	_fa;

	public BusquedaAStar(Problema p, Heuristic heuristic, FuncionTranscion fa) {
		super(p);
		 _heuristic =  heuristic;
		 _fa 		=  fa;
		 name = "AStar "+fa.getName();
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean ejecutar() {
		
		Casilla 				actual		= null;
		ArrayList <Casilla> 	sucesores;
		
		
		int h =  _heuristic.h(_inicial,_final);
		
		_inicial.setGasto(0);
		_inicial.setH(h); //MEJORAR
		
		abierto.add(_inicial);
		
		//abierto.push	(_inicial);
		
		boolean find		=	false;
		
		while (!abierto.empty() & !find ) {
			
			actual = (Casilla) abierto.remove(0);
			nodosExpandidos++;
			
			cerrados.add(actual);  					
			
			//System.out.println("Actual:" +actual);
			
			if (actual.equals(_final)) 
				find = true;
			
			else{
				
				sucesores = _fa.calcularEstados(actual,_p);
				//System.out.println("sucesores:" +sucesores);
				
				for (Casilla e: sucesores) {
					
					if ( !cerrados.contains(e)) {
					//if ( !_obs.contains(e) && !cerrados.contains(e)) {
						
						h =  _heuristic.h (e,_final);
						e.setH(h); 
						
						
						int gasto = actual.getGasto();
						e.setGasto	(gasto+new Accion(actual,e).getGasto());
						
						if( abierto.contains(e)) {
							int  index= abierto.indexOf(e);
							Casilla casilla = abierto.get(index); //ToDo devulve null si no lo encuentra?
							
							if (e.getF() <  casilla.getF()) {
								abierto.remove(index);
								abierto.add(e);
							}
								
						}
						else {
							abierto.add(e);
						}
					}
				}
				Collections.sort(abierto);       
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
				 	Accion a =  new Accion (_padre,working);
				 
				 	//AccionCuatro a = new AccionCuatro (_padre,working); //ToDo:
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
		//nuevoProblema.imprimeObstaculos();
		
		//List <Casilla> obs = nuevoProblema.getObstaculos();
		//Casilla c_inicial = nuevoProblema.getInicial();
		//Casilla c_final   = nuevoProblema.getFinal();
		//System.out.println("final: " +c_final+ " \n");
		
		//BusquedaAStar b 	= new BusquedaAStar(nuevoProblema,new HeuristicOctil()),new FuncionTranscionOcho();
		BusquedaAStar b 		= new BusquedaAStar(nuevoProblema,new HeuristicManhattan(),new FuncionTranscionOcho());
		System.out.println("Encontrado  "+ b.ejecutar());
		
		//@SuppressWarnings("unused")
		LinkedList<Accion> camino 	= b.getCamino();
		
		/*for (int i=0;i<camino.size();i++) {
			System.out.println(camino.get(i));
		}	*/
	}
		
		
}
