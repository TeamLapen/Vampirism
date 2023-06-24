package de.teamlapen.vampirism;

import net.minecraft.resources.ResourceLocation;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * Class to store constants and stuff
 */
public class REFERENCE {
    public static final String MODID = "vampirism";
    public static final String NAME = "Vampirism";
    public static final String MINECRAFT_VERSION = "@MVERSION@";
    public static final String FORGE_VERSION_MIN = "14.23.1.2554";
    public static final int HIGHEST_VAMPIRE_LEVEL = 14;
    public static final int HIGHEST_HUNTER_LEVEL = 14;
    public static final int HIGHEST_VAMPIRE_LORD = 5;
    public static final int HIGHEST_HUNTER_LORD = 5;
    public static final String FORGE_VERSION = "@FVERSION@";
    public static final String VERSION_UPDATE_FILE = "https://maxanier.de/projects/vampirism/versions.json";
    public static final String SUPPORTER_FILE = "https://maxanier.de/projects/vampirism/supporters.json";
    public static final String CURSEFORGE_LINK = "https://minecraft.curseforge.com/projects/vampirism-become-a-vampire";
    public static final String INTEGRATIONS_MODID = "vampirism_integrations";
    public static final String INTEGRATIONS_LINK = "https://minecraft.curseforge.com/projects/vampirism-integrations";
    public static final String GUIDEAPI_LINK = "https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage";
    public static final String SETTINGS_API = "https://api.vampirism.dev/api";
    public static final String SETTINGS_API_VERSION = "v1";
    public static final int EYE_TYPE_COUNT = 16;
    public static final int FANG_TYPE_COUNT = 7;
    /**
     * Check for vampire garlic damage every n ticks
     * Must be higher than 1, due to implementation
     */
    public final static int REFRESH_GARLIC_TICKS = 40;
    /**
     * Check for vampire sun damage every n ticks
     * Must be higher than 2 due to implementation
     */
    public final static int REFRESH_SUNDAMAGE_TICKS = 8;
    public final static ResourceLocation FACTION_PLAYER_HANDLER_KEY = new ResourceLocation(MODID, "ifactionplayerhandler");
    public final static ResourceLocation VAMPIRE_PLAYER_KEY = new ResourceLocation(MODID, "vampire");
    public final static ResourceLocation HUNTER_PLAYER_KEY = new ResourceLocation(MODID, "hunter");
    public final static ResourceLocation EXTENDED_CREATURE_KEY = new ResourceLocation(MODID, "iextendedcreature");
    public final static ResourceLocation WORLD_CAP_KEY = new ResourceLocation(MODID, "world");
    public static ArtifactVersion VERSION = new DefaultArtifactVersion("0.0.0");

}
