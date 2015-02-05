package de.teamlapen.vampirism.generation.villages;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import de.teamlapen.vampirism.util.Logger;

import java.io.File;
import java.util.regex.Pattern;

/**
 * All the initialization for new Village Biomes
 * @author WILLIAM
 *
 */
public class VillageBiomes {

	public static void preInit(FMLPreInitializationEvent event) {
        // Load Config
		File ConfigFile = new File(event.getModConfigurationDirectory(), "VillageBiomes.cfg");
        ConfigHandler.loadConfig(ConfigFile);
    }

	// TODO: add to vampirism.java
    public static void postInit(FMLPostInitializationEvent ev) {
        // All other mods should be done registering by now.
        BiomeRegistrant.init();

        for (String name : ConfigHandler.getAddBiomes()) {
            if (Pattern.matches("\\d+", name))
                BiomeRegistrant.addBiomeById(Integer.parseInt(name));
            else
                BiomeRegistrant.addBiomeByName(name);
        }
        for (String name : ConfigHandler.getAddTypes()) {
        	Logger.i("VillageBiomes", String.format("Adding all %s biomes as village biomes.", name));
            BiomeRegistrant.addBiomesByTypeName(name);
        }

        for (String name : ConfigHandler.getRemoveBiomes()) {
            if (Pattern.matches("\\d+", name))
                BiomeRegistrant.removeBiomeById(Integer.parseInt(name));
            else
                BiomeRegistrant.removeBiomeByName(name);
        }
        for (String name : ConfigHandler.getRemoveTypes()) {
        	Logger.i("VillageBiomes", "Removing all " + name + " biomes from village biomes.");
            BiomeRegistrant.removeBiomesByTypeName(name);
        }

        // Register the custom village block replacer
        MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeBlockReplacer());
    }
}
