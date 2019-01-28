package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import java.util.Random;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities
 * can be used as AITask in custom task list or with {@link handle()} in UpdateLiving in an Entity
 */
public class EntityAIUseAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends EntityAIBase {

    private T entity;
    private List<IEntityAction> availableActions;
    private int cooldown = 0;
    private int duration = 0;
    private IEntityAction action;
    private Random rand = new Random();
    private boolean flag;

    public EntityAIUseAction(T entityIn, List<IEntityAction> actions) {
        this.entity = entityIn;
        this.availableActions = actions;
    }

    @Override
    public boolean shouldExecute() {
        if (entity.getAttackTarget() instanceof EntityPlayer && !availableActions.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        action = null;
        cooldown = 100;
        duration = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started,
     * 
     * {@link duration} > 0 action in progress,
     * {@link duration} = 0 action will be deactivated,
     * {@link duration} = -1 no action is active,
     * {@link duration} <= 0 && {@link cooldown} > 0 actions on cooldown,
     * {@link duration} <= 0 && {@link cooldown} = 0 new action will be set & activated
     */
    @Override
    public void updateTask() {
        if (duration > 0) {
            duration--;
            updateAction();
        } else if (cooldown > 0) {
            cooldown--;
        } else {
            newAction();
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
            if (action instanceof ILastingAction) {
                ((ILastingAction<T>) action).deactivate(entity);
            }
            duration--;
        }
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
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
     * sets a new random action from {@link availableActions} for the entity,
     * otherwise a placeholder
     */
    private void newAction() {
        if (rand.nextInt(1) == 0) { // TODO usable balance
                action = availableActions.get(rand.nextInt(availableActions.size()));
        } else {
            action = new DefaultEntityAction() {
                @Override
                public int getCooldown(int level) {
                    return (int) (100 * (1 + rand.nextFloat() * 2)); // TODO edit
                }
            };
        }
    }

    /**
     * use this method if EntityAIUseAction is used in onUpdate() in an Entity
     */
    public void handle() {
        if (shouldExecute()) {
            if (flag) {
                updateTask();
            } else {
                startExecuting();
                flag = true;
            }
        } else if (!availableActions.isEmpty()) {
            if (flag) {
                resetTask();
            }
            flag = false;
        }
    }

    public void reset() {
        for (IEntityAction action : availableActions) {
            if (action instanceof ILastingAction) {
                ((ILastingAction<T>) action).deactivate(entity);
            }
        }
    }
}
