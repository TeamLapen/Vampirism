package de.teamlapen.vampirism.api.entity;

import net.minecraft.util.ResourceLocation;

/**
 * Manages sundamage for biomes and dimensions
 */
public interface ISundamageRegistry {
    void addNoSundamageBiome(ResourceLocation registryName);

    /**
     * Checkd if vampirs can get sundamage in that biome
     *
     * @return
     */
    boolean getSundamageInBiome(ResourceLocation registryName);

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
