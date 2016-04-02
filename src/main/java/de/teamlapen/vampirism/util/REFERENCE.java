package de.teamlapen.vampirism.util;

import net.minecraft.util.ResourceLocation;

/**
 * Class to store constants and stuff
 */
public class REFERENCE {
    public static final String MODID = "vampirism";
    public static final String NAME = "Vampirism";
    public static final String VERSION = "@VERSION@";
    public static final String MINECRAFT_VERSION = "@MVERSION@";
    public static final String FORGE_VERSION_MIN = "11.15.1.1722";
    public static final int HIGHEST_VAMPIRE_LEVEL = 14;
    public static final int HIGHEST_HUNTER_LEVEL = 15;
    public static final String FORGE_VERSION = "@FVERSION@";
    public static final String VERSION_UPDATE_FILE = "https://raw.githubusercontent.com/TeamLapen/Vampirism/master/src/main/resources/versions.info";

    public static final int EYE_TYPE_COUNT = 6;
    /**
     * Check for vampire garlic damage every n ticks
     */
    public final static int REFRESH_GARLIC_TICKS = 40;
    /**
     * Check for vampire sundamage every n ticks
     */
    public final static int REFRESH_SUNDAMAGE_TICKS = 8;

    public final static ResourceLocation FACTION_PLAYER_HANDLER_KEY = new ResourceLocation(MODID, "IFactionPlayerHandler");
    public final static ResourceLocation VAMPIRE_PLAYER_KEY = new ResourceLocation(MODID, "IVampirePlayer");
    public final static ResourceLocation HUNTER_PLAYER_KEY = new ResourceLocation(MODID, "IHunterPlayer");
    public final static ResourceLocation EXTENDED_CREATURE_KEY = new ResourceLocation(MODID, "IExtendedCreature");
}
