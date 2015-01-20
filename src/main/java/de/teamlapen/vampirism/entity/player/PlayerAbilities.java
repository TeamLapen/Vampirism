package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.util.BALANCE;


/**
 * Class to determine which special abilities a VampirePlayer should have and store them
 * @author Maxanier
 *
 */
public class PlayerAbilities {
	public final float nightVision; //Used in the coremod.CoreHandler to tell the Renderer if it should enable that effect
	
	private PlayerAbilities(float nv){
		nightVision=nv;
	}
	
	/**
	 * Determines which special abilities the player should have
	 * @param level Vampire level
	 * @return 
	 */
	public static PlayerAbilities getPlayerAbilities(float level){
		return new PlayerAbilities(BALANCE.getVampirePlayerNightVision(level));
	}
}
