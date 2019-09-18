package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Map;

/**
 * Handle loading and saving of blood values for entities.
 */
public class BloodValueLoaderEntites extends BloodValueLoader {
    private final static Logger LOGGER = LogManager.getLogger();
    /**
     * File to save dynamically calculated values to
     */
    private @Nullable
    static File bloodValueWorldFile;

    private static BloodValueLoaderEntites INSTANCE = new BloodValueLoaderEntites();

    public static BloodValueLoaderEntites getInstance() {
        return INSTANCE;
    }

    public static void onServerStarting(MinecraftServer server) {
        bloodValueWorldFile = new File(new File(server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), REFERENCE.MODID), "calculated-blood-values.txt");
        if (bloodValueWorldFile.exists()) {
            INSTANCE.loadDynamicBloodValues(bloodValueWorldFile);
        }
    }

    public static void onServerStopping() {
        if (bloodValueWorldFile != null) {
            INSTANCE.saveDynamicBloodValues(bloodValueWorldFile);
        } else {
            LOGGER.warn("Can't save blood values. File does not exist");
        }

    }

    private static boolean writeBloodValues(Writer w, Map<ResourceLocation, Integer> values, String comment) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(w);
            bw.write('#');
            bw.write(comment);
            bw.newLine();
            for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
                bw.write(entry.getKey().toString());
                bw.write('=');
                bw.write(String.valueOf(entry.getValue()));
                bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "Failed to write blood values", e);
        } finally {
            if (bw != null) {
                bw.close();
            }
            w.close();
        }
        return false;
    }


    private BloodValueLoaderEntites() {
        super("vampirism_blood_values/entities", (values, multiplier) -> ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(values, multiplier), new ResourceLocation("multiplier"));
    }

    /**
     * Reads automatically calculated values from world file
     */
    private void loadDynamicBloodValues(File f) {
        try {
            Map<ResourceLocation, Integer> saved = loadBloodValuesFromReader(new InputStreamReader(new FileInputStream(f)), f.getName());
            VampirismEntityRegistry.biteableEntryManager.addCalculated(saved);
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "[ModCompat]Could not read saved blood values from world from file {} {}", f, e);
        }
    }

    /**
     * Saves blood values to file to be saved in world dir
     */
    private void saveDynamicBloodValues(File f) {
        Map<ResourceLocation, Integer> values = VampirismEntityRegistry.biteableEntryManager.getValuesToSave();
        if (!f.exists() && values.isEmpty()) return; //Don't create a empty file
        if (!f.exists()) {
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
        }
        try {
            if (!writeBloodValues(new FileWriter(f), values, "Dynamically calculated blood values - DON'T EDIT")) {
                LOGGER.warn(LogUtil.CONFIG, "Could not write calculated values to file");
            }
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "Failed to write calculated values to file", e);
        }
    }
}
