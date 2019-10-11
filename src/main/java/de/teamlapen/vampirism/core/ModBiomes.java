package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;

import de.teamlapen.vampirism.api.world.VampireBiome;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.gen.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.gen.biome.VampirismBiomeFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampire_forest, VampirismConfig.BALANCE.vampireForestWeight.get()));
        }
    }

    static void addFeatures() {
        List nonHunterCampBiomeCategories = Lists.newArrayList(Biome.Category.OCEAN, Biome.Category.THEEND, Biome.Category.NETHER, Biome.Category.ICY, Biome.Category.BEACH, Biome.Category.RIVER);
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            if (nonHunterCampBiomeCategories.contains(biome.getCategory())) continue;
            if (biome instanceof VampireBiome) continue;
            VampirismBiomeFeatures.addHunterTent(biome);
        }
    }


}
