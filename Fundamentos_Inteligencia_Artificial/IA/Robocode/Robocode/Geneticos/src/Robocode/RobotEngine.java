/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Robocode;

import java.io.*;
import java.util.List;

import org.jgap.*;
import org.jgap.data.DataTreeBuilder;
import org.jgap.data.IDataCreators;
import org.jgap.impl.*;
import org.jgap.xml.XMLDocumentBuilder;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;

import Geneticos.RobocodeFitnessFunction;
import Geneticos.RobocodeGenetic;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

/**
 *
 * @author 
 */
public class RobotEngine {

	
		
		int popSize = 1; //Population size
        int maxIter = 1; // Maximum generations
	    
	    /******* VARIABLES ROBOCODE ************/     
		
	    public  int numberOfBattles = 1;
	   


	    
/*******************************
 * 	    
 * @param poplacionSize
 * @param maxIter
 */
	    public RobotEngine (int poplacionSize, int maxIteraciones,int numeroBatllas){
	    	 popSize = poplacionSize;
	         maxIter = maxIteraciones;
	         numberOfBattles =numeroBatllas;
   }

/*******************************
 *  StartEngine
 * @throws Exception 
 *  		
 */
	    
	 public void StartEngine(boolean ejecucionVisible) throws Exception{
	
		 RobocodeEngine engine;
		 RobotSpecification[] robots;
		    
         engine = new RobocodeEngine(new File("."));
         engine.setVisible(ejecucionVisible);
         robots = engine.getLocalRepository();
         
         RobocodeGenetic genetico = new RobocodeGenetic (popSize, maxIter, numberOfBattles);
	             
         boolean evolvable = false;
         
         System.out.println("\n************************");
         System.out.println("Robots Disponibles: "+robots.length);
         
         for (int k = 0; k < robots.length; k++) {
             if (robots[k].getName().compareTo("evolvablerobot.EvolvableRobot") == 0) {
                 evolvable = true;
             }
             System.out.println("\t\t"+robots[k].getName());
         }
         System.out.println("************************  \n\n");
         
         if (evolvable && (robots.length > 2)) {
        	 genetico.setGeneticConfiguration(engine,robots);
        	 genetico.evolve(maxIter); 
         }
         else {
                 System.out.println("Error. Robot genético no encontrado, o no hay suficientes robots.");
         }
          
         engine.close();
         System.exit(0);
             
	}





	
/*************************************************
 * 	
 * @param args
 * @throws Exception
 */
	public static void main(String[] args) throws Exception {
	          
	    int poplacionSize  =10;   //Tamaño de la poblacion
	    int maxIteraciones =1; 	// Número de evoluciones de la poblacion	
	    int numeroBatallas =1;	// Número de batllas en cada evolucion
	    
		RobotEngine robot = new RobotEngine(poplacionSize,maxIteraciones,numeroBatallas);

		boolean ejecucionVisible = true;   //No observamos la batallas
		robot.StartEngine(ejecucionVisible);
			
	    }
			
  
}
