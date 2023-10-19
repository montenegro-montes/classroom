/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolvablerobot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

/**
 *
 */

public class EvolvableRobot extends AdvancedRobot {

	 private  final int verbose = 1;
	    
	 private  int[] eventPriority;
	 private  boolean[] behaviourSubsumption;
	 private  int[][] behaviourActions;
	    
	 int numeroDeComportamientos 	= 11;
	 int numeroDeAcciones 			= 6;			
	 int numeroDeEventos 			= 7; 			//Prioridad de los eventos
	
	 /***** Ctes Comportamiento ***/
    public  final int defaultBehaviour 					= 0;
    public  final int bulletHitBehaviour 				= 1;
    public  final int bulletHitBulletBehaviour 			= 2;
    public  final int bulletMissedBehaviour 			= 3;
    public  final int hitByBulletBehaviour 				= 4;
    public  final int pushRobotBehaviour 				= 5;
    public  final int pushedByRobotBehaviour 			= 6;
    public  final int hitWallBehaviour				 	= 7;
    public  final int scannedCloseDistRobotBehaviour 	= 8;
    public  final int scannedMidDistRobotBehaviour 		= 9;
    public  final int scannedLongDistRobotBehaviour 	= 10;


/*************************************
 *    
 * @return
 */
    private boolean init() {
            
    	try {
			leerCfg();				//Leo la configuracion
		} catch (Exception e) {
			out.println(e);
		}
    	
 
         eventPriority 			= new int[numeroDeEventos];
         behaviourSubsumption 	= new boolean[numeroDeComportamientos];
	     behaviourActions 		= new int[numeroDeComportamientos][numeroDeAcciones];
	
	     if (!decodeGenome()) { 	//Leo el genoma que pone la funcion de fitness
	            return false;
	     }
        	
        setEventPriority("BulletHitEvent", eventPriority[0]);
        setEventPriority("BulletHitBulletEvent", eventPriority[1]);
        setEventPriority("BulletMissedEvent", eventPriority[2]);
        setEventPriority("HitByBulletEvent", eventPriority[3]);
        setEventPriority("HitRobotEvent", eventPriority[4]);
        setEventPriority("HitWallEvent", eventPriority[5]);
        setEventPriority("ScannedRobotEvent", eventPriority[6]);
        
        return true;
    }

/*************************************
 *    
 */
    public void run() {
        setColors(Color.BLACK, Color.BLUE, Color.YELLOW);
        
        init();
        
        if (verbose > 0) {
            out.println(Arrays.toString(eventPriority));
            out.println(Arrays.toString(behaviourSubsumption));
            for (int i = 0; i < numeroDeComportamientos; i++) {
                out.println(Arrays.toString(behaviourActions[i]));
            }
            out.flush();
        }
        
        while (true) {
            executeBehaviour(defaultBehaviour, 0);
            if (verbose > 1) {
                out.flush();
            }
        }
    }

/*************************************
 *    
 */
    public void onBulletHit(BulletHitEvent event) {
        executeBehaviour(bulletHitBehaviour, putInRange(event.getBullet().getHeading() - getHeading()));

        if (verbose > 1) {
            out.print("Target hit\t");
            out.println(putInRange(event.getBullet().getHeading() - getHeading()));
        }
    }

/*************************************
 *     
 */
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        executeBehaviour(bulletHitBulletBehaviour, putInRange(event.getBullet().getHeading() - getHeading()));

        if (verbose > 1) {
            out.print("Bullet hit\t");
            out.println(putInRange(event.getBullet().getHeading() - getHeading()));
        }
    }

/*************************************
 *     
 */
    public void onBulletMissed(BulletMissedEvent event) {
        executeBehaviour(bulletMissedBehaviour, putInRange(event.getBullet().getHeading() - getHeading()));

        if (verbose > 1) {
            out.print("Missed bullet\t");
            out.println(putInRange(event.getBullet().getHeading() - getHeading()));
        }
    }
/*************************************
 *    
 */
    public void onHitByBullet(HitByBulletEvent event) {
        executeBehaviour(hitByBulletBehaviour, event.getBearing());

        if (verbose > 1) {
            out.print("Hit by enemy\t");
            out.println(event.getBearing());
        }
    }
 /**********************************
  *    
  */
    public void onHitRobot(HitRobotEvent event) {
        if (event.isMyFault()) {                    // I am pushing
            executeBehaviour(pushRobotBehaviour, event.getBearing());

            if (verbose > 1) {
                out.print("Pushing\t");
                out.println(event.getBearing());
            }
        }
        else {                                      // Enemy is pushing
            executeBehaviour(pushedByRobotBehaviour, event.getBearing());

            if (verbose > 1) {
                out.print("Pushed\t");
                out.println(event.getBearing());
            }
        }
    }

/*************************************
 *     
 */
    public void onHitWall(HitWallEvent event) {
        executeBehaviour(hitWallBehaviour, event.getBearing());

        if (verbose > 1) {
            out.print("Wall\t");
            out.println(event.getBearing());
        }
    }

/*************************************
 *     
 */
    public void onScannedRobot(ScannedRobotEvent event) {
        if (event.getDistance() < 100) {            // Enemy is at close distance
            executeBehaviour(scannedCloseDistRobotBehaviour, event.getBearing());

            if (verbose > 1) {
                out.print("Target near\t");
                out.println(event.getBearing());
            }
        }
        else if (event.getDistance() < 300) {       // Enemy is at mid distance
            executeBehaviour(scannedMidDistRobotBehaviour, event.getBearing());

            if (verbose > 1) {
                out.print("Target mid\t");
                out.println(event.getBearing());
            }
        }
        else {                                      // Enemy is at long distance
            executeBehaviour(scannedLongDistRobotBehaviour, event.getBearing());

            if (verbose > 1) {
                out.print("Target far\t");
                out.println(event.getBearing());
            }
        }
    }
 /*************************   
  * 
  * @param behaviour
  * @param bearing
  */
    private void executeBehaviour(int behaviour, double bearing) {
   
    	
            prepareMoveAction(behaviourActions[behaviour][0]);
            prepareTurnRobotAction(behaviourActions[behaviour][1], behaviourActions[behaviour][2], bearing);
            prepareTurnGunAction(behaviourActions[behaviour][3], behaviourActions[behaviour][4], bearing);
            fireAction(behaviourActions[behaviour][5]);
            execute();
     
    	
        if (behaviourSubsumption[behaviour]) {
            clearAllEvents();
        }
    }
 /*****************
  *    
  * @param d
  */
    private void prepareMoveAction(int d) {
        if (d < 0) {
            setBack(-d);
        }
        else if (d > 0) {
            setAhead(d);
        }
    }
  /***********************
   *   
   * @param action
   * @param a
   * @param bearing
   */
    private void prepareTurnRobotAction(int action, int a, double bearing) {
        double angle = 0;
        
        switch (action) {
            case 1:                         // Turn with respect to bearing
                setAdjustGunForRobotTurn(false);
                angle = a + bearing;
                break;
            case 2:                         // Turn with respect to bearing, gun is independent
                setAdjustGunForRobotTurn(true);
                angle = a + bearing;
                break;
            case 3:                         // Turn with respect to heading
                setAdjustGunForRobotTurn(false);
                angle = a;
                break;
            case 4:                         // Turn with respect to heading, gun is independent
                setAdjustGunForRobotTurn(true);
                angle = a;
                break;
            case 5:                         // Mirror turn
                setAdjustGunForRobotTurn(false);
                if ( (bearing > 90) || (bearing < -90) ) {
                    angle = 0;
                }
                else {
                    angle = 180-(2*bearing);
                }
                break;
            case 6:                         // Mirror turn, gun is independent
                setAdjustGunForRobotTurn(true);
                if ( (bearing > 90) || (bearing < -90) ) {
                    angle = putInRange(bearing - 180) / 2;
                }
                else {
                    angle = (2*bearing) - 180;
                }
                break;
        }
        
        angle = putInRange(angle);
        if (angle > 0) {
            setTurnRight(angle);
        }
        else if (angle < 0) {
            setTurnLeft(-angle);
        }
    }
/****************************
 *     
 * @param action
 * @param a
 * @param bearing
 */
    private void prepareTurnGunAction(int action, int a, double bearing) {
        double angle = 0;
        
        switch (action) {
            case 1:                         // Turn with respect to bearing
                angle = a + (bearing - (getGunHeading() - getHeading()));
                break;
            case 2:                         // Turn with respect to heading
                angle = a;
                break;
        }

        angle = putInRange(angle);
        if (angle > 0) {
            setTurnGunRight(angle);
        }
        else if (angle < 0) {
            setTurnGunLeft(-angle);
        }
    }
 /******************
  *    
  * @param action
  */
    private void fireAction(int action) {
        if (action > 0) {
            fire(action);
        }
    }
 /***********   
  * 
  * @param angle
  * @return
  */
    private double putInRange(double angle) {
        if (angle > 180) {
            return 360 - angle;
        }
        else if (angle < -180) {
            return 360 + angle;
        }
        else {
            return angle;
        }
    }
    
   /************************************************
    *  
    * @return
    */
    private boolean decodeGenome() {
        File file = getDataFile("genome");
        try {

        	
        	BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            Scanner scan;
            int i, j;
            out.println("Reading Chromosome...");
            if (!reader.ready()) {
                out.println("Error. Incomplete genome (event probabilites)");
                return false;
            }
            line = reader.readLine();           // Read event priorities
            scan = new Scanner(line).useDelimiter("\t");
            try {
                for (i = 0; i < numeroDeEventos; i++) {
                    eventPriority[i] = scan.nextInt();
                }
            }
            catch (NoSuchElementException nsee) {
                out.println("Error. The set of probabilities is not complete.");
                return false;
            }
            
            if (!reader.ready()) {
                out.println("Error. Incomplete genome (event subsumption)");
                return false;
            }
            line = reader.readLine();           // Read behaviour overwrite
            scan = new Scanner(line).useDelimiter("\t");
            try {
                behaviourSubsumption[0] = false;
                for (i = 1; i < numeroDeComportamientos; i++) {
                    behaviourSubsumption[i] = scan.nextInt() > 50;
                }
            }
            catch (NoSuchElementException nsee) {
                out.println("Error. The set of subsumptions is not complete.");
                return false;
            }
                           
            for (i = 0; i < (numeroDeComportamientos); i++) {
                if (!reader.ready()) {
                    out.println("Error. Incomplete genome (behaviours)");
                    return false;
                }
                line = reader.readLine();           // Read behaviour actions
                scan = new Scanner(line).useDelimiter("\t");

                try {
                    for (j = 0; j < numeroDeAcciones; j++) {
                        behaviourActions[i][j] = scan.nextInt();
                    }
                }
                catch (NoSuchElementException nsee) {
                    out.println("Error. The set of actions in behaviour " + String.valueOf(i) + " is not complete.");
                    return false;
                }
            }
        }
        catch(java.io.IOException ioe) {
            out.println("Error. Impossible to read file");
            return false;
        }
        
        return true;
    }

   /**
    */
    private void leerCfg(){
	    BufferedReader reader = null;
	    try {
			reader = new BufferedReader(new FileReader(getDataFile("cfg.dat")));
		} catch (FileNotFoundException e) {
			  numeroDeComportamientos 	= 11;
			  numeroDeAcciones 			= 6;			
			  numeroDeEventos 			= 7; 
		}
	    
		if (reader!=null) {
			try {
				numeroDeEventos 			=  Integer.parseInt(reader.readLine());
				numeroDeComportamientos 	= Integer.parseInt(reader.readLine());
				numeroDeAcciones		 	=  Integer.parseInt(reader.readLine());
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
    }
}
