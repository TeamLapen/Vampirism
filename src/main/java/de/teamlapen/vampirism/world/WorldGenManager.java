package de.teamlapen.vampirism.world;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;

import de.teamlapen.vampirism.api.world.IWorldGenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

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
    final Map<ResourceLocation, Set<Biome>> ignoreStructureBiome = Maps.newHashMap();
    /**
     * stores structures {@link ResourceLocation} -> set of {@link Biome.Category}s in which the structure shouldn't be generated
     */
    private @Nonnull
    final Map<ResourceLocation, Set<BiomeDictionary.Type>> ignoreStructureBiomeCategory = Maps.newHashMap();

    @Override
    public void removeStructureFromBiomes(ResourceLocation structure, List<Biome> biomes) {
        this.ignoreStructureBiome.computeIfAbsent(structure, name -> Sets.newHashSet()).addAll(biomes);
    }

    @Override
    public void removeStructureFromBiomeCategories(ResourceLocation structure, List<BiomeDictionary.Type> categories) {
        this.ignoreStructureBiomeCategory.computeIfAbsent(structure, name -> Sets.newHashSet()).addAll(categories);
    }

    @Override
    public Set<BiomeDictionary.Type> getIgnoredBiomeCategories(ResourceLocation structure) {
        if (!ignoreStructureBiomeCategory.containsKey(structure)) return Sets.newHashSet();
        return ImmutableSet.copyOf(ignoreStructureBiomeCategory.get(structure));
    }

    @Override
    public Set<Biome> getIgnoredBiomes(ResourceLocation structure) {
        if (!ignoreStructureBiome.containsKey(structure)) return Sets.newHashSet();
        return ImmutableSet.copyOf(ignoreStructureBiome.get(structure));
    }

    @Override
    public boolean canStructureBeGeneratedInBiome(ResourceLocation structure, Biome biome) {
        if (ignoreStructureBiome.containsKey(structure)) {
            if (ignoreStructureBiome.get(structure).contains(biome)) return false;
        }
        if (ignoreStructureBiomeCategory.containsKey(structure)) {
            if (ignoreStructureBiomeCategory.get(structure).containsAll(BiomeDictionary.getTypes(biome))) return false;
        }
        return true;
    }
}
