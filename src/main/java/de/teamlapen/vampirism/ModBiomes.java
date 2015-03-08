package de.teamlapen.vampirism;

import de.teamlapen.vampirism.biome.BiomeVampireForest;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;

public class ModBiomes {
	public static BiomeGenBase biomeVampireForest;
	
	public static void init() {
		int biomeID = 100;
		while (BiomeDictionary.isBiomeRegistered(biomeID))
			biomeID++;
		biomeVampireForest = new BiomeVampireForest(biomeID).setBiomeName(BiomeVampireForest.name);
		BiomeDictionary.registerBiomeType(biomeVampireForest, Type.FOREST, Type.DENSE, Type.MAGICAL, Type.SPOOKY);
		BiomeManager.addSpawnBiome(biomeVampireForest);
	}
	
	
}
