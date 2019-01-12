package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import java.util.Random;

/*
 * Usage every {@link IFactionEntity} like Hunter/Vampire entities
 */
public class EntityAIUseAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends EntityAIBase {

    private T entity;
    private List<DefaultEntityAction> availableActions;
    private int cooldown = 0;
    private int duration = 0;
    private DefaultEntityAction action;
    private Random rand = new Random();

    public EntityAIUseAction(T entityIn, List<DefaultEntityAction> actions) {
        this.entity = entityIn;
        this.availableActions = actions;
    }

    @Override
    public boolean shouldExecute() {
        if (entity.getAttackTarget() instanceof EntityPlayer) {
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        action = null;
        cooldown = 100;
    }


    @Override
    public void updateTask() {
        if (duration > 0 && cooldown > 0) {
            duration--;
            updateAction();
        } else if (cooldown > 0 && duration <= 0) {
            cooldown--;
        } else if (cooldown <= 0 && duration <= 0) {
            newAction();
            duration = action.getDuration(entity.getLevel());
            cooldown = action.getCooldown(entity.getLevel());
        }
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
        if (action instanceof ILastingAction) {
            ((ILastingAction) action).deactivate(entity);
        }
    }

    private void updateAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction) action).onUpdate(entity, duration);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction) action).activate(entity);
            this.action = null;
        }
    }

    private void newAction() {
        if (rand.nextInt(1) == 0) { // TODO usable balance
            try {
                action = availableActions.get(rand.nextInt(availableActions.size()));
                System.out.println(action.getClass().getName()); // TODO remove
            } catch (NullPointerException e) {
                action = new DefaultEntityAction() {
                    @Override
                    public int getCooldown(int level) {
                        return (int) (100 * (1 + rand.nextFloat() * 2)); // TODO edit
                    }
                };
            }
        }
    }
}
