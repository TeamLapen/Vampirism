package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.world.entity.PathfinderMob;

/**
 * {@link IEntityAction} that executes immediately
 */
public interface IInstantAction<T extends PathfinderMob & IEntityActionUser> extends IEntityAction {

    /**
     * called to activate the action
     *
     * @param entity for which the action should be activated
     */
    @SuppressWarnings("SameReturnValue")
    boolean activate(T entity);

    /**
     * called before action will be activated
     *
     * @param entity for which the action should be activated
     */
    default void updatePreAction(T entity, int duration) {

    }
}
