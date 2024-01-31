package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.extensions.IEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Debug;

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
    @ApiStatus.Obsolete
    LivingEntity getRepresentingEntity();

    @Override
    default Entity asEntity() {
        return getRepresentingEntity();
    }
}
