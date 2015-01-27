package de.teamlapen.vampirism.util;

/**
 * All constants which could be used to balance the mod should be stored here
 *
 */
public final class BALANCE {
	//Mob behavior
	@DefaultInt(value=3,comment="Vampirehunters attack players with a vampire level higher than this")
	public static int VAMPIRE_HUNTER_ATTACK_LEVEL;
	@DefaultInt(value=3,comment="Vampires attack players unless their vampirelevel is higher than this")
	public static int VAMPIRE_FRIENDLY_LEVEL;
	@DefaultInt(10)
	public static int VAMPIRE_HUNTER_SPAWN_PROBE;
	@DefaultInt(20)
	public static int VAMPIRE_SPAWN_PROBE;
	
	//Vampireplayer
	@DefaultBoolean(value=true,comment="Whether vampireplayers should loose level if killed by a hunter")
	public static boolean VAMPIRE_PLAYER_LOOSE_LEVEL;
	public static float getVampireSunDamage(int level){
		if(level<5){
			return 0;
		}
		return 1.5F;
	}
	/**
	 * Calculates the players night vision effect
	 * @param level
	 * @return value between 0 and 1
	 */
	public static float getVampirePlayerNightVision(int level){
		float nv= (level==0.0F ? 0 : 1.0F-(3/level));
		return (nv<0.0F?0.0F:nv);
	}
	
	/**
	 * Class to store all constants related to the player modifiers
	 * See {@link: de.teamlapen.vampirism.entity.player.PlayerModifiers#applyModifiers(int, net.minecraft.entity.player.EntityPlayer)} for impact
	 *
	 */
	public static class VP_MODIFIERS{
		@DefaultDouble(1.0D)
		public static double HEALTH_MAX_MOD;
		@DefaultInt(20)
		public static int HEALTH_LCAP=20;
		@DefaultDouble(0.5D)
		public static double HEALTH_TYPE;
		@DefaultDouble(1.0D)
		public static double STRENGTH_MAX_MOD;
		@DefaultInt(20)
		public static int STRENGTH_LCAP;
		@DefaultDouble(0.5D)
		public static double STRENGTH_TYPE;
		@DefaultDouble(0.3D)
		public static double SPEED_MAX_MOD;
		@DefaultInt(15)
		public static int SPEED_LCAP;
		@DefaultDouble(0.5D)
		public static double SPEED_TYPE;
	}
	
	public static class LEVELING{
		@DefaultInt(1)
		public static int ALTAR_2_MIN_LEVEL;
		@DefaultInt(3)
		public static int ALTAR_2_MAX_LEVEL;
		public static int A2_getRequiredBlood(int level){
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
	@DefaultInt(5)
	public static int R1_VILLAGERS;//Required villagers for ritual 1
	//Vampiremob
	@DefaultInt(5)
	public static int SMALL_BLOOD_AMOUNT; //Blood amount a small mob gives
	@DefaultInt(10)
	public static int NORMAL_BLOOD_AMOUNT; // "" normal mob
	@DefaultInt(15)
	public static int BIG_BLOOD_AMOUNT; // "" big mob
	@DefaultDouble(0.3d)
	public static double SUCK_BLOOD_HEALTH_REQUIREMENT; //Percentage of his max health a mob can maximal have to be bitten
	
	//Other
	@DefaultInt(20)
	public static int NEEDED_BLOOD; //Blood amount needed for blood altar ritual
	@DefaultDouble(1.0D)
	public static double BLOOD_SATURATION;//Saturation  of blood
}
