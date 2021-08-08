package de.teamlapen.vampirism.world;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.world.IWorldGenManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
public class WorldGenManager implements IWorldGenManager {
    /**
     * stores structures {@link ResourceLocation} -> set of {@link Biome}s in which the structure shouldn't be generated
     */
    private @Nonnull
    final Map<ResourceLocation, Set<ResourceLocation>> ignoreStructureBiome = Maps.newHashMap();
    /**
     * stores structures {@link ResourceLocation} -> set of {@link Biome.Category}s in which the structure shouldn't be generated
     */
    private @Nonnull
    final Map<ResourceLocation, Set<Biome.BiomeCategory>> ignoreStructureBiomeCategory = Maps.newHashMap();

    @Override
    public boolean canStructureBeGeneratedInBiome(ResourceLocation featureRegistryName, ResourceLocation biomeRegistryKey, Biome.BiomeCategory biomeCategory) {
        if (ignoreStructureBiome.containsKey(featureRegistryName)) {
            if (ignoreStructureBiome.get(featureRegistryName).contains(biomeRegistryKey)) return false;
        }
        if (ignoreStructureBiomeCategory.containsKey(featureRegistryName)) {
            Set<Biome.BiomeCategory> categories = ignoreStructureBiomeCategory.get(featureRegistryName);
            return !categories.contains(biomeCategory);
        }
        return true;
    }

    @Override
    public Set<Biome.BiomeCategory> getIgnoredBiomeCategories(ResourceLocation structure) {
        if (!ignoreStructureBiomeCategory.containsKey(structure)) return Sets.newHashSet();
        return ImmutableSet.copyOf(ignoreStructureBiomeCategory.get(structure));
    }

    @Override
    public Set<ResourceLocation> getIgnoredBiomes(ResourceLocation structure) {
        if (!ignoreStructureBiome.containsKey(structure)) return Sets.newHashSet();
        return ImmutableSet.copyOf(ignoreStructureBiome.get(structure));
    }

    @Override
    public void removeStructureFromBiomeCategories(ResourceLocation structure, List<Biome.BiomeCategory> categories) {
        this.ignoreStructureBiomeCategory.computeIfAbsent(structure, name -> Sets.newHashSet()).addAll(categories);
    }

    @Override
    public void removeStructureFromBiomes(ResourceLocation structure, List<ResourceLocation> biomes) {
        this.ignoreStructureBiome.computeIfAbsent(structure, name -> Sets.newHashSet()).addAll(biomes);
    }
}
