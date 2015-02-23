package de.teamlapen.vampirism;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.DefaultBoolean;
import de.teamlapen.vampirism.util.DefaultDouble;
import de.teamlapen.vampirism.util.DefaultInt;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class Configs {

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
		}
		loadConfiguration();
		Logger.i("Config", "Loaded configuration");
	}
	/**
	 * Loads/refreshes the configuration and adds comments if there aren't any
	 * {@link #init(File) init} has to be called once before using this
	 */
	private static void loadConfiguration() {
		// Categories
		ConfigCategory cat_balance = config.getCategory(CATEGORY_BALANCE);
		cat_balance.setComment("You can adjust these values to change the balancing of this mod");
		ConfigCategory cat_village = config.getCategory(CATEGORY_VILLAGE);
		cat_village.setComment("Here you can configure the village generation");
		ConfigCategory cat_balance_player_mod = config.getCategory(CATEGORY_BALANCE_PLAYER_MOD);
		cat_balance_player_mod.setComment("You can adjust these values to change the vampire player modifiers");
		ConfigCategory cat_balance_leveling = config.getCategory(CATEGORY_BALANCE_LEVELING);
		cat_balance_leveling.setComment("You can adjust these values to change the level up requirements");
		ConfigCategory cat_balance_mobprop = config.getCategory(CATEGORY_BALANCE_MOBPROP);
		cat_balance_leveling.setComment("You can adjust the properties of the added mobs");

		//General
		String conf_version=config.get(CATEGORY_GENERAL, "config_mod_version", REFERENCE.VERSION).getString();
		// Village
		village_gen_enabled = config.get(cat_village.getQualifiedName(), "enabled", true,
				"Should the custom generator be injected? (Enables/Disables the village mod)").getBoolean();
		village_density = config.get(cat_village.getQualifiedName(), "density", 15,
				"Minecraft will try to generate 1 village per NxN chunk area. Vanilla: 32").getInt();
		village_minDist = config.get(cat_village.getQualifiedName(), "minimumDistance", 4,
				"Village centers will be at least N chunks apart. Must be smaller than density. Vanilla: 8").getInt();
		village_size = config.get(cat_village.getQualifiedName(), "size", 0, "A higher size increases the overall spawn weight of buildings.")
				.getInt();

		if (village_minDist < 0) {
			Logger.e("VillageDensity", "Invalid config: Minimal distance must be non-negative.");
			village_gen_enabled = false;
		}
		if (village_minDist >= village_density) {
			Logger.e("VillageDensity", "Invalid config: Minimal distance must be smaller than density.");
			village_gen_enabled = false;
		}
		if (village_size < 0) {
			village_gen_enabled = false;
			Logger.e("VillageDensity", "Invalid config: Size must be non-negative.");
		}

		// Balance
		loadFields(cat_balance, BALANCE.class);
		loadFields(cat_balance_player_mod, BALANCE.VP_MODIFIERS.class);
		loadFields(cat_balance_leveling, BALANCE.LEVELING.class);
		loadFields(cat_balance_mobprop, BALANCE.MOBPROP.class);
		

		if (config.hasChanged()) {
			config.save();
		}
		if(!conf_version.equals(REFERENCE.VERSION)){
			Logger.i("Config", "Resetting config to default because a update was found");
			setConfigToDefault();
		}
		
		
	}
	/**
	 * This methods makes variables from a class available in the config file.
	 * To use this, the given class can only contain static non-final variables,
	 * which should have a '@Default*' annotation containing the default value.
	 * Currently only boolean,int and double are supported
	 * 
	 * @param cat
	 *            Config Category to put the properties inside
	 * @param cls
	 *            Class to go through
	 * @author Maxanier
	 */
	private static void loadFields(ConfigCategory cat, Class cls) {
		for (Field f : cls.getDeclaredFields()) {
			String name = f.getName();
			Class type = f.getType();
			try {
				if (type == int.class) {
					DefaultInt a = f.getAnnotation(DefaultInt.class); // Possible
																					// exception
																					// should
																					// not
																					// be
																					// caught
																					// so
																					// you
																					// cant
																					// forget
																					// a
																					// default
																					// value
					f.set(null, config.get(cat.getQualifiedName(), name, a.value(), a.comment()).getInt());
				} else if (type == double.class) {
					DefaultDouble a = f.getAnnotation(DefaultDouble.class); // Possible
																							// exception
																							// should
																							// not
																							// be
																							// caught
																							// so
																							// you
																							// cant
																							// forget
																							// a
																							// default
																							// value
					f.set(null, config.get(cat.getQualifiedName(), name, a.value(), a.comment()).getDouble());
				} else if (type == boolean.class) {
					DefaultBoolean a = f.getAnnotation(DefaultBoolean.class); // Possible
																								// exception
																								// should
																								// not
																								// be
																								// caught
																								// so
																								// you
																								// cant
																								// forget
																								// a
																								// default
																								// value
					f.set(null, config.get(cat.getQualifiedName(), name, a.value(), a.comment()).getBoolean());
				}
			} catch (NullPointerException e1) {
				Logger.e("Configs", "Author probably forgot to specify a default value for " + name + " in " + cls.getCanonicalName(), e1);
				throw new Error("Please check you default values");
			} catch (Exception e) {
				Logger.e("Configs", "Cant set " + cls.getName() + " values", e);
				throw new Error("Please check your vampirism config file");
			}
		}
	}
	
	public static void setConfigToDefault(){
		for(String cat:config.getCategoryNames()){
			for(Entry<String, Property> e:config.getCategory(cat).entrySet()){
				e.getValue().setToDefault();
			}
		}
		Logger.i("Config", "Reset config to default");
		config.save();
	}
	
	private static void handleModUpdated(){
		
	}
	public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
	public static final String CATEGORY_VILLAGE = "village_settings";
	public static final String CATEGORY_BALANCE = "balance";

	public static final String CATEGORY_BALANCE_PLAYER_MOD = "balance_player_mod";
	public static final String CATEGORY_BALANCE_LEVELING = "balance_leveling";
	public static final String CATEGORY_BALANCE_MOBPROP = "balance_mob_properties";
	public static boolean village_gen_enabled;

	public static int village_density;

	public static int village_minDist;

	public static int village_size;

	public static Configuration config;

	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.modID.equalsIgnoreCase(REFERENCE.MODID)) {
			// Resync configs
			Logger.i("Configs", "Configuration has changed");
			Configs.loadConfiguration();
		}
	}

}
