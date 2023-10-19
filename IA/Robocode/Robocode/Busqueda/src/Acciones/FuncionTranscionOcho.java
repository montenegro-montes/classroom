package Acciones;

import java.util.ArrayList;

import Main.Casilla;
import Main.Problema;

public  class  FuncionTranscionOcho extends FuncionTranscion{
	
	
	public FuncionTranscionOcho () {
	     name= " ocho movimientos ";
	}
	
	
	   public   ArrayList <Casilla> calcularEstados(Casilla actual, Problema p){
		   
		    
			 nfilas		  =  p.getnFila();
			 nColumnas    =  p.getnColumna();
			 _mallaNodos  =  p.getMalla();
			
		   int _fila = actual.getFila(),_columna = actual.getColumna();
			
			ArrayList<Casilla> sucesor = new ArrayList<Casilla>();
			
			if (!obstaculo (_fila,_columna)) {
			
				if (valido(_fila-1,_columna) && !obstaculo (_fila-1,_columna) )
					sucesor.add( new Casilla(_fila-1,_columna,actual));  //Izquierda
				
				if (valido(_fila-1,_columna+1) && !obstaculo (_fila-1,_columna+1)  && !obstaculo (_fila-1,_columna) && !obstaculo (_fila,_columna+1)) 
					sucesor.add( new Casilla(_fila-1,_columna+1,actual)); //IzquierdaNorte
				
				if (valido(_fila-1,_columna-1) && !obstaculo (_fila-1,_columna-1) && !obstaculo (_fila-1,_columna) && !obstaculo (_fila,_columna-1)) 
					sucesor.add( new Casilla(_fila-1,_columna-1,actual)); //IzquierdaSur
				
				if (valido(_fila+1,_columna) && !obstaculo (_fila+1,_columna)) 
					sucesor.add( new Casilla(_fila+1,_columna,actual)); //Derecha
				
				if (valido(_fila+1,_columna+1) && !obstaculo (_fila+1,_columna+1) && !obstaculo (_fila,_columna+1) && !obstaculo (_fila+1,_columna))
					sucesor.add( new Casilla(_fila+1,_columna+1,actual)); //DerechaNorte
				
				if (valido(_fila+1,_columna-1) && !obstaculo (_fila+1,_columna-1)  && !obstaculo (_fila+1,_columna) && !obstaculo (_fila,_columna-1)) 
					sucesor.add( new Casilla(_fila+1,_columna-1,actual)); //DerechaSur
				
				if (valido(_fila,_columna+1) && !obstaculo (_fila,_columna+1) ) 	
					sucesor.add( new Casilla(_fila, _columna+1,actual )); //Arriba
				
				if (valido(_fila,_columna-1) && !obstaculo (_fila,_columna-1)) 	
					sucesor.add( new Casilla(_fila, _columna-1,actual ) ); //Abajo
			}
			
			return sucesor;
		}
	   
}
