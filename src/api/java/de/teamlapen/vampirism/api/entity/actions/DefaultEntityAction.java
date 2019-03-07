package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DefaultEntityAction<T extends EntityCreature & IEntityActionUser> extends IForgeRegistryEntry.Impl<IEntityAction> implements IEntityAction {
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

    /**
     * @return weight of this action
     */
    public int getWeight(T entity) {
        return 1;
    }
}
