package de.teamlapen.vampirism;

import de.teamlapen.lib.util.QualifiedVersion;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

/**
 * Class to store constants and stuff
 */
public class REFERENCE {
    // Vampirism
    public static final String MODID = "vampirism";
    public static final String NAME = "Vampirism";
    public static final String INTEGRATIONS_MODID = "vampirism_integrations";
    public static final QualifiedVersion VERSION = new QualifiedVersion(ModList.get().getModContainerById(MODID).map(s -> s.getModInfo().getVersion().toString()).orElse("1.0.0"));

    // changeable values
    public static final int HIGHEST_VAMPIRE_LEVEL = 14;
    public static final int HIGHEST_HUNTER_LEVEL = 14;
    public static final int HIGHEST_VAMPIRE_LORD = 5;
    public static final int HIGHEST_HUNTER_LORD = 5;
    public static final int EYE_TYPE_COUNT = 16;
    public static final int FANG_TYPE_COUNT = 7;
    /**
     * Check for vampire garlic damage every n ticks
     * Must be higher than 1, due to implementation
     */
    public static final int REFRESH_GARLIC_TICKS = 40;
    /**
     * Check for vampire sun damage every n ticks
     * Must be higher than 2 due to implementation
     */
    public static final int REFRESH_SUNDAMAGE_TICKS = 8;

    // links
    public static final String CURSEFORGE_LINK = "https://minecraft.curseforge.com/projects/vampirism-become-a-vampire";
    public static final String GUIDEAPI_LINK = "https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage";
    public static final String INTEGRATIONS_LINK = "https://minecraft.curseforge.com/projects/vampirism-integrations";
    public static final String SETTINGS_API = "https://api.vampirism.dev/api";
    public static final String SETTINGS_API_VERSION = "v1";

    // fixed values
    public static final ResourceLocation FACTION_PLAYER_HANDLER_KEY = new ResourceLocation(MODID, "ifactionplayerhandler");
    public static final ResourceLocation VAMPIRE_PLAYER_KEY = new ResourceLocation(MODID, "vampire");
    public static final ResourceLocation HUNTER_PLAYER_KEY = new ResourceLocation(MODID, "hunter");
    public static final ResourceLocation EXTENDED_CREATURE_KEY = new ResourceLocation(MODID, "iextendedcreature");
    public static final ResourceLocation WORLD_CAP_KEY = new ResourceLocation(MODID, "world");
}
