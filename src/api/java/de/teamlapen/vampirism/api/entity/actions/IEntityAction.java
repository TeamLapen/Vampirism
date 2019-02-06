package de.teamlapen.vampirism.api.entity.actions;

import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IEntityAction extends IForgeRegistryEntry<IEntityAction> {
    
    /**
     * @return cooldown after completing the action before another action can be used in ticks
     */
    int getCooldown(int level);

    /**
     * @return activation time before the action is activated, but ready to start
     */
    int getPreActivationTime();
}
