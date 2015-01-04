package de.teamlapen.vampirism.util;

/**
 * All constants which could be used to balance the mod should be stored here
 * @author Max
 *
 */
public class BALANCE {
	//Mob behavior
	public final static int VAMPIRE_HUNTER_ATTACK_LEVEL=3;//Vampirehunters attack players with a vampire level higher than this
	public final static int VAMPIRE_FRIENDLY_LEVEL=3;//Vampires attack players unless their vampirelevel is higher than this
	
	//Vampireplayer


	
	//Vampiremob
	public static final int SMALL_BLOOD_AMOUNT = 5; //Blood amount a small mob gives
	public static final int NORMAL_BLOOD_AMOUNT = 10; // "" normal mob
	public static final int BIG_BLOOD_AMOUNT = 15; // "" big mob
	
	public static final float SUCK_BLOOD_HEALTH_REQUIREMENT = 0.3f; //Percentage of his max health a mob can maximal have to be bitten
	
	//Other
	public static final int NEEDED_BLOOD = 20; //Blood amount needed for blood altar ritual
}
