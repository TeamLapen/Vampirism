package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

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
    private final static String TAG = "Configs";
    public static boolean realism_mode;
    public static boolean resetConfigurationInDev;
    public static boolean sundamage_default;
    private static Configuration main_config;

    public static void init(File configDir, boolean inDev){
        File mainConfigFile=new File(configDir, REFERENCE.MODID+".cfg");
        File bloodConfigFile=new File(configDir,REFERENCE.MODID+"_blood_values.txt");

        //TODO load blood values

        main_config=new Configuration(mainConfigFile);
        loadConfiguration();
        VampirismMod.log.i(TAG,"Loaded configuration");
    }

    private static void loadConfiguration(){
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
        resetConfigurationInDev = main_config.getBoolean("reset_configuration_in_dev",CATEGORY_GENERAL,true,"Only relevant for developers");
        VampirismAPI.setDefaultDimsSundamage(main_config.getBoolean("sundamage_default",CATEGORY_GENERAL,false,"Whether you should receive sundamge in unknown dimension or not"));
        VampirismAPI.resetConfiguredSundamgeDims();
        String[] sundamageDims=main_config.getStringList("sundamage_dims",CATEGORY_GENERAL,new String[0],"Specify if individual dimensions should have sundamage. Use e.g. '5:1' to enable sundamage for dimension 5 or '5:0' to disable it");
        for(String s:sundamageDims){
            String[] t=s.split(":");
            if(t.length!=2){
                VampirismMod.log.w(TAG, "Cannot understand sundamage dimension line '%s'. Missing separator", s);
                continue;
            }
            try {
                int dim=Integer.valueOf(t[0]);
                boolean type=Integer.valueOf(t[1])!=0?true:false;
                VampirismAPI.specifyConfiguredSundamageForDim(dim,type);
            } catch (NumberFormatException e) {
                VampirismMod.log.w(TAG,"Cannot understand sundamge dimension line '%s'. Failed to convert numbers",s);
                continue;
            }
        }
        if(main_config.hasChanged())    {
            main_config.save();
        }
    }

    public static void onConfigurationChanged(){
        VampirismMod.log.i(TAG,"Reloading changed configuration");
        loadConfiguration();
    }

    public static Configuration getMainConfig(){
        return main_config;
    }
}
