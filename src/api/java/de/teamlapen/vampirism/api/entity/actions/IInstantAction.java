package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.entity.EntityCreature;

public interface IInstantAction<T extends EntityCreature & IEntityActionUser> extends IEntityAction {

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
