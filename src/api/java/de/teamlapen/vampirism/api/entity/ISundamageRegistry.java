package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nonnull;

/**
 * Manages sundamage for biomes and dimensions
 */
public interface ISundamageRegistry {

    /**
     * Register a biome by id in which no sundamage applies to vampires
     */
    void addNoSundamageBiome(ResourceLocation registryName);

    /**
     * Register a biome by class in which no sundamage applies to vampires
     * Also affects subclasses
     *
     * @param clazz Biome class or interface
     */
    void addNoSundamageBiome(Class clazz);

    /**
     * Register a biome by instance in which no sundamage applies to vampires.
     * Also effects subclasses of given biomes
     *
     * @param biomes
     */
    void addNoSundamageBiomes(Biome... biomes);

    /**
     * Checkd if vampirs can get sundamage in that biome
     *
     * @return
     */
    @Deprecated
    boolean getSundamageInBiome(ResourceLocation registryName);

    /**
     * @return If sundamage applies to vampires in this biome
     */
    boolean getSundamageInBiome(@Nonnull Biome biome);

    /**
     * Checks if vampires can get sundamge in that dimension
     *
     * @param dim
     * @return
     */
    boolean getSundamageInDim(DimensionType dim);

    /**
     * Checks if the given entity could receive sun damage at its current position
     */
    boolean isGettingSundamage(EntityLivingBase entity);

    /**
     * Specifies if vampires should get sundamage in this dimension
     *
     * @param dimension
     * @param sundamage
     */
    void specifySundamageForDim(DimensionType dimension, boolean sundamage);
}
