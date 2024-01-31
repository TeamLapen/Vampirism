package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.extensions.IEntity;

/**
 * Implement this in any EntityLivingBase if you want to something special regarding bites.
 * Vampirism will call this instead of the normal I
 */
public interface IBiteableEntity extends IEntity {


    /**
     * Called when a vampire tries to suck blood (not infect)
     *
     * @param biter The biting entity
     * @return If the entity currently can be bitten
     */
    boolean canBeBitten(IVampire biter);

    /**
     * Returns 1.0F or currentBlood/maximumBlood if applicable and implemented
     *
     * @return currentBlood/maximumBlood
     */
    default float getBloodLevelRelative() {
        return 1.0F;
    }

    /**
     * 1.0 Should be a default value
     *
     * @return Saturation modifier of this entities blood
     */
    default float getBloodSaturation() {
        return 1;
    }

    /**
     * @param biter The biting entity
     * @return Amount of blood that should be added
     */
    int onBite(IVampire biter);

    /**
     * @return Whether this creatures can be turned in general and whether the given vampire is able to do so
     */
    default boolean canBeInfected(IVampire vampire) {
        return false;
    }

    /**
     * Try to infect/convert this entity. This method initially checks {@link #canBeInfected(IVampire)} so you do not need to do this beforehand.
     *
     * @param vampire The vampire trying to infect the entity
     * @return Whether the entity was successfully infected
     */
    default boolean tryInfect(IVampire vampire) {
        return false;
    }
}
