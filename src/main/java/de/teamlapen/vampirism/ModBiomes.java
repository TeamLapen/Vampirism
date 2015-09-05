package de.teamlapen.vampirism;

import de.teamlapen.vampirism.biome.BiomeVampireForest;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

/**
 * 
 * @author WILLIAM
 *
 */
public class ModBiomes {
	public static BiomeGenBase biomeVampireForest;
	public static BiomeEntry biomeEntryVampireForest;

	public static void init() {
		int biomeID = Configs.getVampireBiomeId();
		if (biomeID == -1) {
			biomeID = 10;
			while (BiomeDictionary.isBiomeRegistered(biomeID))
				biomeID++;

			Configs.setVampireBiomeId(biomeID);
		}

		biomeVampireForest = new BiomeVampireForest(biomeID).setBiomeName(BiomeVampireForest.name).setHeight(new BiomeGenBase.Height(0.1F,0.025F));

		// like swamp
		biomeVampireForest.setColor(0xCC00CC);

		BiomeDictionary.registerBiomeType(biomeVampireForest, Type.FOREST, Type.DENSE, Type.MAGICAL, Type.SPOOKY);
		int weight = 10;
		// int weight = 50; // Testing only
		Logger.i("ModBiomes", "VampireForest created with id " + biomeID + " and weight: " + weight);
		biomeEntryVampireForest = new BiomeEntry(biomeVampireForest, weight); // Change weight to 100 to see more of these
		if (!Configs.disable_vampire_biome) {
			BiomeManager.addBiome(BiomeType.WARM, biomeEntryVampireForest);
		}
	}
}
