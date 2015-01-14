package de.teamlapen.vampirism.villages;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.teamlapen.vampirism.util.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * VillageDensity class that reads the settings from the configuration file, and are
 *  used by the VillageReplacer class
 * @author WILLIAM
 *
 */
public class VillageDensity {
    // Settings
    public static Property enabled, density, minDist, size;

	public static void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        final String cat = Configuration.CATEGORY_GENERAL;

        config.load();
        enabled = config.get(cat, "enabled", false, "Should the custom generator be injected? (Enables/Disables the mod)");
        density = config.get(cat, "density", 32, "Minecraft will try to generate 1 village per NxN chunk area. \nDefault: 32");
        minDist = config.get(cat, "minimumDistance", 8, "Village centers will be at least N chunks apart. Must be smaller than density. \nDefault: 8");
        size = config.get(cat, "size", 0, "A higher size increases the overall spawn weight of buildings. (Don't ask, I have no idea) \nDefault: 0");
        config.save();

        if (minDist.getInt() < 0) {
        	Logger.e("VillageDensity", "Invalid config: Minimal distance must be non-negative.");
            enabled.set(false);
        }
        if (minDist.getInt() >= density.getInt()) {
        	Logger.e("VillageDensity", "Invalid config: Minimal distance must be smaller than density.");
            enabled.set(false);
        }
        if (size.getInt() < 0) {
            enabled.set(false);
            Logger.e("VillageDensity", "Invalid config: Size must be non-negative.");
        }		
	}
	
    public static void init() {
        if(!enabled.getBoolean(false)) return;

        Logger.i("VillageDensity", "Registering replacer for village generation.");
        MinecraftForge.TERRAIN_GEN_BUS.register(new VillageGenReplacer());
    }
}
