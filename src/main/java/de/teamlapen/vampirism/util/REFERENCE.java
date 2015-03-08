package de.teamlapen.vampirism.util;

public class REFERENCE {
	public static final class ENTITY {
		public static final String VAMPIRE_HUNTER_NAME = "vampirism:vampireHunter";
		public static final String VAMPIRE_NAME = "vampirism:vampire";
		public static final String DRACULA_NAME = "vampirism:dracula";
		public static final String GHOST_NAME = "vampirism:ghost";
	}

	public static enum KEY {
		UNKNOWN, SUCK, AUTO
	}

	public static final class KEYS {
		public static final String CATEGORY = "keys.vampirism.category";
		public static final String SUCK_BLOOD = "keys.vampirism.suck";
		public static final String AUTO_BLOOD = "keys.vampirism.auto";
	}

	public static final String MODID = "vampirism";

	public static final String NAME = "Vampirism";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSION = "@MVERSION@";
	public static final boolean RESET_CONFIG_IN_DEV=true;

	public static final String VAMPIRE_LEVEL_NBT_KEY = "vampirelevel";

}
