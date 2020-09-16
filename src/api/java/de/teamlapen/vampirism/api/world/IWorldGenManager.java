package de.teamlapen.vampirism.api.world;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public interface IWorldGenManager {

    /**
     * @param featureRegistryName
     * @param biomeRegistryKey
     * @param biomeCategory
     * @return Whether the given feature should be generated in the given biome id and category
     */
    boolean canStructureBeGeneratedInBiome(ResourceLocation featureRegistryName, ResourceLocation biomeRegistryKey, Biome.Category biomeCategory);

    /**
     * returns biome categories in which the named structure shouldn't be generated
     *
     * @param structure resourcelocation of the structure
     * @return set of biome categories to ignore
     */
    Set<Biome.Category> getIgnoredBiomeCategories(ResourceLocation structure);

    /**
     * returns biomes in which the named structure shouldn't be generated
     *
     * @param structure resourcelocation of the structure
     * @return set of biomes to ignore
     */
    Set<ResourceLocation> getIgnoredBiomes(ResourceLocation structure);

    /**
     * add a structure which should not be generated in the listed biome categories
     *
     * @param structure  resourcelocation of the structure
     * @param categories categories
     */
    void removeStructureFromBiomeCategories(ResourceLocation structure, List<Biome.Category> categories);

    /**
     * add a structure which should not be generated in the listed biomes
     *
     * @param structure resourcelocation of the structure
     * @param biomes    biome ids
     */
    void removeStructureFromBiomes(ResourceLocation structure, List<ResourceLocation> biomes);
}
