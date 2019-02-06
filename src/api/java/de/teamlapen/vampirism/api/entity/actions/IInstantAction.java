package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

public interface IInstantAction<T extends IVampirismEntity & IFactionEntity & IAdjustableLevel> extends IEntityAction {

    /**
     * called to active the action
     * 
     * @param entity
     *            for which the action should be activated
     */
    boolean activate(T entity);

    /**
     * called before action will be activated
     * 
     * @param entity
     *            for which the action should be activated
     */
    void updatePreAction(T entity, int duration);
}
