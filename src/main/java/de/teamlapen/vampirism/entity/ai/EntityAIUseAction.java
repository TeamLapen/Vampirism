package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import java.util.Random;

/*
 * Usage only for entity from Vampire/Hunter faction
 */
public class EntityAIUseAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends EntityAIBase {

    private T entity;
    private IFaction faction;
    private List availableActions;

    /**
     * Saves timers for action ids Values: 0 - next action <0 - cooldown for next action >0 - active action
     */
    private int cooldown = 0;
    private DefaultEntityAction action;
    private Random rand = new Random();

    public EntityAIUseAction(T entityIn) {
        this.entity = entityIn;
        this.faction = entityIn.getFaction();
        this.availableActions = VampirismAPI.entityActionManager().getEntityActionForFaction(faction);
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
        action = UtilLib.getNull();
        cooldown = -100;
    }

    @Override
    public void updateTask() {
        if (cooldown > 0) {
            cooldown--;
            updateAction();
        } else if (cooldown < 0) {
            cooldown++;
        } else {
            action = newAction();
            if (action instanceof ILastingAction)
                cooldown = ((ILastingAction) action).getDuration(entity.getLevel());
            else if (action instanceof IInstantAction)
                cooldown = ((IInstantAction) action).getCooldown(entity.getLevel());
            else
                cooldown = (int) (-100 * (1 + rand.nextFloat() * 2));
        }
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    private void updateAction() {
        if (action instanceof ILastingAction) { // TODO if no lasting action, but another -> activate -> -cooldown
            ILastingAction action = (ILastingAction) this.action;
            action.onUpdate(entity);
            if (cooldown == 1) {
                action.deactivate(entity);
                cooldown = -action.getCooldown(entity.getLevel());
            }
        } else if (action instanceof IInstantAction) {
            ((IInstantAction) action).activate(entity);
            this.action = UtilLib.getNull();
        }
    }

    private DefaultEntityAction newAction() {
        if (rand.nextInt(1) == 0) { // TODO
            DefaultEntityAction a = (DefaultEntityAction) availableActions.get(rand.nextInt(availableActions.size()));
            System.out.println(a.getClass().getName());
            return a;
        }
        return UtilLib.getNull();
    }
}
