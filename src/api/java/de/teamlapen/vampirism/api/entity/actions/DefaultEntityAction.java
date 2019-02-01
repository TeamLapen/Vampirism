package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.entity.action.EntityActionManager;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DefaultEntityAction extends IForgeRegistryEntry.Impl<IEntityAction> implements IEntityAction {

    public DefaultEntityAction() {
        EntityActionManager.ID++;
        EntityActionManager.actionsByID.put(EntityActionManager.ID, this);
        EntityActionManager.idByActions.put(this, EntityActionManager.ID);
    }
}
