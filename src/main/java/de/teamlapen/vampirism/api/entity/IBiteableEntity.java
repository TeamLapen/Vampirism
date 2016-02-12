package de.teamlapen.vampirism.api.entity;

/**
 * Implement this in any EntityLivingBase if you want to something special regarding bites.
 * Vampirism will call this instead of the normal I
 */
public interface IBiteableEntity {


    /**
     * @param biter The biting entity
     * @return Amount of blood that should be added
     */
    int onBite(IVampire biter);

    /**
     * @param biter The biting entity
     * @return If the entity currently can be bitten
     */
    boolean canBeBitten(IVampire biter);

    /**
     * 1.0 Should be a default value
     *
     * @return Saturation modifier of this entities blood
     */
    float getBloodSaturation();
}
