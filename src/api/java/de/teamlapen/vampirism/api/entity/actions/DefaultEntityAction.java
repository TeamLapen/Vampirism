package de.teamlapen.vampirism.api.entity.actions;

import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DefaultEntityAction extends IForgeRegistryEntry.Impl<IEntityAction> implements IEntityAction {

    @Override
    public int getPreActivationTime() {
        return 10; //0.5 sec
    }
}
