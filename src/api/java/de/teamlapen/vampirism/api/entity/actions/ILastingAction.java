package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;

public interface ILastingAction<T extends EntityCreature & IEntityActionUser> extends IEntityAction {

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
     * called every LivingUpdate of {@link EntityLiving} which implements {@link EntityVampirism} & {@link IFactionEntity} & {@link IAdjustableLevel}
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
    void updatePreAction(T entity, int duration);
}
