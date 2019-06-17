package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

import java.util.List;

public interface IEntityActionUser extends IAdjustableLevel, IFactionEntity {

    /**
     * gets all available actions for this entity.
     */
    List<IEntityAction> getAvailableActions();
}
