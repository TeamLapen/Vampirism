package de.teamlapen.vampirism.api.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface IWorldGenConfiguration {

    /**
     * @return Whether the given feature should be generated in the given biome id and category
     */
    boolean canStructureBeGeneratedInBiome(ResourceLocation featureRegistryName, ResourceLocation biomeRegistryKey, Biome.BiomeCategory biomeCategory);

    /**
     * returns biome categories in which the named structure shouldn't be generated
     *
     * @param structure location of the structure
     * @return set of biome categories to ignore
     */
    Set<Biome.BiomeCategory> getIgnoredBiomeCategories(ResourceLocation structure);

    /**
     * returns biomes in which the named structure shouldn't be generated
     *
     * @param structure location of the structure
     * @return set of biomes to ignore
     */
    Set<ResourceLocation> getIgnoredBiomes(ResourceLocation structure);

    /**
     * add a structure which should not be generated in the listed biome categories
     *
     * @param structure  location of the structure
     * @param categories categories
     */
    void removeStructureFromBiomeCategories(ResourceLocation structure, List<Biome.BiomeCategory> categories);

    /**
     * add a structure which should not be generated in the listed biomes
     *
     * @param structure location of the structure
     * @param biomes    biome ids
     */
    void removeStructureFromBiomes(ResourceLocation structure, List<ResourceLocation> biomes);
}
