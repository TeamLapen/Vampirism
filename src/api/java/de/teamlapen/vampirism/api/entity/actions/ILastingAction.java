package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.world.entity.PathfinderMob;

/**
 * {@link IEntityAction} that takes several ticks to execute.
 */
public interface ILastingAction<T extends PathfinderMob & IEntityActionUser> extends IEntityAction {

    /**
     * called to activate the action
     *
     * @param entity for which the action should be activated
     */
    void activate(T entity);

    /**
     * called when the duration of the action is over
     *
     * @param entity entity which action should be deactivated
     */
    void deactivate(T entity);

    /**
     * @param level level of the IFactionEntity
     * @return duration of the action in ticks
     */
    int getDuration(int level);

    /**
     * (should be) called every LivingUpdate of {@link net.minecraft.world.entity.Mob}
     *
     * @param entity   entity whose action is to be updated
     * @param duration lasting duration of the action
     */
    void onUpdate(T entity, int duration);

    /**
     * called before action will be activated
     *
     * @param entity for which the action should be activated
     */
    default void updatePreAction(T entity, int duration) {

    }
}
