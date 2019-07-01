package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Default implementation of {@link IEntityAction}
 */
public abstract class DefaultEntityAction extends ForgeRegistryEntry<IEntityAction> implements IEntityAction {
    private EntityClassType[] entityClassTypes;
    private EntityActionTier entityActionTier;

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
    @Override
    public @Nonnull
    EntityActionTier getTier() {
        return entityActionTier;
    }
}
