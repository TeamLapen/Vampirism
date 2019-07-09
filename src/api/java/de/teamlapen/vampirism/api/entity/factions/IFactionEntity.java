package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.entity.LivingEntity;

/**
 * Should be implemented (through the subclasses) by any faction entity
 */
public interface IFactionEntity {
    /**
     * @return the faction this entity belongs to
     */
    IFaction getFaction();

    /**
     * Can be the same object or for Player Capabilities the player object
     *
     * @return The EntityLivingBase represented by this object.
     */
    LivingEntity getRepresentingEntity();
}
