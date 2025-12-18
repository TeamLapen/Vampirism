package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.world.entities.extensions.IEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

/**
 * Should be implemented (through the subclasses) by any faction entity
 */
public interface IFactionEntity extends IEntity {
    /**
     * @return the faction this entity belongs to
     */
    Holder<? extends IFaction<?>> getFaction();

    /**
     * Can be the same object or for Player Capabilities the player object
     *
     * @return The EntityLivingBase represented by this object.
     */
    @Override
    LivingEntity asEntity();
}
