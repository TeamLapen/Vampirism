package de.teamlapen.vampirism.world.gen.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

public abstract class VampirismBiome extends Biome {
    public VampirismBiome(Builder builder, boolean enabled, int weight, BiomeManager.BiomeType type, BiomeDictionary.Type... types) {
        super(builder);
        BiomeDictionary.addTypes(this, types);
        if (enabled) {
            BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(this, weight));
        }
    }
}
