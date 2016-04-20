package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages configuration
 */
public class Configs {

    public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String CATEGORY_GUI = "gui";
    public static final String CATEGORY_VILLAGE = "village_settings";
    public static final String CATEGORY_BALANCE = "balance";
    public static final String CATEGORY_DISABLE = "disabled";
    private final static String TAG = "Configs";
    public static boolean realism_mode;
    public static boolean resetConfigurationInDev;
    public static int gui_level_offset_x;
    public static int gui_level_offset_y;
    public static boolean renderVampireForestFog;
    public static int blood_vision_recompile_ticks;


    public static int village_size;
    public static int village_density;
    public static int village_min_dist;
    public static boolean village_modify;

    public static boolean disable_replaceVanillaNightVision;
    public static boolean disable_vampireForest;
    public static boolean disable_factionDisplayChat;
    public static boolean disable_versionCheck;
    public static boolean disable_advancedMobPlayerFaces;
    public static boolean playerCanTurnPlayer;
    public static boolean updated_vampirism;

    public static boolean autoConvertGlasBottles;
    private static Configuration main_config;

    public static void init(File configDir, boolean inDev) {
        File mainConfigFile = new File(configDir, REFERENCE.MODID + ".cfg");
        File bloodConfigFile = new File(configDir, REFERENCE.MODID + "_blood_values.txt");

        try {

            Map<String, Integer> defaultValues = loadBloodValuesFromReader(new InputStreamReader(Configs.class.getResourceAsStream("/default_blood_values.txt")), "default_blood_values.txt");
            VampirismAPI.biteableRegistry().addBloodValues(defaultValues);
        } catch (IOException e) {
            VampirismMod.log.e(TAG, e, "Could not read default blood values, this should not happen and destroys the mod experience");
        }
        if (bloodConfigFile.exists()) {
            try {
                Map<String, Integer> override = loadBloodValuesFromReader(new FileReader(bloodConfigFile), bloodConfigFile.getName());
                VampirismAPI.biteableRegistry().overrideBloodValues(override);
                VampirismMod.log.i(TAG, "Successfully loaded additional blood value file");
            } catch (IOException e) {
                VampirismMod.log.e(TAG, "Could not read blood values from config file %s", bloodConfigFile.getName());
            }
        }

        main_config = new Configuration(mainConfigFile, REFERENCE.VERSION);
        loadConfiguration();
        if (updated_vampirism) VampirismMod.log.i(TAG, "Vampirism seems to have been updated");
        VampirismMod.log.i(TAG, "Loaded configuration");
    }

    private static void loadConfiguration() {
        // Categories
        ConfigCategory cat_village = main_config.getCategory(CATEGORY_VILLAGE);
        cat_village.setComment("Here you can configure the village generation");
        ConfigCategory cat_general = main_config.getCategory(CATEGORY_GENERAL);
        cat_general.setComment("General settings");
        ConfigCategory cat_disabled = main_config.getCategory(CATEGORY_DISABLE);
        cat_disabled.setComment("You can disable some features here, but it is not recommend and might cause problems (e.g. you can't get certain items");
        ConfigCategory cat_gui = main_config.getCategory(CATEGORY_GUI);
        cat_gui.setComment("Adjust some of Vampirism's gui elements");

        //General
        realism_mode = main_config.getBoolean("vampire_realism_mode", CATEGORY_GENERAL, false, "Changes a few things and changes some default balance values to make it more 'realistic'. You have to reset the balance values and restart MC after changing this.");
        resetConfigurationInDev = main_config.getBoolean("reset_configuration_in_dev", CATEGORY_GENERAL, true, "Only relevant for developers");
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).setDefaultDimsSundamage(main_config.getBoolean("sundamage_default", CATEGORY_GENERAL, false, "Whether you should receive sundamge in unknown dimension or not"));
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).resetConfiguredSundamgeDims();
        String[] sundamageDims = main_config.getStringList("sundamage_dims", CATEGORY_GENERAL, new String[0], "Specify if individual dimensions should have sundamage. Use e.g. '5:1' to enable sundamage for dimension 5 or '5:0' to disable it");
        for (String s : sundamageDims) {
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
                VampirismMod.log.w(TAG, "Cannot understand sundamge dimension line '%s'. Failed to convert numbers", s);
                continue;
            }
        }


        playerCanTurnPlayer = main_config.getBoolean("player_can_turn_player", CATEGORY_GENERAL, true, "If one player can bite infect a human player with sanguinare");
        renderVampireForestFog = main_config.getBoolean("vampire_forest_fog", CATEGORY_GENERAL, true, "");
        blood_vision_recompile_ticks = main_config.getInt("blood_vision_recompile", CATEGORY_GENERAL, 3, 1, 100, "Every n tick the blood vision entities are recompiled - Might have a performance impact");

        autoConvertGlasBottles = main_config.getBoolean("auto_convert_glas_bottles", CATEGORY_GENERAL, true, "If glas bottles should automatically be converted to blood bottles if needed");

        //Village
        village_modify = main_config.getBoolean("village_modify_gen", CATEGORY_VILLAGE, true, "Whether to modify village generation chance or not");
        village_density = main_config.getInt("village_density", CATEGORY_VILLAGE, 22, 1, 1000, "Minecraft will try to generate 1 village per NxN chunk area. Vanilla: 32");
        village_min_dist = main_config.getInt("village_minimum_distance", CATEGORY_VILLAGE, 6, 1, 1000, "Village centers will be at least N chunks apart. Must be smaller than density. Vanilla: 8");
        village_size = main_config.getInt("village_size", CATEGORY_VILLAGE, 0, 0, 10, "A higher size increases the overall spawn weight of buildings.");

        // Gui
        gui_level_offset_x = main_config.getInt("level_offset_x", CATEGORY_GUI, 0, -250, 250, "X-Offset of the level indicator from the center in pixels");
        gui_level_offset_y = main_config.getInt("level_offset_y", CATEGORY_GUI, 47, 0, 270, "Y-Offset of the level indicator from the bottom in pixels");


        //Disable
        disable_replaceVanillaNightVision = main_config.getBoolean("disable_replace_night_vision", CATEGORY_DISABLE, false, "Disable replacing vanilla night vision, if disabled the potion is shown to the player all the time");
        disable_factionDisplayChat = main_config.getBoolean("disable_faction_display_chat", CATEGORY_DISABLE, false, "Do not display the player's current faction in chat");
        disable_vampireForest = main_config.getBoolean("disable_vampire_forest", CATEGORY_DISABLE, false, "Disable vampire forest generation");
        disable_versionCheck = main_config.getBoolean("disable_version_check", CATEGORY_DISABLE, false, "Disable vampirism's version check");
        disable_advancedMobPlayerFaces = main_config.getBoolean("disable_advanced_mob_player_face", CATEGORY_DISABLE, false, "Disable the rendering of other player faces for the advanced hunter and advanced vampire");

        updated_vampirism = !main_config.getDefinedConfigVersion().equals(main_config.getLoadedConfigVersion());

        if (main_config.hasChanged() || updated_vampirism) {
            main_config.save();
        }
    }

    public static void onConfigurationChanged() {
        VampirismMod.log.i(TAG, "Reloading changed configuration");
        loadConfiguration();
    }

    public static Configuration getMainConfig() {
        return main_config;
    }

    /**
     * @param r    Reader the values should be read from
     * @param file Just for logging of errors
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static Map<String, Integer> loadBloodValuesFromReader(Reader r, String file) throws IOException {
        Map<String, Integer> bloodValues = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (line.isEmpty()) continue;
                String[] p = line.split("=");
                if (p.length != 2) {
                    VampirismMod.log.w("ReadBlood", "Line %s  in %s is not formatted properly", line, file);
                    continue;
                }
                int val;
                try {
                    val = Integer.parseInt(p[1]);
                } catch (NumberFormatException e) {
                    VampirismMod.log.w("ReadBlood", "Line %s  in %s is not formatted properly", line, file);
                    continue;
                }
                bloodValues.put(p[0], val);
            }
        } finally {
            if (br != null) {
                br.close();
            }
            r.close();
        }
        return bloodValues;

    }
}
