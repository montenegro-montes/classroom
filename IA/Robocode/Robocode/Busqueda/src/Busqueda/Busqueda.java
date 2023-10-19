package Busqueda;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import Acciones.Accion;
import Main.Casilla;
import Main.Problema;

public abstract class Busqueda {

	Stack<Casilla> 		  abierto;
	Casilla 			  _inicial, _final;
	HashSet		<Casilla> cerrados;
	LinkedList	<Accion>  acciones_solucion;
	Casilla [] [] 		_mallaNodos;
	int 			nfilas,	nColumnas;
	Problema _p;
	String name = null; 
	
	int nodosExpandidos = 0;
	int maxAbiertos  	= -1;
	
	public Busqueda (Problema p){
		abierto 	 = new Stack<Casilla>();
		cerrados = new HashSet<Casilla>();
		
		_p 		 = p;
		_inicial = p.getInicial();
		_final   = p.getFinal();
		_mallaNodos = p.getMalla();
		
		acciones_solucion 	= new LinkedList<Accion>();
		
		nfilas = p.getnFila();
		nColumnas = p.getnColumna();
	}
	
	abstract public boolean ejecutar();
	
	public LinkedList<Accion> getCamino (){
	
	/*	for (int i=0;i<acciones_solucion.size();i++) {
			System.out.println(i+" "+acciones_solucion.get(i));
		}	*/
		return acciones_solucion;
	}	
	
	public String getName() {
		return name;
	}
		
	public Stack<Casilla> getAbierto(){
		return abierto;
	}
	
	public HashSet	<Casilla> getCerrados() {
		return  cerrados;
	}
	
	public int getNumNodosExtendidos() {
		return nodosExpandidos;
	}
	
	public int getNumMaxAbiertos() {
		return maxAbiertos;
	}
	
	

	public int getCoste (){
		int gasto = 0;
		
			for (int i=0;i<acciones_solucion.size();i++) {
				gasto +=acciones_solucion.get(i).getGasto();
			}	
		return gasto;
	}	
}
