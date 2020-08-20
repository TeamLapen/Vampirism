package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.world.IFactionBiome;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.gen.features.VampirismBiomeFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
/**
 * Handles all biome registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModBiomes {
    public static final VampireForestBiome vampire_forest = getNull();
    public static final VampireForestBiome vampire_forest_hills = getNull();


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        registry.register(new VampireForestBiome());
        registry.register(new VampireForestBiome("vampire_forest_hills", builder -> builder.depth(0.8f).scale(0.5f)));
    }

    static void addBiome() {
        BiomeDictionary.addTypes(vampire_forest, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        BiomeDictionary.addTypes(vampire_forest_hills, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.HILLS);
        if (!VampirismConfig.SERVER.disableVampireForestBiomes.get()) {
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampire_forest, VampirismConfig.BALANCE.vampireForestWeight.get() / 2));
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampire_forest_hills, VampirismConfig.BALANCE.vampireForestHillsWeight.get() / 2));
        }
    }

    static void addFeatures() {
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            VampirismBiomeFeatures.addVampireDungeon(biome);
            if (!VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), biome))
                continue;
            if (biome instanceof IFactionBiome && !VReference.HUNTER_FACTION.equals(((IFactionBiome) biome).getFaction()))
                continue;
            VampirismBiomeFeatures.addHunterTent(biome);
        }
    }
}
