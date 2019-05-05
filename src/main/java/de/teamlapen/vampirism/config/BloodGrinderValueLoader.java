package de.teamlapen.vampirism.config;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle loading and saving of blood values.
 */
public class BloodGrinderValueLoader {

    private static final String TAG = "BloodValueGrinderLoader";

    private static final Map<ResourceLocation, Integer> bloodValues = new HashMap<>();

    /**
     * Load Vampirism's built-in blood values including any values for third party
     * mods. Also loads config values
     *
     * @param configDir
     */
    public static void init(File configDir) {

        File bloodConfigFile = new File(configDir, REFERENCE.MODID + "_blood_values_grinder.txt");

        try {

            bloodValues.putAll(loadBloodValuesFromReader(
                    new InputStreamReader(BloodGrinderValueLoader.class
                            .getResourceAsStream("/blood_values_grinder/default_blood_values.txt")),
                    "default_blood_values.txt"));
        } catch (IOException e) {
            VampirismMod.log.bigWarning(TAG,
                    "Could not read default blood grinder values, this should not happen and destroys the mod experience");
            LOGGER.error(e, "Exception");
        }

        if (bloodConfigFile.exists()) {
            try {
                bloodValues.putAll(
                        (loadBloodValuesFromReader(new FileReader(bloodConfigFile), bloodConfigFile.getName())));
                LOGGER.info("Successfully loaded additional blood grinder value file");
            } catch (IOException e) {
                LOGGER.error("Could not read blood grinder values from config file %s",
                        bloodConfigFile.getName());
            }
        }

    }

    /**
     * @param r
     *            Reader the values should be read from
     * @param file
     *            Just for logging of errors
     */
    private static Map<ResourceLocation, Integer> loadBloodValuesFromReader(Reader r, String file) throws IOException {

        Map<ResourceLocation, Integer> bloodValues = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;
                if (StringUtils.isBlank(line))
                    continue;
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

    /**
     * Reads blood values for another mod from the Vampirism jar.
     */
    public static void loadBloodValuesModCompat(String modid) {

        if (!Loader.isModLoaded(modid))
            return;
        try {
            bloodValues.putAll(BloodGrinderValueLoader.loadBloodValuesFromReader(new InputStreamReader(
                    BloodGrinderValueLoader.class.getResourceAsStream("/blood_values_grinder/" + modid + ".txt")),
                    modid + ".txt"));
        } catch (IOException e) {
            LOGGER.error(e,
                    "[ModCompat]Could not read default blood values for mod %s, this should not happen", modid);
        } catch (NullPointerException e) {
            LOGGER.error(e, "[ModCompat]Could not find packed (in JAR) blood value file for mod %s", modid);
        }
    }

    public static Map<ResourceLocation, Integer> getBloodGrinderValues() {

        return ImmutableMap.copyOf(bloodValues);
    }
}
