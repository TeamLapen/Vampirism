package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * all actions must extend {@link DefaultEntityAction}
 */
public interface IEntityAction extends IForgeRegistryEntry<IEntityAction> {
    /**
     * @return cooldown after completing the action before another action can be used in ticks
     */
    int getCooldown(int level);

    /**
     * @return activation time before the action is activated, but ready to start
     */
    int getPreActivationTime();

    /**
     * @returns actions minimum {@link EntityActionTier} for usage
     */
    public EntityActionTier getTier();

    /**
     * @returns needed {@link EntityClassType} for usage
     */
    public EntityClassType[] getClassTypes();

    /**
     * @returns needed {@link IPlayableFaction} for usage
     */
    public IPlayableFaction getFaction();
}
