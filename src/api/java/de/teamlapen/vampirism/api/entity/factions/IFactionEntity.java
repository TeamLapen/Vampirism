package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.entity.EntityLivingBase;

/**
 * Should be implemented (through the subclasses) by any faction entity
 */
public interface IFactionEntity {
    /**
     * Return the faction this entity belongs to
     *
     * @return
     */
    IFaction getFaction();

    /**
     * Can be the same object or for Player Capabilities the player object
     *
     * @return The EntityLivingBase represented by this object.
     */
    EntityLivingBase getRepresentingEntity();
}
