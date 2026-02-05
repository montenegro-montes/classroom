package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Casilla implements Comparable{
	int _fila, _columna;
	boolean _esPuntoFinal = false, _esPuntoInicial = false, _esObstaculo = false;
	int _f,_h,_g=0;
	Configuracion cfg;
	

	
	Casilla _padre	= null;
	
		
	
	public Casilla (int filaP, int columnaP, Casilla padreP){
		ini();
		
		_fila 		= filaP;
		_columna 	= columnaP;
		_padre		= padreP;
	}
	
	private void ini() {
		cfg  = new Configuracion();
	}
	
	public boolean esObstaculo () {
		return _esObstaculo;
	}
	
	void ponerObstaculo() {
		_esObstaculo = true;
	}
	
	boolean esPuntoFinal () {
		return _esPuntoFinal;
	}
	
	void ponerPuntoFinal() {
		_esPuntoFinal = true;
	}
	
	boolean esPuntoInicial () {
		return _esPuntoInicial;
	}
	
	void ponerPuntoInicial() {
		_esPuntoInicial = true;
	}
	
	boolean esLibre () {
		return !_esPuntoInicial & !_esPuntoFinal & !_esObstaculo ;
	}
	
	public double posicionFilaRobot() {
		int tamanhoCelda = cfg.getTamCelda();
		return (_fila * (double)  tamanhoCelda + tamanhoCelda / 2);
	}
	
	public double posicionColumnaRobot() {
		int tamanhoCelda = cfg.getTamCelda();
		return (_columna * (double)  tamanhoCelda + tamanhoCelda / 2);
	}
	
	public int getFila () {
		return  _fila;
	}
	
	public int getColumna () {
		return _columna;
	}
	
	
	
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + _columna;
	    result = prime * result + _fila;
	    return result;
	}

	
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    if (obj == null) {
	        return false;
	    }
	  
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    
	    Casilla other = (Casilla) obj;
	    
	    if (( other._columna == this._columna) &&
	    	   ( other._fila  == this._fila)	 ) {
	    	   return true;
	    }
	    else 
	    	return false;	
	  
	}
	
	
	 public int compareTo(Object o) {
	        Casilla e1 = (Casilla) o;
	        
	       if ( this.getF() == e1.getF() ) return 0;
	       else if ( this.getF() > e1.getF() ) return 1;
	       else return -1;
	 }
	 
		public String toString() {
			//return ("Fila: "+_fila+" Columna: "+_columna);
			return ("("+_fila+" , "+_columna+") f: "+_f+" g: "+_g+" h:"+_h+ " Padre: "+_padre);
		}
		
		public void setH(int h) {
			_h = h;
			_f = _g +_h;
		}
		
		public void inc_gasto(int inc) {
			_g = _g + inc;
			_f = _g +_h;
		}
	
		public void setGasto (int g) {
			_g = g;
			_f = _g +_h;
		}
		
		
		public int getGasto () {
			return _g;
		}
		
		public int getF() {
			return _f;
		}
		
		public Casilla getPadre() {
			return _padre;
		}
		
		
		@SuppressWarnings("unchecked")
		public static void main(String[] args) {
			
			Casilla e1 = new Casilla(1,1);
			e1.setH(40);
			System.out.println(e1);
			
			Casilla e2 = new Casilla(2,2);
			e2.setH(0);
			System.out.println(e2);
			
			Casilla e3 = new Casilla(3,2);
			e3.setH(5);
			System.out.println(e3);

			Casilla e4 = new Casilla(3,3);
			e4.setH(15);
			System.out.println(e4);
			
			List<Casilla>  abiertos = new ArrayList<Casilla>  ();
			abiertos.add(e1); abiertos.add(e2); abiertos.add(e3);abiertos.add(e4);
			
			System.out.println(abiertos);
			
	        
	        
	       // Collections.reverseOrder();
	        Collections.sort(abiertos);    

			System.out.println(abiertos);
		}
}
