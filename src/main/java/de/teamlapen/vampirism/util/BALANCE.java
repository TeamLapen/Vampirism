package de.teamlapen.vampirism.util;

/**
 * All constants which could be used to balance the mod should be stored here
 *
 */
public final class BALANCE {
	//Mob behavior
	public final static int VAMPIRE_HUNTER_ATTACK_LEVEL=3;//Vampirehunters attack players with a vampire level higher than this
	public final static int VAMPIRE_FRIENDLY_LEVEL=3;//Vampires attack players unless their vampirelevel is higher than this
	public final static int VAMPIRE_HUNTER_SPAWN_PROBE=10;//Spawn probe for random spawns. n/unknown
	public final static int VAMPIRE_SPAWN_PROBE=20;//Spawn probe for random spawns. n/unknown
	
	//Vampireplayer
	public static final boolean VAMPIRE_PLAYER_LOOSE_LEVEL=true;//Whether vampireplayers should loose level if killed by a hunter
	/**
	 * Calculates the players night vision effect
	 * @param level
	 * @return value between 0 and 1
	 */
	public static final float getVampirePlayerNightVision(float level){
		float nv= (level==0.0F ? 0 : 1.0F-(3/level));
		return (nv<0.0F?0.0F:nv);
	}
	
	/**
	 * Class to store all constants related to the player modifiers
	 * See {@link: de.teamlapen.vampirism.entity.player.PlayerModifiers#applyModifiers(int, net.minecraft.entity.player.EntityPlayer)} for impact
	 *
	 */
	public static final class VP_MODIFIERS{
		public static final double HEALTH_MAX_MOD=1;
		public static final int HEALTH_LCAP=20;
		public static final double HEALTH_TYPE=1/2;
		public static final double STRENGTH_MAX_MOD=1;
		public static final int STRENGTH_LCAP=20;
		public static final double STRENGTH_TYPE=1/2;
		public static final double SPEED_MAX_MOD=0.3D;
		public static final int SPEED_LCAP=15;
		public static final double SPEED_TYPE=1/2;
	}
	
	public static final class LEVELING{
		public static final int ALTAR_2_MIN_LEVEL=1;
		public static final int ALTAR_2_MAX_LEVEL=3;
		public static final int A2_getRequiredBlood(int level){
			switch(level){
			case 1:
				return 50;
			case 2:
				return 70;
			default:
				return 90;
			}
		}
	}

	//RITUALS
	
	public static final int R1_VILLAGERS=5;//Required villagers for ritual 1
	//Vampiremob
	public static final int SMALL_BLOOD_AMOUNT = 5; //Blood amount a small mob gives
	public static final int NORMAL_BLOOD_AMOUNT = 10; // "" normal mob
	public static final int BIG_BLOOD_AMOUNT = 15; // "" big mob
	
	public static final float SUCK_BLOOD_HEALTH_REQUIREMENT = 0.3f; //Percentage of his max health a mob can maximal have to be bitten
	
	//Other
	public static final int NEEDED_BLOOD = 20; //Blood amount needed for blood altar ritual
	public static final float BLOOD_SATURATION=1.0F;//Saturation  of blood
}
