package de.teamlapen.vampirism.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 * Handle loading and saving of blood values.
 */
public class BloodGrinderValueLoader {

	private static final String TAG = "BloodValueGrinderLoader";

	public static Map<ResourceLocation, Integer> bloodValues = new HashMap<>();
	/**
	 * File to save dynamically calculated values to
	 */
	private static @Nullable File bloodValueGrinderWorldFile;

	/**
	 * Load Vampirism's built-in blood values including any values for third party
	 * mods. Also loads config values
	 *
	 * @param configDir
	 */
	public static void init(File configDir) {
		File bloodConfigFile = new File(configDir, REFERENCE.MODID + "_blood_values_grinder.txt");

		try {

			bloodValues.putAll(loadBloodValuesFromReader(new InputStreamReader(BloodGrinderValueLoader.class.getResourceAsStream("/blood_values_grinder/default_blood_values.txt")), "default_blood_values.txt"));
		}
		catch (IOException e) {
			VampirismMod.log.bigWarning(TAG, "Could not read default blood grinder values, this should not happen and destroys the mod experience");
			VampirismMod.log.e(TAG, e, "Exception");
		}

		if (bloodConfigFile.exists()) {
			try {
				bloodValues.putAll((loadBloodValuesFromReader(new FileReader(bloodConfigFile), bloodConfigFile.getName())));
				VampirismMod.log.i(TAG, "Successfully loaded additional blood grinder value file");
			}
			catch (IOException e) {
				VampirismMod.log.e(TAG, "Could not read blood grinder values from config file %s", bloodConfigFile.getName());
			}
		}

		loadBloodValuesModCompat("test");
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
				}
				catch (NumberFormatException e) {
					VampirismMod.log.w("ReadBlood", "Line %s  in %s is not formatted properly", line, file);
					continue;
				}
				bloodValues.put(new ResourceLocation(p[0]), val);
			}
		}
		finally {
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
		}
		catch (IOException e) {
			VampirismMod.log.e(TAG, e, "Failed to write blood grinder values (%s)", comment);
		}
		finally {
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
		if (!Loader.isModLoaded(modid))
			return;
		try {
			bloodValues.putAll(BloodGrinderValueLoader.loadBloodValuesFromReader(new InputStreamReader(BloodGrinderValueLoader.class.getResourceAsStream("/blood_values_grinder/" + modid + ".txt")), modid + ".txt"));
		}
		catch (IOException e) {
			VampirismMod.log.e(TAG, e, "[ModCompat]Could not read default blood values for mod %s, this should not happen", modid);
		}
		catch (NullPointerException e) {
			VampirismMod.log.e(TAG, e, "[ModCompat]Could not find packed (in JAR) blood value file for mod %s", modid);
		}
	}

	/**
	 * Reads automatically calculated values from world file
	 */
	public static void loadDynamicBloodValues(File f) {
		try {
			Map<ResourceLocation, Integer> saved = BloodGrinderValueLoader.loadBloodValuesFromReader(new InputStreamReader(new FileInputStream(f)), f.getName());
			VampirismEntityRegistry.getBiteableEntryManager().addDynamic(saved);
		}
		catch (IOException e) {
			VampirismMod.log.e(TAG, e, "[ModCompat]Could not read saved blood values from world from file  %s", f);
		}
	}

	/**
	 * Saves blood values to file to be saved in world dir
	 */
	public static void saveDynamicBloodValues(File f) {
		Map<ResourceLocation, Integer> values = VampirismEntityRegistry.getBiteableEntryManager().getValuesToSave();
		if (!f.exists() && values.isEmpty())
			return; // Don't create a empty file
		if (!f.exists()) {
			if (f.getParentFile() != null)
				f.getParentFile().mkdirs();
		}
		try {
			if (!writeBloodValues(new FileWriter(f), values, "Dynamically calculated blood grinder values - DON'T EDIT")) {
				VampirismMod.log.w(TAG, "Could not write dynamic values to file");
			}
		}
		catch (IOException e) {
			VampirismMod.log.e(TAG, e, "Failed to write dynamic values to file");
		}
	}

	public static void onServerStarting(MinecraftServer server) {
		bloodValueGrinderWorldFile = new File(new File(server.getWorld(0).getSaveHandler().getWorldDirectory(), REFERENCE.MODID), "dynamic-blood-grinder-values.txt");
		if (bloodValueGrinderWorldFile.exists()) {
			loadDynamicBloodValues(bloodValueGrinderWorldFile);
		}
	}

	public static void onServerStopping() {
		if (bloodValueGrinderWorldFile != null) {
			saveDynamicBloodValues(bloodValueGrinderWorldFile);
			VampirismEntityRegistry.getBiteableEntryManager().resetDynamic();
		} else {
			VampirismMod.log.w(TAG, "Can't save blood grinder values. File does not exist");
		}

	}
}
