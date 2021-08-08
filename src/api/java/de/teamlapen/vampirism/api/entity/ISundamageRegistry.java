package de.teamlapen.vampirism.api.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

/**
 * Manages sundamage for biomes and dimensions
 */
public interface ISundamageRegistry {


    /**
     * Register a biome by instance in which no sundamage applies to vampires.*
     *
     * @param biomes
     */
    void addNoSundamageBiomes(ResourceLocation... biomes);

    /**
     * Checkd if vampirs can get sundamage in that biome
     *
     * @return Whether vampires can get sundamage in that biome
     */
    boolean getSundamageInBiome(ResourceLocation registryName);


    /**
     * Checks if vampires can get sundamge in that dimension
     *
     * @param dim
     * @return
     */
    boolean getSundamageInDim(ResourceKey<Level> dim);

    /**
     * Checks if the given entity could receive sun damage at its current position.
     * Do not use entity.getEntityWorld during world gen
     */
    boolean isGettingSundamage(LivingEntity entity, LevelAccessor world);

    /**
     * Specifies if vampires should get sundamage in this dimension
     *
     * @param dimension
     * @param sundamage
     */
    void specifySundamageForDim(ResourceKey<Level> dimension, boolean sundamage);
}
