package de.teamlapen.vampirism.api.datamaps;

/**
 * Blood and converter extension for entities.
 * <br>
 * <br>
 * This interface is used as <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamap</a> entry for {@link de.teamlapen.vampirism.api.VampirismRegistries#ENTITY_BLOOD_MAP}
 */
public interface IEntityBlood {

    /**
     * @return The amount of blood this entity has
     */
    int blood();
}
