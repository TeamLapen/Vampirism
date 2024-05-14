package de.teamlapen.vampirism;

import de.teamlapen.lib.util.QualifiedVersion;
import de.teamlapen.vampirism.api.VReference;
import net.neoforged.fml.ModList;

/**
 * Class to store constants and stuff
 */
public class REFERENCE {
    // Vampirism
    public static final String MODID = VReference.MODID;
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
    public static final String CURSEFORGE_LINK = "https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire";
    public static final String MODRINTH_LINK = "https://modrinth.com/mod/vampirism";
    public static final String GUIDEAPI_LINK = "https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage";
    public static final String INTEGRATIONS_LINK = "https://minecraft.curseforge.com/projects/vampirism-integrations";
    public static final String SETTINGS_API = "https://api.vampirism.dev/api/v1";

}