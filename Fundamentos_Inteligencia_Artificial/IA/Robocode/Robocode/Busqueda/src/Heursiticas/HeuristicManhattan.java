package Heursiticas;

import Main.Casilla;

public class  HeuristicManhattan extends Heuristic{

	 public  int h (Casilla actual, Casilla objetivo) {
		 int h=0;
			
			h = 		   Math.abs(objetivo.getFila() 	  - actual.getFila()) +
					   Math.abs(objetivo.getColumna()  - actual.getColumna());
			return h;
	}
	 
}
