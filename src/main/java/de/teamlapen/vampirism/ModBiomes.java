package de.teamlapen.vampirism;

import de.teamlapen.vampirism.biome.BiomeVampireForest;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class ModBiomes {
	public static BiomeGenBase biomeVampireForest;
	public static BiomeEntry biomeEntryVampireForest;
	
	public static void init() {
		int biomeID = 10;
		while (BiomeDictionary.isBiomeRegistered(biomeID))
			biomeID++;
		biomeVampireForest = new BiomeVampireForest(biomeID).setBiomeName(BiomeVampireForest.name);

		// like swamp
		biomeVampireForest.setColor(0xCC00CC);
		
		BiomeDictionary.registerBiomeType(biomeVampireForest, Type.FOREST, Type.DENSE, Type.MAGICAL, Type.SPOOKY);
		int weight = 10;
//		int weight = 50;  // Testing only		
		Logger.i("ModBiomes", "VampireForest created with weight: " + weight);
		biomeEntryVampireForest = new BiomeEntry(biomeVampireForest, weight); // Change weight to 100 to see more of these
		BiomeManager.addBiome(BiomeType.WARM, biomeEntryVampireForest);
	}	
}
