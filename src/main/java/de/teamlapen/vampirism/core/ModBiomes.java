package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.biome.BiomeGenVampireForest;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {

    public static BiomeGenVampireForest vampireForest;


    static void registerBiomes(IForgeRegistry<Biome> registry) {

        vampireForest = new BiomeGenVampireForest();
        vampireForest.setRegistryName(REFERENCE.MODID, "vampireForest");
        registry.register(vampireForest);
        BiomeDictionary.addTypes(vampireForest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!Configs.disable_vampireForest) {

            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampireForest, BiomeGenVampireForest.GEN_WEIGHT));
            VampirismMod.log.d("ModBiomes", "Registered vampire forest with weight %d", BiomeGenVampireForest.GEN_WEIGHT);
            VampirismAPI.sundamageRegistry().addNoSundamageBiome(ModBiomes.vampireForest.getBiomeClass());

        }

    }


}
