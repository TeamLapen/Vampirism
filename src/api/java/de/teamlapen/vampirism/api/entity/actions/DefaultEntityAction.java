package de.teamlapen.vampirism.api.entity.actions;

import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DefaultEntityAction extends IForgeRegistryEntry.Impl<IEntityAction> implements IEntityAction {

    @Override
    public int getDuration(int level) {
        return getCooldown(level);
    }
}
