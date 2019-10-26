package de.teamlapen.vampirism.api.world;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public interface IWorldGenManager {

    /**
     * add a structure which should not be generated in the listed biomes
     *
     * @param structure resourcelocation of the structure
     * @param biomes    biomes
     */
    void removeStructureFromBiomes(ResourceLocation structure, List<Biome> biomes);

    /**
     * returns biomes in which the named structure shouldn't be generated
     *
     * @param structure resourcelocation of the structure
     * @return set of biomes to ignore
     */
    Set<Biome> getIgnoredBiomes(ResourceLocation structure);

    /**
     * add a structure which should not be generated in the listed biome categories
     *
     * @param structure  resourcelocation of the structure
     * @param categories categories
     */
    void removeStructureFromBiomeCategories(ResourceLocation structure, List<BiomeDictionary.Type> categories);

    /**
     * returns biome categories in which the named structure shouldn't be generated
     *
     * @param structure resourcelocation of the structure
     * @return set of biome categories to ignore
     */
    Set<BiomeDictionary.Type> getIgnoredBiomeCategories(ResourceLocation structure);

    /**
     * @return if the given structure can be generated in the given biome
     */
    boolean canStructureBeGeneratedInBiome(ResourceLocation structure, Biome biome);
}
