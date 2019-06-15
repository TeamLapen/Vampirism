package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.biome.BiomeGenVampireForest;
import de.teamlapen.vampirism.biome.VampirismBiome;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {

    private static final Logger LOGGER = LogManager.getLogger(ModBiomes.class);
    public static BiomeGenVampireForest vampireForest;



    static void registerBiomes(IForgeRegistry<Biome> registry) {

        vampireForest = new BiomeGenVampireForest();
        vampireForest.setRegistryName(REFERENCE.MODID, "vampireforest");
        registry.register(vampireForest);
        BiomeDictionary.addTypes(vampireForest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!Configs.disable_vampireForest) {

            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampireForest, Balance.general.VAMPIRE_FOREST_WEIGHT));
            LOGGER.debug("Registered vampire forest with weight %d", Balance.general.VAMPIRE_FOREST_WEIGHT);
            VampirismAPI.sundamageRegistry().addNoSundamageBiome(ModBiomes.vampireForest.getClass());

        }

    }

    static void registerFeatures() {
        if (Configs.disable_all_worldgen) return;
        if (!Configs.disable_hunter_camps) {
            for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                if (biome.getRegistryName().equals(new ResourceLocation("the_end")) || biome.getRegistryName().equals(new ResourceLocation("nether")))
                    continue;
                biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, VampirismBiome.HUNTER_TENT_FEATURE);
            }
        }
    }


}
