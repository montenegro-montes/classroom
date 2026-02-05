package Acciones;


import Main.Casilla;

public class Accion  {
		
	public enum accion {izquierda,izquierdaNorte,izquierdaSur,derecha,derechaNorte,derechaSur,arriba,abajo};
	
	accion action; 
	int n1F, n1C;
	int n2F, n2C;	
	
	public Accion (Casilla n1, Casilla n2){
		super();
		 n1F=n1.getFila(); n1C = n1.getColumna();
		 n2F=n2.getFila(); n2C = n2.getColumna();	
			
		 if (n1F== n2F){
					 if(n1C < n2C)
					 	action = accion.arriba;
					  else 
					 	action = accion.abajo;
		 }
		 else{
			 if(n1F < n2F){
			 	 if(n1C == n2C)
			 		action = accion.derecha;
			 	else if (n1C < n2C)
			 		 action = accion.derechaNorte;
			 		else
			 		  action = accion.derechaSur;
			 } else {
			 	
			 	 if(n1C == n2C)
			 		action = accion.izquierda;
			 	else if (n1C < n2C)
			 		 action = accion.izquierdaNorte;
			 		else
			 		 action = accion.izquierdaSur;
			 }
		 }
	
	}
	
	public int getGasto () {
		if ( action== accion.abajo || action== accion.arriba || action== accion.izquierda || action== accion.derecha  )
			return 100;
		else 
			return 142;
					
	}
	
	public int getGrados() {
		int grados = 0;
		
		switch (action) {
			case arriba: 	    	 grados = 0; 	break;
			case derechaNorte:   grados = 45; 	break;
			case derecha:		 grados = 90; 	break;
			case derechaSur: 	 grados = 135; 	break;
			case abajo: 	   		 grados = 180; 	break;
			case izquierdaSur:   grados = 225; 	break;
			case izquierda: 	     grados = 270; 	break;
			case izquierdaNorte: grados = 315; 	break;
		}

		
		return grados;
	}
	
	public String toString() {
		return "("+n1F+","+n1C+")"+
				   " ("+n2F+","+n2C+")"+
				" Acción: " + action;
	}
	
	public accion getAccion() {
		return action;
	}
	
	public int getFilaInicial() {
		return n1F;
	}
	
	public int getColumnaInicial() {
		return n1C;
	}
	
	public int getFilaFinal() {
		return n2F;
	}
	
	public int getColumnaFinal() {
		return n2C;
	}
	
}
