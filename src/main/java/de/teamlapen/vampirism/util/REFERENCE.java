package de.teamlapen.vampirism.util;

public class REFERENCE {
	public static final class ENTITY {
		public static final String VAMPIRE_HUNTER_NAME = "vampirism.vampireHunter";
		public static final String VAMPIRE_NAME = "vampirism.vampire";
		public static final String DRACULA_NAME = "vampirism.dracula";
		public static final String GHOST_NAME = "vampirism.ghost";
		public static final String VAMPIRE_LORD_NAME = "vampirism.vampireLord";
		public static final String VAMPIRE_MINION_REMOTE_NAME = "vampirism.vampireMinionR";
		public static final String VAMPIRE_MINION_SAVEABLE_NAME = "vampirism.vampireMinionS";
		public static final String DEAD_MOB_NAME = "vampirism.dead_mob";
		public static final String BLINDING_BAT_NAME = "vampirism.blinding_bat";
	}

	public static enum KEY {
		UNKNOWN, SUCK, AUTO, SKILL, VISION, MINION_CONTROL
	}

	public static final class KEYS {
		public static final String CATEGORY = "keys.vampirism.category";
		public static final String SUCK_BLOOD = "keys.vampirism.suck";
		public static final String AUTO_BLOOD = "keys.vampirism.auto";
		public static final String TOGGLE_SKILLS = "keys.vampirism.skill";
		public static final String SWITCH_VISION = "key.vampirism.vision";
		public static final String MINION_CONTROL = "key.vampirism.minion_control";
	}

	public static final String MODID = "vampirism";

	public static final String NAME = "Vampirism";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSION = "@MVERSION@";
	public static final String VAMPIRE_LEVEL_NBT_KEY = "vampirelevel";

	public static final int HIGHEST_REACHABLE_LEVEL = 13;

	public static final String UPDATE_FILE_LINK = "http://teamlapen.de/projects/vampirism/files/modversion.json";

}
