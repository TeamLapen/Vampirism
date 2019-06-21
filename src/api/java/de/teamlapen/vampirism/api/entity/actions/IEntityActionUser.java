package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

import java.util.List;

public interface IEntityActionUser extends IAdjustableLevel, IFactionEntity {

    default EntityActionTier getEntityTier() {
        return EntityActionTier.Default;
    }

    default EntityClassType getEntityClass() {
        return EntityClassType.None;
    }

    /**
     * gets all available actions for this entity.
     */
    default List<IEntityAction> getAvailableActions() {
        return VampirismAPI.entityActionManager().getAllEntityActionsByTierAndClassType(this.getFaction(), getEntityTier(), getEntityClass());
    }
}
