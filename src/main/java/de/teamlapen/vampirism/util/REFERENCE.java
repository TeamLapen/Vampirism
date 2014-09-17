package de.teamlapen.vampirism.util;

public class REFERENCE {
	public static final class ENTITY {
		public static final String VAMPIRE_HUNTER_NAME = "vampirism:vampireHunter";
		public static final String VAMPIRE_NAME = "vampirism:vampire";
	}
	public static enum KEY {
		UNKNOWN, SUCK
	}
	public static final class KEYS {
		public static final String CATEGORY = "keys.vampirism.category";
		public static final String SUCK_BLOOD = "keys.vampirism.suck";
	}
	public static final String MODID = "vampirism";

	public static final String NAME = "Vampirism";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSION = "@MVERSION@";
	public static final int neededBlood = 20;
	public static final float suckBloodHealthRequirement = 0.3f;

	public static final int smallBloodAmount = 5;
	public static final int normalBloodAmount = 10;

	public static final int bigBloodAmount = 15;

	public static final String VAMPIRE_LEVEL_NBT_KEY = "vampirelevel";

	public static final String TE_BLOODALTAR_NBT_KEY = "tebloodaltar";

}
