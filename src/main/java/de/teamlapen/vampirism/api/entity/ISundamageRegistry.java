package de.teamlapen.vampirism.api.entity;

/**
 * Manages sundamage for biomes and dimensions
 */
public interface ISundamageRegistry {
    void addNoSundamageBiome(int id);

    /**
     * Checkd if vampirs can get sundamage in that biome
     *
     * @param id
     * @return
     */
    boolean getSundamageInBiome(int id);

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
