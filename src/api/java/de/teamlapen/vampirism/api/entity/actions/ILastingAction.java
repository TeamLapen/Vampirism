package de.teamlapen.vampirism.api.entity.actions;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;

/**
 * {@link IEntityAction} that takes several ticks to execute.
 */
public interface ILastingAction<T extends CreatureEntity & IEntityActionUser> extends IEntityAction {

    /**
     * @param level
     *            level of the IFactionEntity
     * @return duration of the action in ticks
     */
    int getDuration(int level);

    /**
     * called when the duration of the action is over
     *
     * @param entity
     *            entity which action should be deactivated
     */
    void deactivate(T entity);

    /**
     * (should be) called every LivingUpdate of {@link MobEntity}
     *
     * @param entity
     *            entity whose action is to be updated
     * @param duration
     *            lasting duration of the action
     */
    void onUpdate(T entity, int duration);

    /**
     * called to activate the action
     *
     * @param entity
     *            for which the action should be activated
     */
    void activate(T entity);

    /**
     * called before action will be activated
     *
     * @param entity
     *            for which the action should be activated
     */
    default void updatePreAction(T entity, int duration) {

    }
}
