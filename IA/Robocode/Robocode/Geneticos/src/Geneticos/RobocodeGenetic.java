/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Geneticos;

import java.io.*;
import java.util.List;

import org.jgap.*;
import org.jgap.data.DataTreeBuilder;
import org.jgap.data.IDataCreators;
import org.jgap.impl.*;
import org.jgap.xml.XMLDocumentBuilder;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;

import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

/**
 *
 * @author hsatizab
 */
public class RobocodeGenetic {

	/******* VARIABLES GENETICOS ************/
		public  final  int numeroDeEventos = 7; 			//Prioridad de los eventos
		public  final  int numeroDeComportamientos = 11;   //Comportamientos disponibles
		public  final  int numeroDeAcciones = 6;			//Numero de Acciones por cada comportamiento
		public  final  int tamnhoComportamiento = numeroDeComportamientos * numeroDeAcciones;
	   // Gen sería 7 + 11 + 11*6 = 18 + 66 = 84 Enteros
		
		private final String cromosomaValues = "EvolvingRobocode.xml";
		
		int popSize = 1; //Population size
        int maxIter = 1; // Maximum generations
        private  RobocodeFitnessFunction myFitnessFunction;
	    private  Configuration configuration;
	    
		    
	    
	    /******* VARIABLES ROBOCODE ************/     
		
	    public  int numberOfBattles = 1;



	    
/*******************************
 * 	    
 * @param poplacionSize
 * @param maxIter
 */
	    public RobocodeGenetic (int poplacionSize, int maxIteraciones,int numeroBatllas){
	    	 popSize = poplacionSize;
	         maxIter = maxIteraciones;
	         numberOfBattles =numeroBatllas;
	         
	         try {
				archivoCfg();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

/****************	    
 *  Creo archivo de cfg para el robot, así sabe que tiene el cromosoma
 * @throws IOException
 */
	 private void archivoCfg() throws IOException{

			String dir="./robots/evolvablerobot/EvolvableRobot.data/";
			File DirCfg = new File(dir);
			String fileS=dir+"cfg.dat";
			
			if (!DirCfg.exists())  DirCfg.mkdir();
			File fileCfg = new File(fileS);
			if(!fileCfg.exists()) {
			    fileCfg.createNewFile();
			} 
			
			PrintStream w = null;
				w = new PrintStream(new FileOutputStream(fileS));
				w.println(numeroDeEventos);
				w.println(numeroDeComportamientos);
				w.println(numeroDeAcciones);
				w.close();
	 }

	 /*******************************
	  *  setGeneticConfiguration
	  *  		
	  */

	public void setGeneticConfiguration(RobocodeEngine engine,RobotSpecification[] robots) throws InvalidConfigurationException{
		
		configuration = new DefaultConfiguration();
				
		int i, j;
        Gene[] sampleGenes = new Gene[numeroDeEventos+numeroDeComportamientos+(tamnhoComportamiento)];
        for (i = 0, j = 0; j < numeroDeEventos; i++, j++) {               // Event priorities
            sampleGenes[i] = new IntegerGene(configuration, 0, 100);
        }
        for (j = 0; j < numeroDeComportamientos; i++, j++) {                     // Event subsumption
            sampleGenes[i] = new IntegerGene(configuration, 0, 100);
        }
        for (j = 0; j < numeroDeComportamientos; j++) {
            sampleGenes[i++] = new IntegerGene(configuration, -20, 100);   // Forward
            sampleGenes[i++] = new IntegerGene(configuration, 0, 6);       // Turn robot action
            sampleGenes[i++] = new IntegerGene(configuration, -90, 90);    // Turn angle
            sampleGenes[i++] = new IntegerGene(configuration, 0, 2);       // Turn gun action
            sampleGenes[i++] = new IntegerGene(configuration, -90, 90);    // Turn gun angle
            sampleGenes[i++] = new IntegerGene(configuration, 0, 3);       // Fire
        }

        IChromosome sampleChromosome = new Chromosome(configuration, sampleGenes);
        configuration.setSampleChromosome(sampleChromosome);

        configuration.setPopulationSize(popSize);

        int []cromosomaCfg = new int [3];
        cromosomaCfg[0] = numeroDeEventos;
        cromosomaCfg[1] = numeroDeComportamientos;
        cromosomaCfg[2] = numeroDeAcciones;
        
        
        myFitnessFunction = new RobocodeFitnessFunction(engine, robots,cromosomaCfg, numberOfBattles);
  
        configuration.setFitnessFunction(myFitnessFunction);
        
        //----------------------------------------------------------
        System.out.println(configuration.toString());
        List go = configuration.getGeneticOperators();
        System.out.println("Crossover rate: " + ((CrossoverOperator)go.get(0)).getCrossOverRatePercent());
        System.out.println("Allow full crossover: " + ((CrossoverOperator)go.get(0)).isAllowFullCrossOver());
        System.out.println("Mutation rate: " + ((MutationOperator)go.get(1)).getMutationRate());
        System.out.println("****************** FIN CFG GENETICOS *****************************\n");
		
	}
	
	 /*******************************
	  *  getPoblacionInicial
	  *  		
	  */	
	private Genotype getPoblacionInicial()  throws Exception{
	
		 Genotype population;
	        try {
	            Document doc = XMLManager.readFile(new File(cromosomaValues));
	            population = XMLManager.getGenotypeFromDocument(configuration, doc);
	        }
	        catch (UnsupportedRepresentationException uex) {
	            System.out.println("Previous population has a different configuration");
	            population = Genotype.randomInitialGenotype(configuration);
	        }
	        catch (FileNotFoundException fex) {
	            System.out.println("Previous population not found");
	            population = Genotype.randomInitialGenotype(configuration);
	        }
	        
	        return population;
	}

	 /*******************************
	  *  setPoblacionFinal
	  *  		
	  */	
	
	private void setPoblacionFinal( Genotype population)  throws Exception{
		 
		  	DataTreeBuilder builder = DataTreeBuilder.getInstance();
	        IDataCreators doc2 = builder.representGenotypeAsDocument(population);
	
	        // create XML document from generated tree
	        XMLDocumentBuilder docbuilder = new XMLDocumentBuilder();
	        Document xmlDoc = (Document) docbuilder.buildDocument(doc2);
	        XMLManager.writeFile(xmlDoc, new File(cromosomaValues));
	}

	 /*******************************
	  *  evolve
	  *  		
	  */	
	
	public void evolve(int mi) throws Exception {

			if (mi<1) return;
				 
		       int i;
	        
		    Genotype population = getPoblacionInicial();     
	        IChromosome mejorSolucion = null;
	        
	           
	        long startTime = System.currentTimeMillis();
	        for (i = 0; i < mi; i++) {
	        		System.out.println("Iteration: " +(i+1)+ " de: "+mi);
	
	        		population.evolve();
	         }
	        
	        long endTime = System.currentTimeMillis();
	        System.out.println("Total evolution time: " + (endTime - startTime) + " ms");
	       
	        System.out.println("****************************");
        		mejorSolucion = population.getFittestChromosome();
        	System.out.println("Best robot: "+mejorSolucion.getFitnessValue());
        		
        		int size=mejorSolucion.size();
        		for (int recorrido=0;recorrido<size;recorrido++)
        			System.out.print(" "+(Integer)mejorSolucion.getGene(recorrido).getAllele());
        		
        	System.out.println("\n****************************");
	        
	        setPoblacionFinal(population);      					//Guardo la poblacion en el xml
	        storeBestChromosoma(mejorSolucion,"genome"); 			//Almaceno el mejor cromosoma en Raiz
	        storeBestChromosoma(mejorSolucion,"./robots/evolvablerobot/EvolvableRobot.data/genome"); 	//Almaceno el mejor cromosoma como datos Robot
	    }

/**
 * @throws IOException *************************************************
 * 
 */
	
	private void storeBestChromosoma(IChromosome mejorSolucion,String path) throws IOException{
		int i,j,k;  
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
	        k = 0;
	        for (i = 0; i < numeroDeEventos; i++) {
	            writer.write(mejorSolucion.getGene(k++).getAllele().toString());
	            if ((i + 1) < numeroDeEventos) {
	                writer.write("\t");
	            }
	        }
	        writer.write("\n");
	        writer.flush();
	        
	        for (i = 0; i < numeroDeComportamientos; i++) {
	            writer.write(mejorSolucion.getGene(k++).getAllele().toString());
	            if ((i + 1) < numeroDeComportamientos) {
	                writer.write("\t");
	            }
	        }
	        writer.write("\n");
	        writer.flush();
	        
	        for (j = 0; j < numeroDeComportamientos; j++) {
	            for (i = 0; i < numeroDeAcciones; i++) {
	                writer.write(mejorSolucion.getGene(k++).getAllele().toString());
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
