package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.extensions.IEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Should be implemented (through the subclasses) by any faction entity
 */
public interface IFactionEntity extends IEntity {
    /**
     * @return the faction this entity belongs to
     */
    @NotNull
    IFaction<?> getFaction();

    /**
     * Can be the same object or for Player Capabilities the player object
     *
     * @return The EntityLivingBase represented by this object.
     * @deprecated use {@link #asEntity()}
     */
    @Deprecated
    LivingEntity getRepresentingEntity();

    @Override
    default @NotNull LivingEntity asEntity() {
        return getRepresentingEntity();
    }
}
