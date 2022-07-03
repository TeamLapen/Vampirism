package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;

import javax.annotation.Nonnull;

/**
 * Default implementation of {@link IEntityAction}
 */
public abstract class DefaultEntityAction implements IEntityAction {
    private final EntityClassType[] entityClassTypes;
    private final EntityActionTier entityActionTier;

    public DefaultEntityAction(@Nonnull EntityActionTier tier, EntityClassType... param) {
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
    @Nonnull
    @Override
    public EntityActionTier getTier() {
        return entityActionTier;
    }
}
