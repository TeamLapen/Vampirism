package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DefaultEntityAction extends IForgeRegistryEntry.Impl<IEntityAction> implements IEntityAction {
    protected EntityClassType[] entityClassTypes;
    protected EntityActionTier entityActionTier;

    public DefaultEntityAction(EntityActionTier tier, EntityClassType... param) {
        entityActionTier = tier;
        entityClassTypes = param;
    }

    @Override
    public int getPreActivationTime() {
        return 10; //0.5 sec
    }
    
    /**
     * @returns the actions {@link EntityActionTier}
     */
    public EntityActionTier getTier() {
        return entityActionTier;
    }

    /**
     * @returns the actions {@link EntityClassType}
     */
    public EntityClassType[] getClassTypes() {
        return entityClassTypes;
    }
}
