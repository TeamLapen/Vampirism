package de.teamlapen.vampirism.api.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

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
    boolean getSundamageInDim(int dim);

    /**
     * Specifies if vampires should get sundamage in this dimension
     *
     * @param dimensionId
     * @param sundamage
     */
    void specifySundamageForDim(int dimensionId, boolean sundamage);
}
