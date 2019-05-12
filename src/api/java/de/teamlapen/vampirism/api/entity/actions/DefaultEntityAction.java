package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Default implementation of {@link IEntityAction}
 */
public abstract class DefaultEntityAction extends ForgeRegistryEntry<IEntityAction> implements IEntityAction {
    private EntityClassType[] entityClassTypes;
    private EntityActionTier entityActionTier;

    public DefaultEntityAction(EntityActionTier tier, EntityClassType... param) {
        entityActionTier = tier;
        entityClassTypes = param;
    }

    /**
     * @return the actions {@link EntityClassType}
     */
    @Override
    public EntityClassType[] getClassTypes() {
        return entityClassTypes;
    }

    /**
     * @return the actions {@link EntityActionTier}
     */
    @Override
    public EntityActionTier getTier() {
        return entityActionTier;
    }
}
