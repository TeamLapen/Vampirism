package de.teamlapen.lib.lib.util;

import com.google.common.collect.ImmutableList;
import de.teamlapen.lib.lib.config.forge.ConfigCategory;
import de.teamlapen.lib.lib.config.forge.Configuration;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles loading of mod compatibilities
 */
public class ModCompatLoader implements IInitListener {

    private final static Logger LOGGER = LogManager.getLogger();
    private final String configName;
    private
    @Nullable
    List<IModCompat> availableModCompats = new LinkedList<>();
    private List<IModCompat> loadedModCompats;

    @Nullable
    private Configuration config;

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

    /**
     * May be null before INIT
     *
     * @return The mod compat config file
     */
    @Nullable
    public Configuration getConfig() {
        return config;
    }

    public List<IModCompat> getLoadedModCompats() {
        return ImmutableList.copyOf(loadedModCompats);
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
        if (step == Step.COMMON_SETUP) {
            prepareModCompats(FMLPaths.CONFIGDIR.get().toFile());
        }
        Iterator<IModCompat> it = loadedModCompats.iterator();
        while (it.hasNext()) {
            IModCompat next = it.next();
            try {
                next.onInitStep(step, event);
            } catch (Exception e) {
                LOGGER.error(LogUtil.COMPAT, "---------------------------------------------------------");
                LOGGER.error(LogUtil.COMPAT, "Mod Compat {} threw an exception during {}. Unloading.", next.getModID(), step);
                LOGGER.error(LogUtil.COMPAT, "Issue", e);
                LOGGER.error(LogUtil.COMPAT, "---------------------------------------------------------");
                it.remove();
            }
        }
    }

    private boolean isModLoaded(IModCompat modCompat) {
        return ModList.get().isLoaded(modCompat.getModID());
    }

    private void prepareModCompats(File configDir) {
        if (availableModCompats == null) {
            LOGGER.warn("Trying to load mod compat twice");
            return;
        }
        config = new Configuration(new File(configDir, configName));

        List<IModCompat> loaded = new LinkedList<>();
        for (IModCompat modCompat : availableModCompats) {
            if (isModLoaded(modCompat)) {
                ConfigCategory compatCat = config.getCategory(modCompat.getModID());
                compatCat.setComment("Configure mod compatibility between Vampirism and " + modCompat.getModID());
                if (config.getBoolean("enable_compat_" + modCompat.getModID(), compatCat.getName(), true, "If the compatibility for this mod should be loaded")) {
                    modCompat.loadConfigs(config, compatCat);
                    loaded.add(modCompat);
                    LOGGER.trace(LogUtil.COMPAT, "Prepared {} compatibility", modCompat.getModID());
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
