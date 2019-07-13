package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.gen.biome.VampirismBiomeFeatures;
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
    public static VampireForestBiome vampireForest;



    static void registerBiomes(IForgeRegistry<Biome> registry) {

        vampireForest = new VampireForestBiome();
        vampireForest.setRegistryName(REFERENCE.MODID, "vampireforest");
        registry.register(vampireForest);
        BiomeDictionary.addTypes(vampireForest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!VampirismConfig.SERVER.disableVampireForest.get()) {

            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampireForest, Balance.general.VAMPIRE_FOREST_WEIGHT));
            LOGGER.debug("Registered vampire forest with weight {}", Balance.general.VAMPIRE_FOREST_WEIGHT);
            VampirismAPI.sundamageRegistry().addNoSundamageBiome(ModBiomes.vampireForest.getClass());
        }

    }

    static void registerFeatures() {
        if (!VampirismConfig.SERVER.disableHunterCamps.get()) {
            for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                if (new ResourceLocation("the_end").equals(biome.getRegistryName()) || new ResourceLocation("nether").equals(biome.getRegistryName()))
                    continue;
                biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, VampirismBiomeFeatures.HUNTER_TENT_FEATURE);
            }
        }
    }


}
