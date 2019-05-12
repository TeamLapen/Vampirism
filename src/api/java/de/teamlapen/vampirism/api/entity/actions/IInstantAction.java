package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.entity.EntityCreature;

/**
 * {@link IEntityAction} that executes immediately
 */
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
    default void updatePreAction(T entity, int duration) {

    }
}
