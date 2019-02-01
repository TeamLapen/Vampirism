package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.player.EntityPlayer;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * can be used with {@link handle()} in UpdateLiving in an Entity
 */
public class EntityActionHandler<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> {

    private T entity;
    private List<IEntityAction> availableActions;
    private int cooldown = 0;
    private int duration = 0;
    private IEntityAction action;
    private Random rand = new Random();
    /** true if a player is used as target by actions */
    private boolean flag;

    public EntityActionHandler(T entityIn, List<IEntityAction> actions) {
        this.entity = entityIn;
        this.availableActions = actions;
    }

    /**
     * starts the execution by setting the base values
     */
    public void startExecuting() {
        action = null;
        cooldown = 100;
        duration = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started and sets a new action
     * 
     * {@link duration} > 0 action in progress,
     * {@link duration} = 0 action will be deactivated,
     * {@link duration} = -1 no action is active,
     * {@link duration} <= 0 && {@link cooldown} > 0 actions on cooldown,
     * {@link duration} <= 0 && {@link cooldown} = 0 new action will be set & activated
     */
    public void updateHandler() {
        if (duration > 0) {
            duration--;
            updateAction();
        } else if (cooldown > 0) {
            cooldown--;
        } else {
            action = getRandomAction();
            if (action == null) {
                cooldown = (int) (100 * (1 + rand.nextFloat() * 2)); //TODO modify
                return;
            }
            cooldown = action.getCooldown(entity.getLevel());
            if (action instanceof ILastingAction) {
                duration = ((ILastingAction<T>) action).getDuration(entity.getLevel());
                ((ILastingAction<T>) action).activate(entity);
            } else if (action instanceof IInstantAction) {
                ((IInstantAction<T>) action).activate(entity);
            }
        }

        /* calls deactivate() for {@link ILastingAction} once per action */
        if (duration == 0) {
            resetAction(null);
            duration--;
        }
    }

    /**
     * resets the action. called if the target switches from a player to null or something else and after the duration of an action is over
     * 
     * @param actionIn
     *            called with an action if a specific action should be deactivated else null
     */
    public void resetAction(IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).deactivate(entity);
        }
    }

    /**
     * updates action based on actiontype
     */
    private void updateAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).onUpdate(entity, duration);
        }
    }

    /**
     * returns a new random action from {@link availableActions} or null
     */
    @Nullable
    private IEntityAction getRandomAction() {
        if (rand.nextInt(1) == 0) { // TODO usable balance
            return availableActions.get(rand.nextInt(availableActions.size()));
        }
        return null;
    }

    /**
     * use this method if EntityAIUseAction is used in onUpdate() in an IFactionEntity class
     */
    public void handle() {
        if (availableActions != null && !availableActions.isEmpty()) {
            if (entity.getAttackTarget() instanceof EntityPlayer) {
                if (flag) {
                    updateHandler();
                } else {
                    startExecuting();
                    flag = true;
                }
            } else {
                if (flag) {
                    flag = false;
                    resetAction(null);
                }
            }
        }
    }

    public IEntityAction getAction() {
        return action;
    }

    public boolean getFlag() {
        return flag;
    }
}
