/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package Geneticos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.jgap.*;

import Robocode.BattleObserver;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;


public class RobocodeFitnessFunction extends FitnessFunction {
	    
	    private RobocodeEngine engine;
	    private BattleSpecification battleSpecs;
	    private BattleObserver battleObserver;
	    private RobotSpecification evolvable;
	    private RobotSpecification[] otherRobots;
	    
	    private int numberOfBattles;
	    private int numeroDeEventos;
	    private static int numeroDeComportamientos;
	    private static int numeroDeAcciones;

	    /**************************
	     * 
	     * @param re
	     * @param robots
	     * @param cromosomasCfg
	     * @param nBatallas
	     */
	    
    public RobocodeFitnessFunction(RobocodeEngine re, RobotSpecification[] robots, int [] cromosomasCfg, int nBatallas) {
        engine = re;
        numberOfBattles = nBatallas;
        numeroDeEventos =  cromosomasCfg[0];
        numeroDeComportamientos = cromosomasCfg[1];
	    numeroDeAcciones = cromosomasCfg[2];
	    
        battleObserver = new BattleObserver();
        engine.addBattleListener(battleObserver);
        
        otherRobots = new RobotSpecification[robots.length-1];
        for (int i = 0, j = 0; i < robots.length; i++) {
            if (robots[i].getName().compareTo("evolvablerobot.EvolvableRobot") == 0) {
                evolvable = robots[i];
            }
            else {
                otherRobots[j++] = robots[i];
            }
        }

    }

    /**
    * 
    */
    public double evaluate(IChromosome a_subject) {
        int i = 0, j;
        
        try {
			storeChromosoma(a_subject); //ALMACENO EL CHROMOSOMA PARA QUE LO LEA EL GENETICO
		} catch (IOException e) {
			e.printStackTrace();
		}
   
    /****** IMPRIMO GEN ***/     
   	 System.out.print("GEN: ");
        int size=a_subject.size();
		for (int recorrido=0;recorrido<size;recorrido++)
			System.out.print(" "+(Integer)a_subject.getGene(recorrido).getAllele());
		 System.out.println("");
	/****** FIN IMPRIMO GEN ***/       
		
        double tempFitness;
        double fitness = 0;
        for (j = 0; j < otherRobots.length; j++) {
            RobotSpecification[] tempRobots = new RobotSpecification[2];
            tempRobots[0] = evolvable;
            tempRobots[1] = otherRobots[j];
            
            int numRondas=1;
            battleSpecs = new BattleSpecification(numRondas, new BattlefieldSpecification(800, 600), tempRobots);
            tempFitness= 0;
            
            System.out.println("\t\t Testing against " + j + " " + otherRobots[j].getName() + "...");
            
            for (i = 0; i < numberOfBattles; i++) {
                engine.runBattle(battleSpecs, true);
                double miScore 		= battleObserver.getScoreRobot();
                double enemigoScore = battleObserver.getScoreEnemy();
               
                tempFitness += miScore /300 - enemigoScore/500;
                	System.out.println("\t\t\t Battle "+i+ " miScore " + miScore + " enemigoScore " +enemigoScore);
            }
            
            tempFitness /= numberOfBattles;
            fitness += tempFitness;
            
            
        }
        fitness /= otherRobots.length;

        System.out.println("\t\t\t *******  Fitnes "+fitness);
        return Math.max(0.0d, fitness);
    }
    
/***************************************************************
 *   
 * @param chromosoma
 * @throws IOException
 */

    private void storeChromosoma(IChromosome chromosoma) throws IOException{
		int i,j,k;  
		String file="./robots/evolvablerobot/EvolvableRobot.data/genome";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	        k = 0;
	        for (i = 0; i < numeroDeEventos; i++) {
	            writer.write(chromosoma.getGene(k++).getAllele().toString());
	            if ((i + 1) < numeroDeEventos) {
	                writer.write("\t");
	            }
	        }
	        writer.write("\n");
	        writer.flush();
	        
	        for (i = 0; i < numeroDeComportamientos; i++) {
	            writer.write(chromosoma.getGene(k++).getAllele().toString());
	            if ((i + 1) < numeroDeComportamientos) {
	                writer.write("\t");
	            }
	        }
	        writer.write("\n");
	        writer.flush();
	        
	        for (j = 0; j < numeroDeComportamientos; j++) {
	            for (i = 0; i < numeroDeAcciones; i++) {
	                writer.write(chromosoma.getGene(k++).getAllele().toString());
	                if ((i + 1) < numeroDeAcciones) {
	                    writer.write("\t");
	                }
	            }
	            writer.write("\n");
	            writer.flush();
	        }
	        writer.close();
	}
	
 
}
