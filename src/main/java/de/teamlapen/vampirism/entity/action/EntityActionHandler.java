package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * is used with {@link handle()} in UpdateLiving in an EntityVampirism
 */
public class EntityActionHandler<T extends EntityCreature & IEntityActionUser> {

    private T entity;
    private List<IEntityAction> availableActions;
    private int preActivation = 0;
    private int cooldown = 0;
    private int duration = 0;
    private float healthTresholdForDisruption;
    private IEntityAction action;
    private boolean isPlayerTarget;

    public EntityActionHandler(T entityIn) {
        this.entity = entityIn;
        this.availableActions = entityIn.getAvailableActions();
    }

    public void startExecuting() {
        action = null;
        cooldown = 50;
        duration = -1;
        preActivation = -1;
    }

    /**
     * called every time the entity is fighting a player, handles the actions
     */
    public void updateHandler() {
        if (preActivation == 0) { /* action starts now */
            /* calls activate() for {@link ILastingAction} & {@link IInstantAction} once per action */
            activateAction();
            preActivation--;
        } else if (preActivation > 0) {/* action will be started soon */
            /* calls updatePreAction() for {@link ILastingAction} & {@link IInstantAction} as long as the action need to be activated */
            updatePreAction();
            preActivation--;
            if (entity.getHealth() < healthTresholdForDisruption) {
                cancelActivation();
            }
        } else if (duration == 0) { /* deactivate action */
            /* calls deactivateAction() for {@link ILastingAction} once per action */
            deactivateAction();
            duration--;
        } else if (duration > 0) { /* action is in progress */
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the actions duration last */
            duration--;
            updateAction();
        } else if (cooldown > 0) {/* action on cooldown */
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the actions cooldown last */
            cooldown--;
        } else { /* new action */
            /* sets a new action if cooldown is over */
            action = chooseNewAction();
            cooldown = action.getCooldown(entity.getLevel());
            preActivation = action.getPreActivationTime();
            healthTresholdForDisruption = entity.getHealth() - (entity.getMaxHealth() * (float) Balance.ea.DISRUPTION_HEALTH_AMOUNT);
            if (action instanceof ILastingAction) {
                duration = ((ILastingAction<T>) action).getDuration(entity.getLevel());
            }
        }
    }

    /**
     * cancels the action by setting {@link preActivation}, {@link duration} and {@link cooldown} to default
     */
    public void cancelActivation() {
        preActivation = -1;
        duration = -1;
        cooldown = 100;
    }

    public void deactivateAction() {
        deactivateAction(null);
    }

    /**
     * reset the given {@link IEntityAction} or if null, reset the {@link action}
     * 
     * @param actionIn
     */
    public void deactivateAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).deactivate(entity);
        }
    }

    public void updateAction() {
        updateAction(null);
    }

    /**
     * updates the given {@link IEntityAction} or if null, activates the {@link action}
     * 
     * @param actionIn
     */
    public void updateAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).onUpdate(entity, duration);
        }
    }

    public void updatePreAction() {
        updatePreAction(null);
    }

    /**
     * updates the given {@link IEntityAction} pre activation or if null, updates the {@link action}
     * 
     * @param actionIn
     */
    public void updatePreAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).updatePreAction(entity, preActivation);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).updatePreAction(entity, preActivation);
        }
    }

    public void activateAction() {
        activateAction(null);
    }

    /**
     * activates the given {@link IEntityAction} or if null, activates the {@link action}
     * 
     * @param actionIn
     */
    public void activateAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).activate(entity);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).activate(entity);
        }
    }

    /**
     * returns a new random action from {@link availableActions}
     */
    @Nullable
    private IEntityAction getRandomAction() {
        return availableActions.get(entity.getRNG().nextInt(availableActions.size()));
    }

    /**
     * returns a plausible action
     * 
     * elevates the chance of the actions, based on the entity and its target
     * 
     * @returns
     */
    @Nonnull
    private IEntityAction chooseNewAction() {
        Map<IEntityAction, Integer> actionsMap = new HashMap<>();
        for (IEntityAction e : availableActions) {
            actionsMap.put(e, 1);
        }
        double distanceToTarget = new Vec3d(entity.posX, entity.posY, entity.posZ).subtract(entity.getAttackTarget().posX, entity.getAttackTarget().posY, entity.getAttackTarget().posZ).lengthVector();
        double healthPercent = entity.getHealth() / entity.getMaxHealth();
        /* Speed Action */
        if (distanceToTarget > 10)
            actionsMap.computeIfPresent(EntityActions.entity_speed, (k, v) -> v + 2);
        else if (distanceToTarget > 5)
            actionsMap.computeIfPresent(EntityActions.entity_speed, (k, v) -> v + 1);
        /* Regeneration Action && Heal Action */
        if (actionsMap.containsKey(EntityActions.entity_heal)) {
            /* Heal */
            if (healthPercent < 0.1)
                actionsMap.compute(EntityActions.entity_heal, (k, v) -> v + 2);
            else if (healthPercent < 0.4)
                actionsMap.compute(EntityActions.entity_heal, (k, v) -> v + 1);
        } else {
            /* Regeneration */
            if (healthPercent < 0.1)
                actionsMap.computeIfPresent(EntityActions.entity_regeneration_areaofeffect, (k, v) -> v + 2);
            else if (healthPercent < 0.4)
                actionsMap.computeIfPresent(EntityActions.entity_regeneration_areaofeffect, (k, v) -> v + 1);
        }

        if (entity instanceof IVampire) {
            /* Vampire Only Actions */
            IVampire entity = (IVampire) this.entity;
            /* Sunscream Action */
            if (entity.isGettingSundamage() && !entity.isIgnoringSundamage()) {
                actionsMap.computeIfPresent(EntityActions.entity_sunscreen, (k, v) -> v + actionsMap.size() < 5 ? 4 : 2);
            }
            /* Dark Projectile Action */
            if (distanceToTarget > 20)
                actionsMap.computeIfPresent(EntityActions.entity_dark_projectile, (k, v) -> v + 2);
            else if (distanceToTarget > 10)
                actionsMap.computeIfPresent(EntityActions.entity_dark_projectile, (k, v) -> v + 1);
            /* Invisible Action */
            if (distanceToTarget > 4 && actionsMap.size() < 5)
                actionsMap.computeIfPresent(EntityActions.entity_invisible, (k, v) -> v + actionsMap.size() < 5 ? 2 : 1);
        } else if (entity instanceof IHunter) {
            /* Hunter Only Actions */
            IHunter entity = (IHunter) this.entity;
        }

        List<IEntityAction> actionList = new ArrayList<>();
        for (Map.Entry<IEntityAction, Integer> e : actionsMap.entrySet()) {
            for (int i = 0; i < e.getValue(); i++) {
                actionList.add(e.getKey());
            }
        }
        return actionList.get(entity.getRNG().nextInt(actionList.size()));
    }

    public void handle() {
        if (availableActions != null && !availableActions.isEmpty()) {
            if (entity.getAttackTarget() instanceof EntityPlayer) {
                if (isPlayerTarget) {
                    updateHandler();
                } else {
                    startExecuting();
                    isPlayerTarget = true;
                }
            } else {
                if (isPlayerTarget) {
                    isPlayerTarget = false;
                    deactivateAction();
                }
            }
        }
    }

    public IEntityAction getAction() {
        return action;
    }

    public boolean isPlayerTarget() {
        return isPlayerTarget;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("activeAction")) {
            deactivateAction(VampirismAPI.entityActionManager().getRegistry().getValue(new ResourceLocation(nbt.getString("activeAction"))));
            isPlayerTarget = true;
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        if (isPlayerTarget() && getAction() != null) {
            nbt.setString("activeAction", action.getRegistryName().toString());
        }
    }

    /**
     * Sets the given list of actions to the actions the EntityActionHandler should use
     * 
     * @param actionsIn
     */
    public void setAvailableActions(List<IEntityAction> actionsIn) {
        this.availableActions = actionsIn;
    }

    public void removeAction(IEntityAction actionIn) {
        availableActions.remove(actionIn);
    }
}
