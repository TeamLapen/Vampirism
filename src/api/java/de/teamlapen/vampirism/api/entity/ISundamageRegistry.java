package de.teamlapen.vampirism.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

/**
 * Manages sundamage for biomes and dimensions
 */
public interface ISundamageRegistry {


    /**
     * Register a biome by instance in which no sundamage applies to vampires.
     *
     * @deprecated use datapacks or {@link #addNoSundamageBiomes(ResourceKey[])} instead
     */
    @Deprecated
    void addNoSundamageBiomes(ResourceLocation... biomes);

    /**
     * Register a biome by instance in which no sundamage applies to vampires.
     * <p>
     * Use this or a datapack.
     */
    void addNoSundamageBiomes(ResourceKey<Biome>... biomes);

    /**
     * Specifies if vampires should get sundamage in this dimension.
     * <p>
     * use this or a datapack.
     */
    void specifySundamageForDim(ResourceKey<Level> dimension, boolean sundamage);

    /**
     * Register a dimension as one in which no sundamage applies to vampires.
     *
     * @param dimensionType The resource key of the dimension type
     */
    void addNoSundamageDimensionType(ResourceKey<DimensionType> dimensionType);

    /**
     * Check if vampires can get sundamage in that biome
     *
     * @return Whether vampires can get sundamage in that biome
     * @deprecated use {@link #hasBiomeSundamage(net.minecraft.resources.ResourceKey)} )} instead
     */
    @Deprecated
    boolean getSundamageInBiome(ResourceLocation registryName);

    /**
     * Check if vampires can get sundamage in that biome
     *
     * @param biome the resource key of the biome
     * @return Whether vampires can get sundamage in that biome
     */
    boolean hasBiomeSundamage(ResourceKey<Biome> biome);

    /**
     * Check if vampires can get sundamage in that dimension type
     *
     * @param dimensionType the resource key of the dimension type
     * @return Whether vampires can get sundamage in that dimension
     */
    boolean hasDimensionTypeSundamage(ResourceKey<DimensionType> dimensionType);

    /**
     * Check if vampires can get sundamage in that dimension
     *
     * @param dim the resource key of the dimension
     * @return Whether vampires can get sundamage in that dimension
     */
    boolean getSundamageInDim(ResourceKey<Level> dim);

    /**
     * Checks if the given entity could receive sun damage at its current position.
     * Do not use entity.getEntityWorld during world gen
     */
    boolean isGettingSundamage(LivingEntity entity, LevelAccessor world);

    /**
     * Checks if the level at the given position does apply sundamage
     *
     * @param pos the position is used to find the biome in the level
     */
    boolean hasSunDamage(@NotNull LevelAccessor levelAccessor, @NotNull BlockPos pos);

}
