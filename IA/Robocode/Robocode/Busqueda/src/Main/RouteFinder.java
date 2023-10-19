package Main;


import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;

/**
 * 
 * @date   2018-03-22
 * 
 * Plantilla para la práctica de algoritmos de búsqueda con Robocode (G. Ing. Comp.)
 * 
 * 
 */

public class RouteFinder {

	public static void main(String[] args) {
		
		
		//Creamos un mapa con los datos especificados
		// Create the battlefield
		
	
		//AQUÍ SE DEBERÁ DE GENERAR EL MAPA DE OBSTÁCULOS Y LAS POSICIONES INICIAL Y FINAL DEL ROBOT
		
		Configuracion cfg = new Configuracion();	
		Problema nuevoProblema = new Problema (cfg);
		//nuevoProblema.imprimeObstaculos();
		
		
		// Crear el RobocodeEngine desde la instalación 
		RobocodeEngine engine =
				//new RobocodeEngine(new java.io.File("C:/Robocode")); //Windows
				new RobocodeEngine(new java.io.File("/Users/joseamontenegromontes/robocode"));
		
		engine.setVisible(true); // Mostrar el simulador de Robocode
		
		
		BattlefieldSpecification battlefield =
				new BattlefieldSpecification(cfg.getNumPixelFila(), cfg.getnumPixelCol());
		
		double orientacion = 0.0;
		
		// En modelRobots recogemos la especificación de los robots que utilizaremos en la simulación.
		// En este caso serán un robot sittingDuck y nuestro propio robot. La referencia a nuestro robot
		// debe ser relativa al proyecto que pusimos en Options>Preferences>DevelopmentOptions en Robocode,
		// indicando el nombre del paquete (si lo hay) y del robot.  En nuestro caso suponemos como nombre 
		// prueba.Bot3*
		// Al nombre de los robots definidos por el usuario siempre hay que añadirle el carácter * al final. 
		RobotSpecification[] modelRobots =
				engine.getLocalRepository ("sample.SittingDuck,robots.BusquedaRobot*");
		
		// Incluiremos un robot sittingDuck por obstáculo, más nuestro propio robot.
		RobotSpecification[] existingRobots  = new RobotSpecification[cfg.getNumObstaculos()+1];
		RobotSetup		  [] robotSetups 	= new RobotSetup[cfg.getNumObstaculos()+1];
		
		/*
	     * Creamos primero nuestro propio robot y lo colocamos en la posición inicial del problema,
	     * que debería estar libre de obstáculo.
		 */
		
		
		int indice = 0;
		
		existingRobots[indice]=modelRobots[1];

		robotSetups[indice]=new RobotSetup(  nuevoProblema.getFilaPosicionIncial()        ,        //AQUÍ DEBE FIGURAR LA FILA EN PIXELS CORRESPONDIENTE A LA POSICIÓN INICIAL DEL ROBOT
						 nuevoProblema.getColumnaPosicionIncial()     ,        //AQUÍ DEBE FIGURAR LA COLUMNA EN PIXELS CORRESPONDIENTE A LA POSICIÓN INICIAL DEL ROBOT    
						 orientacion);             	 //orientación inicial

		
		/*
	     * Creamos un robot sittingDuck por cada obstáculo, y lo colocamos en el centro de la 
	     * celda correspondiente.
		 */
			
		
		//AQUÍ SE DEBERÍA CREAR UN ROBOT sittingDuck EN LA POSICIÓN DE CADA OBSTÁCULO DE LA MALLA.
	
		indice++;
	
		for (int i=0; i<cfg.getNumObstaculos();i++) {
					
					existingRobots[indice]=modelRobots[0];   //sittingDuck
					
					robotSetups[indice]=  new RobotSetup( 		
						nuevoProblema.getFilaObstaculo(i),    	//AQUÍ DEBE FIGURAR LA FILA EN PIXELS CORRESPONDIENTE AL CENTRO DE LA COLUMNA DONDE HAY OBSTÁCULO
						nuevoProblema.getColumnaObstaculo(i), 	//AQUÍ DEBE FIGURAR LA COLUMNA EN PIXELS CORRESPONDIENTE AL CENTRO DE LA CELDA DONDE HAY OBSTÁCULO							
						orientacion);    			//orientación
					indice++;
		}	
	
			
		System.out.println("Generados " + (indice -1) + " sitting ducks.");
					
		/* 
		 * Crear y desarrollar la batalla con los robots antes definidos
		 */
		
		
		BattleSpecification battleSpec =
				new BattleSpecification(battlefield,
						cfg.numberOfRounds,
						cfg.inactivityTime,
						cfg.gunCoolingRate,
						cfg.sentryBorderSize,
						cfg.hideEnemyNames,
						existingRobots,
						robotSetups);
		
		
		// Ejecutar la simulación el tiempo especificado
		engine.runBattle(battleSpec, true); 
		// Cerrar la simulación
		engine.close();
		// Asegurarse de que la MV de Java se cierra adecuadamente.
		System.exit(0);
	}
	
}
