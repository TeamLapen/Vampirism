package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.entity.CreatureEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Actions that can be executed by some of vampirism's entities. Similar to the actions available to the player.
 * DON'T use this interface directly. Use either {@link IInstantAction} or {@link ILastingAction}
 */
public interface IEntityAction extends IForgeRegistryEntry<IEntityAction> {
    /**
     * @return needed {@link EntityClassType} for usage
     */
    EntityClassType[] getClassTypes();

    /**
     * @return cooldown after completing the action before another action can be used in ticks
     */
    int getCooldown(int level);

    /**
     * @return needed {@link IPlayableFaction} for usage
     */
    IPlayableFaction getFaction();

    /**
     * @return activation time in ticks before the action is activated once ready to start
     */
    default int getPreActivationTime() {
        return 10;
    }

    /**
     * @return actions minimum {@link EntityActionTier} for usage
     */
    @Nonnull
    EntityActionTier getTier();

    /**
     * The higher the weight, the more likely the action is chosen,
     * Can be varied depending on the given entities situtation.
     * Should be in the range 1-10, but could be higher
     *
     * @return weight of this action
     */
    default int getWeight(CreatureEntity entity) {
        return 1;
    }
}
