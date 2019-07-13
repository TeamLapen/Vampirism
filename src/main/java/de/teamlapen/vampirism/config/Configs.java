package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.forge.ConfigCategory;
import de.teamlapen.lib.lib.config.forge.Configuration;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Manages configuration
 */
public class Configs {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String CATEGORY_GUI = "gui";
    public static final String CATEGORY_VILLAGE = "village_settings";
    public static final String CATEGORY_BALANCE = "balance";
    public static final String CATEGORY_DISABLE = "disabled";
    public static final String CATEGORY_WORLDGEN = "world_gen";
    public static boolean realism_mode;
    public static boolean resetConfigurationInDev;


    public static boolean updated_vampirism;


    private static Configuration main_config;
    private static boolean overriddenByServer = false;

    public static void init(File configDir, boolean inDev) {
        File mainConfigFile = new File(configDir, REFERENCE.MODID + ".cfg");
        main_config = new Configuration(mainConfigFile, REFERENCE.VERSION);
        loadConfiguration(false);
        if (updated_vampirism) LOGGER.info(LogUtil.CONFIG, "Vampirism seems to have been updated");
        LOGGER.info("Loaded configuration");
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
        String[] sundamageDims = main_config.getStringList("sundamage_dims", CATEGORY_GENERAL, new String[0], "Specify if individual dimensions should have sundamage. Use e.g. '5:1' to enable sundamage for dimension 5 or '5:0' to disable it");//TODO config must take dimensions ResourceLocation
        for (String s : sundamageDims) {
            if (s.isEmpty()) continue;
            String[] t = s.split(":");
            if (t.length != 2) {
                LOGGER.warn(LogUtil.CONFIG, "Cannot understand sundamage dimension line '{}'. Missing separator", s);
                continue;
            }
            try {
                int dimid = Integer.valueOf(t[0]);
                boolean type = Integer.valueOf(t[1]) != 0;
                DimensionType dim = DimensionType.getById(dimid);
                ((SundamageRegistry) VampirismAPI.sundamageRegistry()).specifyConfiguredSundamageForDim(dim, type);
            } catch (NumberFormatException e) {
                LOGGER.warn(LogUtil.CONFIG, "Cannot understand sundamage dimension line '{}'. Failed to convert numbers", s);
            }
        }
        String[] sundamageDisabledBiomes = main_config.getStringList("sundamage_disabled_biomes", CATEGORY_GENERAL, new String[0], "Specifiy biomes in which players should not get sundamage. Use e.g. 'minecraft:mesa' to disable sundamage in Mesa biome. Use '/vampirism-test biome' to find out the current biome id");
        for (String s : sundamageDisabledBiomes) {
            if (s.isEmpty()) continue;
            try {
                ResourceLocation res = new ResourceLocation(s);
                ((SundamageRegistry) VampirismAPI.sundamageRegistry()).addNoSundamageBiomeConfigured(res);
            } catch (Exception e) {
                LOGGER.error(LogUtil.CONFIG, "Failed to parse no sundamage biome id " + s, e);
            }

        }


        //Disable


        updated_vampirism = false;//TODO 1.13 !main_config.getDefinedConfigVersion().equals(main_config.getLoadedConfigVersion());

        if (!dontSave && (main_config.hasChanged() || updated_vampirism)) {
            main_config.save();
        }
    }

    public static void onConfigurationChanged() {
        LOGGER.info(LogUtil.CONFIG, "Reloading changed configuration");
        loadConfiguration(false);
    }

    public static Configuration getMainConfig() {
        return main_config;
    }


    @OnlyIn(Dist.CLIENT)
    public static void onDisconnectedFromServer() {
        if (overriddenByServer) {
            LOGGER.trace(LogUtil.CONFIG, "Disconnected from server -> Reloading config");
            loadConfiguration(true);
            overriddenByServer = false;
        }
    }


    public static void writeToNBTServer(CompoundNBT nbt) {
        if (renderVampireForestFogEnforce) {
            nbt.putBoolean("vampire_forest_fog", renderVampireForestFog);
        }
        nbt.putBoolean("pvp_only_between_factions", pvp_only_between_factions);
    }

    @OnlyIn(Dist.CLIENT)
    public static void readFromNBTClient(CompoundNBT nbt) {
        overriddenByServer = true;
        if (nbt.contains("vampire_forest_fog")) {
            renderVampireForestFog = nbt.getBoolean("vampire_forest_fog");
        }
        pvp_only_between_factions = nbt.getBoolean("pvp_only_between_factions");
    }
}
