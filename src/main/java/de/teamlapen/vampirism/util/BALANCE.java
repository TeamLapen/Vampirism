package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;

/**
 * All constants which could be used to balance the mod should be stored here
 *
 */
public final class BALANCE {
	public static class LEVELING {
		public static int A2_getRequiredBlood(int level) {
			return ALTAR_2_MIN_BLOOD + (level - TileEntityBloodAltar2.MIN_LEVEL) * ALTAR_2_ADD_BLOOD_PER_LEVEL;
		}
		@DefaultInt(value=40,minValue=20,maxValue=800,name="Minimum Blood for Altar 2",
				comment="Blood required for the first level up with altar 2")
		public static int ALTAR_2_MIN_BLOOD;
		@DefaultInt(value=30,minValue=10,maxValue=600,name="Extra Blood per Level",
				comment="Extra blood per level required")
		public static int ALTAR_2_ADD_BLOOD_PER_LEVEL;
	}
	public static class MOBPROP {
		@DefaultDouble(value=20.0D,minValue=10.0D,maxValue=40.0D,name="Vampire Max Health",comment = "")
		public static double VAMPIRE_MAX_HEALTH;
		@DefaultDouble(value=5.0D,minValue=2.5D,maxValue=10.0D,name="Vampire Attack Damage",comment = "")
		public static double VAMPIRE_ATTACK_DAMAGE;
		@DefaultDouble(value=0.3D,minValue=0.1D,maxValue=0.6D,name="Vampire Movement Speed",comment = "")
		public static double VAMPIRE_MOVEMENT_SPEED;
		@DefaultInt(value=10,minValue=5,maxValue=20,name = "Distance where Hunter Attacks Vampire",comment = "")
		public static int VAMPIRE_DISTANCE_HUNTER;

		@DefaultDouble(value = 90.0D,minValue=20.0D,maxValue=150.0D,name="Vampire Lord Max Health",comment = "")
		public static double VAMPIRE_LORD_MAX_HEALTH;
		@DefaultDouble(value=7.0D,minValue=1.0D,maxValue=14.0D,name="Vampire Lord Attack Damage",comment = "")
		public static double VAMPIRE_LORD_ATTACK_DAMAGE;
		@DefaultDouble(value=0.3D,minValue=0.1D,maxValue=0.6D,name="Vampire Lord Movement Speed",comment = "")
		public static double VAMPIRE_LORD_MOVEMENT_SPEED;
		@DefaultDouble(value=1.3D,minValue=1.0D,maxValue=2.0D,name="Vampire Lord Improvement per Level",
				comment="For each higher level the stats are multiplied with this factor")
		public static double VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL;

		@DefaultDouble(value=35.0D,minValue=15.0D,maxValue=70.0D,name="Vampire Hunter Max Health",comment = "")
		public static double VAMPIRE_HUNTER_MAX_HEALTH;
		@DefaultDouble(value=2.5D,minValue=1.0D,maxValue=5.0D,name="Vampire Hunter Attack Damage",comment = "")
		public static double VAMPIRE_HUNTER_ATTACK_DAMAGE;
		@DefaultDouble(value=0.28D,minValue=0.1D,maxValue=0.6D,name="Vampire Hunter Movement Speed",comment = "")
		public static double VAMPIRE_HUNTER_MOVEMENT_SPEED;
		@DefaultInt(value=5,minValue=0,maxValue=20,name="Maximum Vampire Hunters per Village",comment="")
		public static int VAMPIRE_HUNTER_MAX_PER_VILLAGE;
		@DefaultDouble(value=1.4D,minValue=1.0D,maxValue=3.0D,name="Vampire Hunter Agressive Multiplier",comment = "")
		public static double VAMPIRE_HUNTER_AGRESSIVE_MULT;
		
		@DefaultDouble(value=10.0D,minValue=5.0D,maxValue=20.0D,name="Vampire Minion Max Health",comment = "")
		public static double VAMPIRE_MINION_MAX_HEALTH;
		@DefaultDouble(value=3.0D,minValue=1.5D,maxValue=6.0D,name="Vampire Minion Attack Damage",comment = "")
		public static double VAMPIRE_MINION_ATTACK_DAMAGE;
		@DefaultDouble(value=0.25D,minValue=0.1D,maxValue=0.5D,name="Vampire Minion Movement Speed",comment = "")
		public static double VAMPIRE_MINION_MOVEMENT_SPEED;
		
		@DefaultDouble(value=100.0D,minValue=50.0D,maxValue=150.0D,name="Dracula Max Health",comment = "")
		public static double DRACULA_MAX_HEALTH;
		@DefaultDouble(value=10.0D,minValue=5.0D,maxValue=20.0D,name="Dracula Attack Damage",comment = "")
		public static double DRACULA_ATTACK_DAMAGE;
		@DefaultDouble(value=0.4D,minValue=0.2D,maxValue=0.8D,name="Dracula Movement Speed",comment = "")
		public static double DRACULA_MOVEMENT_SPEED;
	}
	/**
	 * Class to store all constants related to the player modifiers See {@link:
	 * de.teamlapen.vampirism.entity.player.PlayerModifiers#applyModifiers(int,
	 * net.minecraft.entity.player.EntityPlayer)} for impact
	 *
	 */
	public static class VP_MODIFIERS {
		@DefaultDouble(value=1.0D,minValue=0.5D,maxValue=2.0D,name="Health Max Modifier",comment = "")
		public static double HEALTH_MAX_MOD;
		@DefaultInt(value=20,minValue=10,maxValue=40,name = "Health Level Cap",comment = "")
		public static int HEALTH_LCAP;
		@DefaultDouble(value=0.5D,minValue=0.5D,maxValue=1.0D,name="Health Type",comment="0.5 for square root, 1 for linear")
		public static double HEALTH_TYPE;
		@DefaultDouble(value=1.0D,minValue=0.5D,maxValue=2.0D,name="Strength Max Modifier",comment = "")
		public static double STRENGTH_MAX_MOD;
		@DefaultInt(value=20,minValue=10,maxValue=40,name = "Strength Level Cap",comment = "")
		public static int STRENGTH_LCAP;
		@DefaultDouble(value=0.5D,minValue=0.5D,maxValue=1.0D,name="Strength Modifier Type",comment="0.5 for square root, 1 for linear")
		public static double STRENGTH_TYPE;
		@DefaultDouble(value=0.3D,minValue=0.15D,maxValue=5D,name="Speed Max Modifier",comment = "")
		public static double SPEED_MAX_MOD;
		@DefaultInt(value=15,minValue=7,maxValue=100,name = "Speed Level Cap",comment = "")
		public static int SPEED_LCAP;
		@DefaultDouble(value=0.5D,minValue=0.1D,maxValue=1.0D,name="Speed Type",comment = "")
		public static double SPEED_TYPE;
		@DefaultDouble(value=0.2D,minValue=0.1D,maxValue=0.4D,name="Jump Max Boost",comment = "")
		public static double JUMP_MAX_BOOST;
		@DefaultInt(value=6,minValue=3,maxValue=100,name = "Jump Level Cap",comment = "")
		public static int JUMP_LCAP;
		@DefaultDouble(value=0.5D,minValue=0.1D,maxValue=1.0D,name="Jump Type",comment = "")
		public static double JUMP_TYPE;
	}
	
	public static class VV_PROP{
		@DefaultInt(value=4,minValue=1,name="Hunter's Tolerance for Biting Villagers",
				comment="How many villagers can be bitten until the hunters get agressive")
		public static int BITTEN_UNTIL_AGRESSIVE;
		@DefaultInt(value=4,minValue=1,name="Hunter's Tolerance for Killing Villagers",
				comment="How many villagers have to be killed by Vampires until the hunters get agressive")
		public static int CONVERTED_UNTIL_AGRESSIVE;
		@DefaultInt(value=50,minValue=1,name="Villagers Forgiveness Rate",
				comment="Determines how fast the villages forget about their fallen citizens.")
		public static int REDUCE_RATE;
	}
	
	/**
	 * Duration, cooldown etc for vampire player skills.
	 * Time values should be in seconds
	 */
	public static class VP_SKILLS{
		@DefaultInt(value=60,minValue=0,name="Regeneration Cool Down",comment = "In seconds")
		public static int REGEN_COOLDOWN;
		@DefaultInt(value=20,minValue=0,name="Regeneration Duration",comment = "In seconds")
		public static int REGEN_DURATION;
		@DefaultInt(value=4,minValue=-1,name="Regeneration Min Level",comment = "Set to -1 to deactivate this skill")
		public static int REGEN_MIN_LEVEL;
		
		@DefaultInt(value=60,minValue=0,name="Weather Cool Down",comment = "In seconds")
		public static int WEATHER_COOLDOWN;
		@DefaultInt(value=4,minValue=-1,name="Weather Min Level",comment = "Set to -1 to deactivate this skill")
		public static int WEATHER_MIN_LEVEL;
		
		@DefaultInt(value=60,minValue=0,name="Revive Fallen Cool Down",comment = "In seconds")
		public static int REVIVE_FALLEN_COOLDOWN;
		@DefaultInt(value=7,minValue=-1,name="Revive Fallen Min Level",comment = "Set to -1 to deactivate this skill")
		public static int REVIVE_FALLEN_MIN_LEVEL;
		
		@DefaultInt(value=20,minValue=0,name="Vampire Rage Cool Down",
				comment="Vampire Rage cooldown duration")
		public static int RAGE_COOLDOWN;
		@DefaultInt(value=10,minValue=1,name="Vampire Rage Duration",
				comment="Standard Vampire Rage duration")
		public static int RAGE_MIN_DURATION;
		@DefaultInt(value=5,minValue=0,name="Vampire Rage Duration Increase",
				comment="Vampire Rage duration increase per level")
		public static int RAGE_DUR_PL;
		@DefaultInt(value=8,minValue=-1,name="Vampire Rage Min Level",
				comment="Set to -1 to deactivate this skill")
		public static int RAGE_MIN_LEVEL;
		
		@DefaultInt(value=10, name = "Invisibility (Vampire Lord) Duration")
		public static int INVISIBILITY_DURATION;
		@DefaultInt(value=45, name = "Invisibility Cooldown")
		public static int INVISIBILITY_COOLDOWN;
		
		@DefaultInt(value=2,minValue=1,name="Bat Speed Modifier")
		public static int BAT_SPEED_MOD;
		
		@DefaultInt(value=3,minValue=-1,name="Bat Transformation Min Level")
		public static int BAT_MIN_LEVEL;
		
		@DefaultInt(value=5,minValue=-1,name="Summon blinding bats Min Level")
		public static int SUMMON_BAT_MIN_LEVEL;
		
		@DefaultInt(value=300,minValue=1,name="Summon blinding bats cooldown")
		public static int SUMMON_BAT_COOLDOWN;
		
		@DefaultInt(value=50,minValue=1,name="Lord teleport max distance")
		public static int TELEPORT_MAX_DISTANCE;
		
		@DefaultInt(value=30,minValue=1,name="Lord teleport cooldown")
		public static int TELEPORT_COOLDOWN;
		
		public static int getVampireLordDuration(int level){
			if(level<RAGE_MIN_LEVEL){
				return 0;
			}
			return 20 *(RAGE_MIN_DURATION+(level-RAGE_MIN_LEVEL)*RAGE_DUR_PL);
		}
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
	
	// Mob behavior
	@DefaultInt(value=2,minValue=1,name="Player Level for Vampire Hunter Attack",
			comment="Vampirehunters attack players with a vampire level higher than this")
	public static int VAMPIRE_HUNTER_ATTACK_LEVEL;

	@DefaultInt(value=2,minValue=1,name="Player Level for Vampire Attack",
			comment="Vampires attack players unless their vampire level is higher than this")
	public static int VAMPIRE_FRIENDLY_LEVEL;

	@DefaultInt(value=15,minValue=0,name="Vampire Hunter Spawn",comment = "Should be compared with the spawnrate of friendly mobs")
	public static int VAMPIRE_HUNTER_SPAWN_PROBE;

	@DefaultInt(value=100,minValue=0,name="Vampire Spawn Probe",comment = "")
	public static int VAMPIRE_SPAWN_PROBE;
	
	@DefaultInt(value=30,minValue=0,name="Vampire Lord Spawn Probe",comment = "")
	public static int VAMPIRE_LORD_SPAWN_PROBE;

	// Vampireplayer
	@DefaultBoolean(value=true,name="Vampire Player Loose Level",comment="Whether vampireplayers should loose level if killed by a hunter")
	public static boolean VAMPIRE_PLAYER_LOOSE_LEVEL;

	@DefaultDouble(value=4.0D,minValue=0D,name="Vampire Player Sun Damage",comment = "")
	public static double VAMPIRE_PLAYER_SUN_DAMAGE;
	
	@DefaultInt(value=2,minValue=1,name="Vampire Player Creeper Avoidance Level",comment = "")
	public static int VAMPIRE_PLAYER_CREEPER_AVOID_LEVEL;
	
	@DefaultInt(value=10,comment="Probability that a vampire gives you sanguinare (1/n)",name="Sanguinare Probability",minValue=1)
	public static int VAMPIRE_PLAYER_SANGUINARE_PROB;
	
	@DefaultInt(value=1200,comment="Duration of the sanguinare vampiris effect in seconds",name="Sanguinare Duration",minValue=1)
	public static int VAMPIRE_PLAYER_SANGUINARE_DURATION;
	
	// Vampiremob
	@DefaultInt(value=5,minValue=2,maxValue=30,name="Small Mob Blood Amount",comment = "The amount of blood you get when you bite a 'small' mob")
	public static int SMALL_BLOOD_AMOUNT;
	@DefaultInt(value=10,minValue=2,maxValue=30,name="Medium Mob Blood Amount",comment = "")
	public static int NORMAL_BLOOD_AMOUNT;
	@DefaultInt(value=15,minValue=2,maxValue=30,name="Big Mob Blood Amount",comment = "")
	public static int BIG_BLOOD_AMOUNT;
	@DefaultDouble(value=1.0D,minValue=0.01D,maxValue=1.0D,name="Mob Health to Suck Blood",
			comment="Percentage of his max health a mob can maximal have to be bitten (Disabled by default)")
	public static double SUCK_BLOOD_HEALTH_REQUIREMENT;

	// Other
	@DefaultDouble(value=1.0D,minValue=0.1D,maxValue=4.0D,name="Blood Saturation",comment = "Influences the blood usage.")
	public static double BLOOD_SATURATION;// Saturation of blood
	@DefaultInt(value=4,minValue=1,maxValue=100,name="Blood Exhaustion per Level",
			comment = "Amount of exhaustion, which has to be reached to loose one blood")
	public static int BLOOD_EXH_PER_BL;
	@DefaultInt(value=3,minValue=-1,name="Create dead mob probability",comment="1/n Propability to create a dead mob entity. -1 to disable, 0 to always")
	public static int DEAD_MOB_PROP;
}
