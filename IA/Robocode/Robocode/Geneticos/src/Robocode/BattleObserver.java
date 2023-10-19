/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Robocode;

import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

/**
 *
 * @author hsatizab
 */
public class BattleObserver extends BattleAdaptor {
	
	  private double scoreRobot;
	  private double scoreEnemy;
	     
     // Called when the battle is completed successfully with battle results
     public void onBattleCompleted(BattleCompletedEvent e) {
		if (e.getIndexedResults().length > 1) {
			scoreRobot = e.getIndexedResults()[0].getScore();  //Segun la configuracion de batalla cero es el evovable
			scoreEnemy = e.getIndexedResults()[1].getScore();
			
			e.getIndexedResults()[0].getBulletDamage();
		
		//  Es posible obtener más información de la batalla que sea útil para el fitness ToDo
		//	e.getSortedResults()[0].getBulletDamage();
		//	e.getSortedResults()[0].getBulletDamageBonus();
		}
		else {
			System.out.println("Error. Robocode did not send results.");
			scoreRobot = 0;
			scoreEnemy = 0;
		}
     }
 
     // Called when the game sends out an information message during the battle
     public void onBattleMessage(BattleMessageEvent e) {
     }
 
     // Called when the game sends out an error message during the battle
     public void onBattleError(BattleErrorEvent e) {
     }
     
     public double getScoreRobot() {
         return scoreRobot;
     }
     
     public double getScoreEnemy() {
         return scoreEnemy;
     }
     
   
}
