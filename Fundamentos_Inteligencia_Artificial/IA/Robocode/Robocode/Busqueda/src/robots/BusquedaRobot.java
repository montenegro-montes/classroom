package robots;

import static robocode.util.Utils.normalRelativeAngleDegrees;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import Acciones.Accion;
import Acciones.FuncionTranscionCuatro;
import Acciones.FuncionTranscionOcho;
import Busqueda.Busqueda;
import Busqueda.BusquedaAStar;
import Busqueda.BusquedaAmplitud;
import Busqueda.BusquedaProfundidad;
import Busqueda.BusquedaVoraz;
import Heursiticas.HeuristicManhattan;
import Heursiticas.HeuristicOctil;
import Main.Casilla;
import Main.Configuracion;
import Main.Problema;
import robocode.Robot;

/**
 * 
 */

/**
 * @date 2018-03-22
 * 
 * Plantilla para la definición de un robot en Robocode
 *
 */
@SuppressWarnings("unused")
public class BusquedaRobot extends Robot {

	Problema nuevoProblema;
	int accionInt=0;
	Accion accion;
	Configuracion cfg;
	int tamCelda;
	
	Busqueda b;
	//The main method in every robot
	public void run() {
		
		
		//System.out.println("Iniciando ejecución del robot");
		
		// Nuestro robot será rojo
		setAllColors(Color.red);

		//DATOS QUE DEBEN COINCIDIR CON LOS DEL PROGRAMA main DE LA CLASE RouteFinder
	
		
		//Orientamos inicialmente el robot hacia arriba
		turnRight(normalRelativeAngleDegrees(0 - getHeading()));
		
		
		

		// AQUí DEBE:
		//  1. GENERARSE EL PROBLEMA DE BUSQUEDA
		//  2. BUSCAR LA SOLUCIÓN CON UN ALGORITMO DE BÚSQUEDA
		//  3. EJECUTAR LA SOLUCIÓN ENCONTRADA
		

		
		cfg = new Configuracion();		
		nuevoProblema = new Problema (cfg);
		//nuevoProblema.imprimeObstaculos();
		tamCelda = cfg.getTamCelda();
		
		Casilla _final = nuevoProblema.getFinal();
		//System.out.println("CASILLA FINAL "+_final+" "+_final.posicionFilaRobot()+ " "+_final.posicionColumnaRobot());

			//	b = new BusquedaAmplitud(nuevoProblema,new FuncionTranscionOcho());
		         //b 	= new BusquedaVoraz(nuevoProblema,new HeuristicOctil(),new FuncionTranscionOcho());
			b 	= new BusquedaAStar(nuevoProblema,new HeuristicOctil(),new FuncionTranscionOcho());
		
	
	// b 				= new BusquedaAStar(nuevoProblema,new HeuristicOctil(),new FuncionTranscionCuatro());
	//	b 				= new BusquedaAStar(nuevoProblema,new HeuristicManhattan(),new FuncionTranscionOcho()));
	//	 b 				= new BusquedaVoraz(nuevoProblema,new HeuristicOctil(),new FuncionTranscionCuatro());
	//	 b 				= new BusquedaProfundidad(nuevoProblema,new FuncionTranscionOcho());
	//	 b 				= new BusquedaAmplitud(nuevoProblema,new FuncionTranscionCuatro());
		 
		boolean 	      find  				= b.ejecutar();
				
	    String cfgString = "Algoritmo: "+b.getName()+ " Semilla: "+cfg.getSemilla()+ " Obstáculos: "+ cfg.getNumObstaculos();
	    System.out.println      (cfgString);
	    System.out.println      ("Encontrado?  "+ find);
		
		if (find) {
			LinkedList<Accion> camino = b.getCamino();
			int 	   orientacion;
			
			for (accionInt = 0; accionInt<camino.size();accionInt++) {
				accion = camino.get(accionInt);
				
				orientacion =  accion.getGrados();
				System.out.println(accionInt +" "+accion+" "+ orientacion);
		
				
				//ROBOT
				turnRight(normalRelativeAngleDegrees(orientacion - getHeading()));
				
				 Acciones.Accion.accion action = accion.getAccion();
				 double avance=0.0;
				 
				 //System.out.println("accion: "+action);
				 
				 if ( action ==  Acciones.Accion.accion.arriba 	||
					 action ==  Acciones.Accion.accion.abajo 	||
					 action ==  Acciones.Accion.accion.izquierda ||
					 action ==  Acciones.Accion.accion.derecha) {
					  
					 	
				 	 avance = tamCelda ;
				 
				//	 System.out.println("Avance Lineal "+avance );
					 
					 ahead(avance); 
				 }
				 else  {
					 // avance = Math.floor(tamCelda + Math.sqrt(tamCelda) + Math.sqrt(tamCelda));
					 avance = Math.floor(tamCelda * Math.sqrt(2)); //?????
					 //System.out.println("Avance Diagonal "+avance );
					 ahead(avance); 
				 }	
			}
			
			System.out.println("Nodos Expandidos: "+b.getNumNodosExtendidos()+
    		" Abiertos: "+b.getAbierto().size()+ " Coste: "+b.getCoste());
			
			// Victory dance
			for (int i = 0; i < 10; i++) {
				turnRight(30);
				turnLeft(30);
			}
		}
		else {
			int k = 0;
			while(k < 20){
				turnRight(90);
				k++;
			}
		}
		
	}
	
	
	
	public void onPaint(Graphics2D g) {

		
		
		Casilla _final 	 = nuevoProblema.getFinal();
		Casilla _inicial = nuevoProblema.getInicial();
		
		
		
		// Inicial
	    g.setColor(Color.GREEN);
	    g.fillRect((int)_inicial.posicionFilaRobot()+15,(int) _inicial.posicionColumnaRobot()+15, 10, 10);
	    
		// DESTINO
	    //g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
	    g.setColor(Color.RED);
	    g.fillRect((int)_final.posicionFilaRobot()+15,(int) _final.posicionColumnaRobot()+15, 10, 10);
		
	    
	    
	    //Cuadrículas
	    int fila 	 	= cfg.getNumColumna();
	    int columna  	= cfg.getNumFila();
	    int tamCelda 	= cfg.getTamCelda();
	    int filaPixels 	= cfg.getNumPixelFila();
	    
		g.setPaint(Color.white);

	    for (int i=0; i<columna;i++)
	    		g.drawLine(i*tamCelda+1, 0, i*tamCelda+1, filaPixels);
	    
	    for (int i=0; i<fila;i++)
	    		g.drawLine (0, i*tamCelda+1, filaPixels, i*tamCelda+1);
	    
	    // Información acciones.
	    /*g.setPaint(Color.white);
	    g.setFont(new Font("Serif", Font.BOLD, 16));
	    
	    
	    String cfgString = "Algoritmo: "+b.getName()+ " Semilla: "+cfg.getSemilla()+ " Obstáculos: "+ cfg.getNumObstaculos();
	    g.drawString(cfgString, 5, 25);
	    
	    String s = "No solución";
	    
	    if (accion!=null) 
	    		s= "* "+accionInt +" "+accion+ " "+ "Nodos Expandidos: "+b.getNumNodosExtendidos()+
	    		" Abiertos: "+b.getAbierto().size()+ " Coste: "+b.getCoste();
	    
	    g.drawString(s, 5, 5);*/
	    
	    g.setPaint(Color.CYAN);
	    Stack <Casilla> abiertos = b.getAbierto();
	    
	    for (Casilla casilla: abiertos) {
		    g.fillRect((int)casilla.posicionFilaRobot()-20,(int) casilla.posicionColumnaRobot()-20, 5, 5);
	    }
	    
	    HashSet<Casilla> cerrados = b.getCerrados();

	    g.setPaint(Color.ORANGE);
	    for (Casilla casilla: cerrados) {
		    g.fillRect((int)casilla.posicionFilaRobot()-20,(int) casilla.posicionColumnaRobot()-20, 5, 5);
	    }
	    
	    
	   LinkedList<Accion> sol= b.getCamino();
	   g.setPaint(Color.MAGENTA);
	    for (Accion accion: sol) {
	    	Casilla c1 = new Casilla ( accion.getFilaInicial(), accion.getColumnaInicial());
	    	Casilla c2 = new Casilla ( accion.getFilaFinal(), accion.getColumnaFinal());
	    	
		    g.fillRect((int)c1.posicionFilaRobot()-20,(int) c1.posicionColumnaRobot()-20, 5, 5);
		    g.fillRect((int)c2.posicionFilaRobot()-20,(int) c2.posicionColumnaRobot()-20, 5, 5);

	    } 
	    
	    g.setFont(new Font("Serif", Font.BOLD, 16));
	    g.setPaint(Color.MAGENTA);
	
	    String cfgString = "Camino";
	    g.drawString(cfgString, 5, 25);
	   
	    g.setPaint(Color.ORANGE);
	    cfgString = "Cerrados";
	    g.drawString(cfgString, 65, 25);
	    
	    g.setPaint(Color.CYAN);
	    cfgString = "Abiertos";
	    g.drawString(cfgString, 135, 25);
	    
	    g.setPaint(Color.GREEN);
	    cfgString = "Inicio";
	    g.drawString(cfgString, 5, 5);
		
	    g.setPaint(Color.RED);
	    cfgString = "Fin";
	    g.drawString(cfgString, 65, 5);
	    
	}
	
	
}
