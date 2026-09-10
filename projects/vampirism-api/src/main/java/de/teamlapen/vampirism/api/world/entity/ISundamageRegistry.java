package de.teamlapen.vampirism.api.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

/**
 * Manages sundamage for biomes and dimensions
 */
@SuppressWarnings("unchecked")
public interface ISundamageRegistry {


    /**
     * Register a biome in which no sundamage applies to vampires.
     * <p>
     * Use this or {@link de.teamlapen.vampirism.api.VampirismTags.Biomes#HAS_NO_SUNDAMAGE}.
     */
    void addNoSundamageBiomes(ResourceKey<Biome>... biomes);

    /**
     * Register a dimension type in which no sundamage applies to vampires.
     * <p>
     * Use this or {@link de.teamlapen.vampirism.api.VampirismTags.DimensionTypes#HAS_NO_SUNDAMAGE}
     */
    void addNoSundamageDimensionTypes(ResourceKey<DimensionType>... dimensionTypes);

    /**
     * Register a dimension in which no sundamage applies to vampires.
     */
    void addNoSundamageDimension(ResourceKey<Level>... dimensionTypes);

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

    void reload();
}
