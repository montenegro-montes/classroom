

package Heursiticas;

import Main.Casilla;

public class  HeuristicOctil extends Heuristic{


	 public int h(Casilla actual, Casilla objetivo){
			int deltaf = Math.abs(actual.getFila() - objetivo.getFila());
			int deltac = Math.abs(actual.getColumna() - objetivo.getColumna());
			
			int minimo = Math.min(deltaf, deltac);
			int maximo = Math.max(deltaf, deltac);
			
			return 142 * minimo + 100 * (maximo - minimo);
		}
	
	 
}
