package de.teamlapen.lib.lib.util;

import de.teamlapen.lib.VampLib;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles loading of mod compatibilities
 */
public class ModCompatLoader implements IInitListener {

    private final static String TAG = "ModCompat";
    private final String configName;
    private
    @Nullable
    List<IModCompat> availableModCompats = new LinkedList<>();
    private List<IModCompat> loadedModCompats;

    /**
     * @param configName Name for the config file. Can be a file in a folder
     */
    public ModCompatLoader(String configName) {
        this.configName = configName;
    }

    /**
     * Add any compats BEFORE pre-init
     *
     * @param compat The compat to add
     */
    public void addModCompat(IModCompat compat) {
        if (availableModCompats == null) {
            throw new IllegalStateException("Add mod compats BEFORE pre-init (" + compat.getModID() + ")");
        }
        availableModCompats.add(compat);
    }

    public List<IModCompat> getAvailableModCompats() {
        return availableModCompats;
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        if (step == Step.PRE_INIT) {
            prepareModCompats(((FMLPreInitializationEvent) event).getModConfigurationDirectory());
        }
        Iterator<IModCompat> it = loadedModCompats.iterator();
        while (it.hasNext()) {
            IModCompat next = it.next();
            try {
                next.onInitStep(step, event);
            } catch (Exception e) {
                VampLib.log.e(TAG, "---------------------------------------------------------");
                VampLib.log.e(TAG, e, "Mod Compat %s threw an exception during %s. Unloading.", next.getModID(), step);
                VampLib.log.e(TAG, "---------------------------------------------------------");
                it.remove();
            }
        }
    }

    private boolean isModLoaded(IModCompat modCompat) {
        return Loader.isModLoaded(modCompat.getModID());
    }

    private void prepareModCompats(File configDir) {
        if (availableModCompats == null) {
            VampLib.log.w(TAG, "Trying to load mod compat twice");
            return;
        }
        Configuration config = new Configuration(new File(configDir, configName));

        List<IModCompat> loaded = new LinkedList<>();
        for (IModCompat modCompat : availableModCompats) {
            if (isModLoaded(modCompat)) {
                ConfigCategory compatCat = config.getCategory(modCompat.getModID());
                compatCat.setComment("Configure mod compatibility between Vampirism and " + modCompat.getModID());
                if (config.getBoolean("enable_compat_" + modCompat.getModID(), compatCat.getName(), true, "If the compatibility for this mod should be loaded")) {
                    modCompat.loadConfigs(config, compatCat);
                    loaded.add(modCompat);
                    VampLib.log.d(TAG, "Prepared %s compatibility", modCompat.getModID());
                }
            }
        }
        if (config.hasChanged()) {
            config.save();
        }
        loadedModCompats = loaded;
        availableModCompats = null;
    }
}
