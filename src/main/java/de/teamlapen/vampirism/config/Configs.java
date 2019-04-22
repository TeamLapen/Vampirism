package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Manages configuration
 */
public class Configs {

    public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String CATEGORY_GUI = "gui";
    public static final String CATEGORY_VILLAGE = "village_settings";
    public static final String CATEGORY_BALANCE = "balance";
    public static final String CATEGORY_DISABLE = "disabled";
    public static final String CATEGORY_WORLDGEN = "world_gen";
    private final static String TAG = "Configs";
    public static boolean realism_mode;
    public static boolean resetConfigurationInDev;
    public static int gui_level_offset_x;
    public static int gui_level_offset_y;
    public static boolean gui_skill_button_enable;
    public static boolean renderVampireForestFog;
    public static boolean renderVampireForestFogEnforce;
    public static int blood_vision_recompile_ticks;
    public static int coffin_sleep_percentage;
    public static boolean pvp_only_between_factions;
    public static boolean bat_mode_in_end;
    public static boolean unlock_all_skills;
    public static int sunscreen_beacon_distance;
    public static boolean autoCalculateEntityBlood;
    public static boolean sunscreen_beacon_mineable;
    public static boolean playerCanTurnPlayer;


    public static int village_size;
    public static int village_density;
    public static int village_min_dist;
    public static boolean village_modify;

    public static boolean disable_replaceVanillaNightVision;
    public static boolean disable_vampireForest;
    public static boolean disable_factionDisplayChat;
    public static boolean disable_versionCheck;
    public static boolean disable_advancedMobPlayerFaces;
    public static boolean updated_vampirism;
    public static boolean disable_vampireEyes;
    public static boolean disable_config_sync;
    public static boolean disable_screen_overlay;
    public static boolean disable_collectVersionStat;
    public static boolean disable_fang_infection;
    public static boolean disable_mob_bite_infection;
    public static boolean disable_hunter_camps;
    public static boolean disable_all_worldgen;
    public static boolean disable_halloween_special;

    public static @Nonnull
    int[] worldGenDimensions = new int[0];

    public static boolean autoConvertGlasBottles;
    private static Configuration main_config;
    private static boolean overriddenByServer = false;

    public static void init(File configDir, boolean inDev) {
        File mainConfigFile = new File(configDir, REFERENCE.MODID + ".cfg");
        main_config = new Configuration(mainConfigFile, REFERENCE.VERSION);
        loadConfiguration(false);
        if (updated_vampirism) VampirismMod.log.i(TAG, "Vampirism seems to have been updated");
        VampirismMod.log.i(TAG, "Loaded configuration");
    }

    private static void loadConfiguration(boolean dontSave) {
        // Categories
        ConfigCategory cat_village = main_config.getCategory(CATEGORY_VILLAGE);
        cat_village.setComment("Here you can configure the village generation");
        ConfigCategory cat_general = main_config.getCategory(CATEGORY_GENERAL);
        cat_general.setComment("General settings");
        ConfigCategory cat_disabled = main_config.getCategory(CATEGORY_DISABLE);
        cat_disabled.setComment("You can disable some features here, but it is not recommend and might cause problems (e.g. you can't get certain items");
        ConfigCategory cat_gui = main_config.getCategory(CATEGORY_GUI);
        cat_gui.setComment("Adjust some of Vampirism's gui elements");
        ConfigCategory cat_world_gen = main_config.getCategory(CATEGORY_WORLDGEN);
        cat_world_gen.setComment("Adjust Vampirism's world generation features");

        //General
        realism_mode = main_config.getBoolean("vampire_realism_mode", CATEGORY_GENERAL, false, "Changes a few things and changes some default balance values to make it more 'realistic'. You have to reset the balance values and restart MC after changing this.");
        resetConfigurationInDev = main_config.getBoolean("reset_configuration_in_dev", CATEGORY_GENERAL, true, "Only relevant for developers");
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).setDefaultDimsSundamage(main_config.getBoolean("sundamage_default", CATEGORY_GENERAL, false, "Whether you should receive sundamge in unknown dimension or not"));
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).resetConfigurations();
        String[] sundamageDims = main_config.getStringList("sundamage_dims", CATEGORY_GENERAL, new String[0], "Specify if individual dimensions should have sundamage. Use e.g. '5:1' to enable sundamage for dimension 5 or '5:0' to disable it");
        for (String s : sundamageDims) {
            if (s.isEmpty()) continue;
            String[] t = s.split(":");
            if (t.length != 2) {
                VampirismMod.log.w(TAG, "Cannot understand sundamage dimension line '%s'. Missing separator", s);
                continue;
            }
            try {
                int dim = Integer.valueOf(t[0]);
                boolean type = Integer.valueOf(t[1]) != 0;
                ((SundamageRegistry) VampirismAPI.sundamageRegistry()).specifyConfiguredSundamageForDim(dim, type);
            } catch (NumberFormatException e) {
                VampirismMod.log.w(TAG, "Cannot understand sundamage dimension line '%s'. Failed to convert numbers", s);
                continue;
            }
        }
        String[] sundamageDisabledBiomes = main_config.getStringList("sundamage_disabled_biomes", CATEGORY_GENERAL, new String[0], "Specifiy biomes in which players should not get sundamage. Use e.g. 'minecraft:mesa' to disable sundamage in Mesa biome. Use '/vampirism-test biome' to find out the current biome id");
        for (String s : sundamageDisabledBiomes) {
            if (s.isEmpty()) continue;
            try {
                ResourceLocation res = new ResourceLocation(s);
                ((SundamageRegistry) VampirismAPI.sundamageRegistry()).addNoSundamageBiomeConfigured(res);
            } catch (Exception e) {
                VampirismMod.log.e(TAG, e, "Failed to parse no sundamage biome id %s", s);
            }

        }

        playerCanTurnPlayer = main_config.getBoolean("player_can_turn_player", CATEGORY_GENERAL, true, "If one player can bite infect a human player with sanguinare");
        renderVampireForestFog = main_config.getBoolean("vampire_forest_fog", CATEGORY_GENERAL, true, "");
        renderVampireForestFogEnforce = main_config.getBoolean("vampire_forest_fog_enforce", CATEGORY_GENERAL, true, "Prevents clients from disabling the fog client side");
        blood_vision_recompile_ticks = main_config.getInt("blood_vision_recompile", CATEGORY_GENERAL, 3, 1, 100, "Every n tick the blood vision entities are recompiled - Might have a performance impact");

        autoConvertGlasBottles = main_config.getBoolean("auto_convert_glas_bottles", CATEGORY_GENERAL, true, "If glas bottles should automatically be converted to blood bottles if needed");
        coffin_sleep_percentage = main_config.getInt("coffin_sleep_percantage", CATEGORY_GENERAL, 100, 1, 100, "Percentage of players that have to lay in a coffin to make it night. Be careful with values below 51 if e.g. Morpheus is installed");
        pvp_only_between_factions = main_config.getBoolean("pvp_only_between_factions", CATEGORY_GENERAL, false, "If PVP should only be allowed between factions. PVP has to be enabled in the server properties for this. Not guaranteed to always protect player from teammates");
        bat_mode_in_end = main_config.getBoolean("bat_mode_in_end", CATEGORY_GENERAL, false, "If vampires can convert to a bat in the end");
        unlock_all_skills = main_config.getBoolean("unlock_all_skills_at_max", CATEGORY_GENERAL, false, "CHEAT: If enabled, you will be able to unlock all skills at max level");
        sunscreen_beacon_distance = main_config.getInt("sunscreen_beacon_distance", CATEGORY_GENERAL, 32, 1, 40000, "Block radius, the sunscreen beacon affects");
        sunscreen_beacon_mineable = main_config.getBoolean("sunscreen_beacon_mineable", CATEGORY_GENERAL, false, "If the sunscreen beacon can be mined in survival");
        autoCalculateEntityBlood = main_config.getBoolean("auto_calculate_entity_blood", CATEGORY_GENERAL, true, "Calculate the blood level for unknown creatures based on their size");

        //Village
        village_modify = main_config.getBoolean("village_modify_gen", CATEGORY_VILLAGE, true, "Whether to modify village generation chance or not");
        village_density = main_config.getInt("village_density", CATEGORY_VILLAGE, 18, 9, 1000, "Minecraft will try to generate 1 village per NxN chunk area. Vanilla: 32");
        village_min_dist = main_config.getInt("village_minimum_distance", CATEGORY_VILLAGE, 6, 1, 1000, "Village centers will be at least N chunks apart. Must be smaller than density. Vanilla: 8");
        village_size = main_config.getInt("village_size", CATEGORY_VILLAGE, 0, 0, 10, "A higher size increases the overall spawn weight of buildings.");

        // Gui
        gui_level_offset_x = main_config.getInt("level_offset_x", CATEGORY_GUI, 0, -250, 250, "X-Offset of the level indicator from the center in pixels");
        gui_level_offset_y = main_config.getInt("level_offset_y", CATEGORY_GUI, 47, 0, 270, "Y-Offset of the level indicator from the bottom in pixels");
        gui_skill_button_enable = main_config.getBoolean("skill_button_enable", CATEGORY_GUI, true, "If the skill button in inventory should be rendered");


        //WorldGen
        worldGenDimensions = main_config.get(CATEGORY_WORLDGEN, "world_gen_dimensions", new int[0], "List of dimensions ids Vampirism tries to execute worldgen besides DIM0").getIntList();

        //Disable
        disable_replaceVanillaNightVision = main_config.getBoolean("disable_replace_night_vision", CATEGORY_DISABLE, false, "Disable replacing vanilla night vision, if disabled the potion is shown to the player all the time");
        disable_factionDisplayChat = main_config.getBoolean("disable_faction_display_chat", CATEGORY_DISABLE, false, "Do not display the player's current faction in chat");
        disable_vampireForest = main_config.getBoolean("disable_vampire_forest", CATEGORY_DISABLE, false, "Disable vampire forest generation");
        disable_versionCheck = main_config.getBoolean("disable_version_check", CATEGORY_DISABLE, false, "Disable vampirism's version check");
        disable_advancedMobPlayerFaces = main_config.getBoolean("disable_advanced_mob_player_face", CATEGORY_DISABLE, false, "Disable the rendering of other player faces for the advanced hunter and advanced vampire");
        disable_vampireEyes = main_config.getBoolean("disable_vampire_player_eyes", CATEGORY_DISABLE, false, "Disables the rendering of vampire eyes");
        disable_config_sync = main_config.getBoolean("disable_config_sync", CATEGORY_DISABLE, false, "Disable syncing config between server and client. (Note: Only a few settings are synced anyway)");
        disable_screen_overlay = main_config.getBoolean("disable_screen_overlay", CATEGORY_DISABLE, false, "Disable the colored overlay (sunindicator, disguise or rage)  if they cause problems.");
        disable_collectVersionStat = main_config.getBoolean("disable_collect_basic_version_stat", CATEGORY_DISABLE, false, "Disable sending Mod version, MC version and mod count on version check");
        disable_fang_infection = main_config.getBoolean("disable_fang_infection", CATEGORY_DISABLE, false, "Disable vampire fangs being useable to infect yourself");
        disable_mob_bite_infection = main_config.getBoolean("disable_mob_bite_infection", CATEGORY_DISABLE, false, "Prevent vampire mobs from infecting players when attacking");
        disable_hunter_camps = main_config.getBoolean("disable_hunter_camps", CATEGORY_DISABLE, false, "Disable the generation of hunter camps completely");
        disable_all_worldgen = main_config.getBoolean("disable_all_worldgen", CATEGORY_DISABLE, false, "Disable all world gen. Does not affect vampire Forest");
        disable_halloween_special = main_config.getBoolean("disable_halloween_special", CATEGORY_DISABLE, false, "Disable halloween special event");

        updated_vampirism = !main_config.getDefinedConfigVersion().equals(main_config.getLoadedConfigVersion());

        if (!dontSave && (main_config.hasChanged() || updated_vampirism)) {
            main_config.save();
        }
    }

    public static void onConfigurationChanged() {
        VampirismMod.log.i(TAG, "Reloading changed configuration");
        loadConfiguration(false);
    }

    public static Configuration getMainConfig() {
        return main_config;
    }


    @OnlyIn(Dist.CLIENT)
    public static void onDisconnectedFromServer() {
        if (overriddenByServer) {
            VampirismMod.log.d(TAG, "Disconnected from server -> Reloading config");
            loadConfiguration(true);
            overriddenByServer = false;
        }
    }


    public static void writeToNBTServer(NBTTagCompound nbt) {
        if (renderVampireForestFogEnforce) {
            nbt.setBoolean("vampire_forest_fog", renderVampireForestFog);
        }
        nbt.setBoolean("pvp_only_between_factions", pvp_only_between_factions);
    }

    @OnlyIn(Dist.CLIENT)
    public static void readFromNBTClient(NBTTagCompound nbt) {
        overriddenByServer = true;
        if (nbt.hasKey("vampire_forest_fog")) {
            renderVampireForestFog = nbt.getBoolean("vampire_forest_fog");
        }
        pvp_only_between_factions = nbt.getBoolean("pvp_only_between_factions");
    }
}
