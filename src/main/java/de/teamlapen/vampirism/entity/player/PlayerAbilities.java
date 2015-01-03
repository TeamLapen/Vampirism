package de.teamlapen.vampirism.entity.player;


/**
 * Class to determine which special abilities a VampirePlayer should have and store them
 * @author Max
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
		float nv= (level==0.0F ? 0 : 1.0F-(3/level));
		nv=(nv<0.0F?0.0F:nv);
		return new PlayerAbilities(nv);
	}
}
