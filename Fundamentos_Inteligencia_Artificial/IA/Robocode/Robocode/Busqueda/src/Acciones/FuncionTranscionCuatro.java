package Acciones;

import java.util.ArrayList;

import Main.Casilla;
import Main.Problema;

public  class  FuncionTranscionCuatro extends FuncionTranscion{
	
	
	public FuncionTranscionCuatro (){
	     name= " cuatro movimientos ";

	}
	   public   ArrayList <Casilla> calcularEstados(Casilla actual, Problema p){
		     name= " cuatro movimientos ";

			 nfilas	      =  p.getnFila();
			 nColumnas    =  p.getnColumna();
			 _mallaNodos  =  p.getMalla();
			
		   int _fila = actual.getFila(), _columna = actual.getColumna();
			
			ArrayList<Casilla> sucesor = new ArrayList<Casilla>();
				
				//(0,0) // Izquierda (1,0) // Arriba 	 (0,1)
				
			if (!obstaculo (_fila,_columna))	{
				
				if (valido(_fila-1,_columna) && !obstaculo (_fila-1,_columna)) 
					sucesor.add( new Casilla(_fila-1,_columna,actual)); //Izquierda
				
				if (valido(_fila+1,_columna) && !obstaculo (_fila+1,_columna)) 
					sucesor.add( new Casilla(_fila+1,_columna,actual)); //Derecha
				
				if (valido(_fila,_columna+1) && !obstaculo (_fila,_columna+1)) 	
					sucesor.add( new Casilla(_fila, _columna+1,actual )); //Arriba
				
				if (valido(_fila,_columna-1) && !obstaculo (_fila,_columna-1)) 	
					sucesor.add( new Casilla(_fila, _columna-1,actual ) ); //Abajo
				
			}
		
		
				return sucesor;
		}
	   
       // 1: Up 2: Up-right 3: Right 4: Down-right
       // 5: Down 6: Down-left 7: Left 8: Up-left
       
       // Without diagonal movements the priority is:
       // 1: Up 2: Right 3: Down 4: Left
	   
}
