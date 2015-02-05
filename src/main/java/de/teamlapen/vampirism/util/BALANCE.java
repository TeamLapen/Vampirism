package de.teamlapen.vampirism.util;

/**
 * All constants which could be used to balance the mod should be stored here
 *
 */
public final class BALANCE {
	public static class LEVELING {
		public static int A2_getRequiredBlood(int level) {
			return ALTAR_2_MIN_BLOOD + (level - ALTAR_2_MIN_LEVEL) * ALTAR_2_ADD_BLOOD_PER_LEVEL;
		}
		@DefaultInt(1)
		public static int ALTAR_2_MIN_LEVEL;
		@DefaultInt(3)
		public static int ALTAR_2_MAX_LEVEL;
		@DefaultInt(0)
		public static int R1_VILLAGERS;// Required villagers for ritual 1
		@DefaultInt(value = 50, comment = "Blood required for the first level up with altar 2")
		public static int ALTAR_2_MIN_BLOOD;
		@DefaultInt(value = 20, comment = "Extra blood per level required")
		public static int ALTAR_2_ADD_BLOOD_PER_LEVEL;

		@DefaultInt(value = 20, comment = "Blood amount needed for inital ritual")
		public static int ALTAR_1_BLOOD;
	}
	public static class MOBPROP {
		@DefaultDouble(20.0D)
		public static double VAMPIRE_MAX_HEALTH;
		@DefaultDouble(5.0D)
		public static double VAMPIRE_ATTACK_DAMAGE;
		@DefaultDouble(0.3D)
		public static double VAMPIRE_MOVEMENT_SPEED;
		@DefaultInt(10)
		public static int VAMPIRE_DISTANCE_HUNTER;

		@DefaultDouble(25.0D)
		public static double VAMPIRE_HUNTER_MAX_HEALTH;
		@DefaultDouble(5.0D)
		public static double VAMPIRE_HUNTER_ATTACK_DAMAGE;
		@DefaultDouble(0.4F)
		public static double VAMPIRE_HUNTER_MOVEMENT_SPEED;
		@DefaultInt(5)
		public static int VAMPIRE_HUNTER_MAX_PER_VILLAGE;
	}
	/**
	 * Class to store all constants related to the player modifiers See {@link:
	 * de.teamlapen.vampirism.entity.player.PlayerModifiers#applyModifiers(int,
	 * net.minecraft.entity.player.EntityPlayer)} for impact
	 *
	 */
	public static class VP_MODIFIERS {
		@DefaultDouble(1.0D)
		public static double HEALTH_MAX_MOD;
		@DefaultInt(20)
		public static int HEALTH_LCAP = 20;
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
	/**
	 * Calculates the players night vision effect
	 * 
	 * @param level
	 * @return value between 0 and 1
	 */
	public static float getVampirePlayerNightVision(int level) {
		float nv = (level == 0.0F ? 0 : 1.0F - (2 / level));
		return (nv < 0.0F ? 0.0F : nv);
	}

	public static float getVampireSunDamage(int level) {
		if (level < 5) {
			return 0;
		}
		return (float) VAMPIRE_PLAYER_SUN_DAMAGE;
	}
	// Mob behavior
	@DefaultInt(value = 3, comment = "Vampirehunters attack players with a vampire level higher than this")
	public static int VAMPIRE_HUNTER_ATTACK_LEVEL;

	@DefaultInt(value = 3, comment = "Vampires attack players unless their vampirelevel is higher than this")
	public static int VAMPIRE_FRIENDLY_LEVEL;

	@DefaultInt(20)
	public static int VAMPIRE_HUNTER_SPAWN_PROBE;

	@DefaultInt(30)
	public static int VAMPIRE_SPAWN_PROBE;

	// Vampireplayer
	@DefaultBoolean(value = true, comment = "Whether vampireplayers should loose level if killed by a hunter")
	public static boolean VAMPIRE_PLAYER_LOOSE_LEVEL;

	@DefaultDouble(1.5D)
	public static double VAMPIRE_PLAYER_SUN_DAMAGE;

	// Vampiremob
	@DefaultInt(5)
	public static int SMALL_BLOOD_AMOUNT; // Blood amount a small mob gives
	@DefaultInt(10)
	public static int NORMAL_BLOOD_AMOUNT; // "" normal mob
	@DefaultInt(15)
	public static int BIG_BLOOD_AMOUNT; // "" big mob
	@DefaultDouble(0.3d)
	public static double SUCK_BLOOD_HEALTH_REQUIREMENT; // Percentage of his max
														// health a mob can
														// maximal have to be
														// bitten

	// Other
	@DefaultDouble(1.0D)
	public static double BLOOD_SATURATION;// Saturation of blood
	@DefaultInt(value = 4, comment = "Amount of exhaustion, which has to be reached to loose one blood")
	public static int BLOOD_EXH_PER_BL;
}
