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
    public static final String FORGE_VERSION_MIN = "14.23.0.2491";
    public static final int HIGHEST_VAMPIRE_LEVEL = 14;
    public static final int HIGHEST_HUNTER_LEVEL = 14;
    public static final String FORGE_VERSION = "@FVERSION@";
    public static final String VERSION_UPDATE_FILE = "http://maxanier.de/projects/vampirism/versions.json";
    public static final String SUPPORTER_FILE = "http://maxanier.de/projects/vampirism/supporters.json";//TODO maybe switch back to HTTPS when most java version trust let's encrypt
    public static final String CURSEFORGE_LINK = "https://minecraft.curseforge.com/projects/vampirism-become-a-vampire";
    public static final String PATREON_LINK = "https://www.patreon.com/maxanier";

    public static final int EYE_TYPE_COUNT = 14;
    public static final int FANG_TYPE_COUNT = 6;
    /**
     * Check for vampire garlic damage every n ticks
     */
    public final static int REFRESH_GARLIC_TICKS = 40;
    /**
     * Check for vampire sundamage every n ticks
     */
    public final static int REFRESH_SUNDAMAGE_TICKS = 8;

    public final static ResourceLocation FACTION_PLAYER_HANDLER_KEY = new ResourceLocation(MODID, "ifactionplayerhandler");
    public final static ResourceLocation VAMPIRE_PLAYER_KEY = new ResourceLocation(MODID, "ivampireplayer");
    public final static ResourceLocation HUNTER_PLAYER_KEY = new ResourceLocation(MODID, "ihunterplayer");
    public final static ResourceLocation EXTENDED_CREATURE_KEY = new ResourceLocation(MODID, "iextendedcreature");
    public final static ResourceLocation VAMPIRISM_VILLAGE_KEY = new ResourceLocation(MODID, "ivampirismvillage");
}
