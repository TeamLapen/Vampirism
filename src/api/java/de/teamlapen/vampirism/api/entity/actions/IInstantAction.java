package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;

public interface IInstantAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends IEntityAction {

    /**
     * called to active the action
     * 
     * @param entity
     *            for which the action should be activated
     */
    boolean activate(T entity);
}
