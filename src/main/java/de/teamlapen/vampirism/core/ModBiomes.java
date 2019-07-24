package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.gen.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.gen.biome.VampirismBiomeFeatures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {

    private static final Logger LOGGER = LogManager.getLogger(ModBiomes.class);
    @ObjectHolder("vampirism:vampire_forest")
    public static VampireForestBiome vampire_forest;



    static void registerBiomes(IForgeRegistry<Biome> registry) {
        registry.register(new VampireForestBiome());
    }

    static void addBiome() {
        BiomeDictionary.addTypes(vampire_forest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!VampirismConfig.COMMON.disableVampireForest.get()) {
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampire_forest, Balance.general.VAMPIRE_FOREST_WEIGHT));
        }
    }

    static void registerFeatures() {
            for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                if (new ResourceLocation("the_end").equals(biome.getRegistryName()) || new ResourceLocation("nether").equals(biome.getRegistryName()))
                    continue;
                VampirismBiomeFeatures.addHunterTent(biome);
            }
    }


}
