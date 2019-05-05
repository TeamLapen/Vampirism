package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle loading and saving of blood values.
 */
public class BloodValueLoader {

    private static final String TAG = "BloodValueLoader";
    /**
     * File to save dynamically calculated values to
     */
    private static @Nullable
    File bloodValueWorldFile;

    /**
     * Load Vampirism's built-in blood values including any values for third party mods.
     * Also loads config values
     *
     * @param configDir
     */
    public static void init(File configDir) {
        File bloodConfigFile = new File(configDir, REFERENCE.MODID + "_blood_values.txt");

        try {

            Map<ResourceLocation, Integer> defaultValues = loadBloodValuesFromReader(new InputStreamReader(BloodValueLoader.class.getResourceAsStream("/blood_values/default_blood_values.txt")), "default_blood_values.txt");
            VampirismAPI.entityRegistry().addBloodValues(defaultValues);
        } catch (IOException e) {
            VampirismMod.log.bigWarning(TAG, "Could not read default blood values, this should not happen and destroys the mod experience");
            LOGGER.error(e, "Exception");
        }

        if (bloodConfigFile.exists()) {
            try {
                Map<ResourceLocation, Integer> override = loadBloodValuesFromReader(new FileReader(bloodConfigFile), bloodConfigFile.getName());
                VampirismAPI.entityRegistry().overrideBloodValues(override);
                LOGGER.info("Successfully loaded additional blood value file");
            } catch (IOException e) {
                LOGGER.error("Could not read blood values from config file %s", bloodConfigFile.getName());
            }
        }

        loadBloodValuesModCompat("animania");
        loadBloodValuesModCompat("ancientwarfarenpc");
        loadBloodValuesModCompat("twilightforest");
        loadBloodValuesModCompat("sophisticatedwolves");
    }


    /**
     * @param r    Reader the values should be read from
     * @param file Just for logging of errors
     */
    private static Map<ResourceLocation, Integer> loadBloodValuesFromReader(Reader r, String file) throws IOException {
        Map<ResourceLocation, Integer> bloodValues = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (StringUtils.isBlank(line)) continue;
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
                bloodValues.put(new ResourceLocation(p[0]), val);
            }
        } finally {
            if (br != null) {
                br.close();
            }
            r.close();
        }
        return bloodValues;

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
            LOGGER.error(e, "Failed to write blood values (%s)", comment);
        } finally {
            if (bw != null) {
                bw.close();
            }
            w.close();
        }
        return false;
    }

    /**
     * Reads blood values for another mod from the Vampirism jar.
     */
    public static void loadBloodValuesModCompat(String modid) {
        if (!Loader.isModLoaded(modid)) return;
        try {
            Map<ResourceLocation, Integer> defaultValues = BloodValueLoader.loadBloodValuesFromReader(new InputStreamReader(BloodValueLoader.class.getResourceAsStream("/blood_values/" + modid + ".txt")), modid + ".txt");
            VampirismAPI.entityRegistry().addBloodValues(defaultValues);
        } catch (IOException e) {
            LOGGER.error(e, "[ModCompat]Could not read default blood values for mod %s, this should not happen", modid);
        } catch (NullPointerException e) {
            LOGGER.error(e, "[ModCompat]Could not find packed (in JAR) blood value file for mod %s", modid);
        }
    }

    /**
     * Reads automatically calculated values from world file
     */
    public static void loadDynamicBloodValues(File f) {
        try {
            Map<ResourceLocation, Integer> saved = BloodValueLoader.loadBloodValuesFromReader(new InputStreamReader(new FileInputStream(f)), f.getName());
            VampirismEntityRegistry.getBiteableEntryManager().addDynamic(saved);
        } catch (IOException e) {
            LOGGER.error(e, "[ModCompat]Could not read saved blood values from world from file  %s", f);
        }
    }

    /**
     * Saves blood values to file to be saved in world dir
     */
    public static void saveDynamicBloodValues(File f) {
        Map<ResourceLocation, Integer> values = VampirismEntityRegistry.getBiteableEntryManager().getValuesToSave();
        if (!f.exists() && values.isEmpty()) return; //Don't create a empty file
        if (!f.exists()) {
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
        }
        try {
            if (!writeBloodValues(new FileWriter(f), values, "Dynamically calculated blood values - DON'T EDIT")) {
                LOGGER.warn("Could not write dynamic values to file");
            }
        } catch (IOException e) {
            LOGGER.error(e, "Failed to write dynamic values to file");
        }
    }


    public static void onServerStarting(MinecraftServer server) {
        bloodValueWorldFile = new File(new File(server.getWorld(0).getSaveHandler().getWorldDirectory(), REFERENCE.MODID), "dynamic-blood-values.txt");
        if (bloodValueWorldFile.exists()) {
            loadDynamicBloodValues(bloodValueWorldFile);
        }
    }

    public static void onServerStopping() {
        if (bloodValueWorldFile != null) {
            saveDynamicBloodValues(bloodValueWorldFile);
            VampirismEntityRegistry.getBiteableEntryManager().resetDynamic();
        } else {
            LOGGER.warn("Can't save blood values. File does not exist");
        }

    }
}
