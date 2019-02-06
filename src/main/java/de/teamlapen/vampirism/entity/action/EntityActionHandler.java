package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * is used with {@link handle()} in UpdateLiving in an EntityVampirism
 */
public class EntityActionHandler<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> {

    private T entity;
    private List<IEntityAction> availableActions;
    private int preActivation = 0;
    private int cooldown = 0;
    private int duration = 0;
    private IEntityAction action;
    private boolean isPlayerTarget;

    public EntityActionHandler(T entityIn) {
        this.entity = entityIn;
    }

    public void startExecuting() {
        action = null;
        cooldown = 50;
        duration = -1;
        preActivation = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started and sets a new action
     * 
     * {@link preActivation} > 0 action will be started soon
     * {@link preActivation} = 0 action is starting now
     * {@link preActivation} = -1 action was started
     * {@link duration} > 0 action in progress,
     * {@link duration} = 0 action will be deactivated,
     * {@link duration} = -1 no action is active,
     * {@link duration} <= 0 && {@link cooldown} > 0 actions on cooldown,
     * {@link duration} <= 0 && {@link cooldown} = 0 new action will be set
     */
    public void updateHandler() {
        if (preActivation == 0) {
            /* calls activate() for {@link ILastingAction} & {@link IInstantAction} once per action */
            activateAction();
            preActivation--;
        } else if (preActivation > 0) {
            /* calls updatePreAction() for {@link ILastingAction} & {@link IInstantAction} as long as the action need to be activated */
            updatePreAction();
            preActivation--;
        } else if (duration == 0) {
            /* calls resetAction() for {@link ILastingAction} once per action */
            resetAction();
            duration--;
        } else if (duration > 0) {
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the actions duration last */
            duration--;
            updateAction();
        } else if (cooldown > 0) {
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the actions cooldown last */
            cooldown--;
        } else {
            /* sets a new action if cooldown is over */
            action = getIntelligentAction();
            cooldown = action.getCooldown(entity.getLevel());
            preActivation = action.getPreActivationTime();
            if (action instanceof ILastingAction) {
                duration = ((ILastingAction<T>) action).getDuration(entity.getLevel());
            }
        }
    }

    public void resetAction() {
        resetAction(null);
    }

    /**
     * reset the given {@link IEntityAction} or if null, reset the {@link action}
     * 
     * @param actionIn
     */
    @Nullable
    public void resetAction(IEntityAction actionIn) {
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
    @Nullable
    public void updateAction(IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).onUpdate(entity, duration);
        }
    }

    public void updatePreAction() {
        updateAction(null);
    }

    /**
     * updates the given {@link IEntityAction} pre activation or if null, updates the {@link action}
     * 
     * @param actionIn
     */
    @Nullable
    public void updatePreAction(IEntityAction actionIn) {
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
    @Nullable
    public void activateAction(IEntityAction actionIn) {
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
    private IEntityAction getIntelligentAction() {
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
                actionsMap.computeIfPresent(EntityActions.entity_regeneration, (k, v) -> v + 2);
            else if (healthPercent < 0.4)
                actionsMap.computeIfPresent(EntityActions.entity_regeneration, (k, v) -> v + 1);
        }

        if (entity instanceof IVampire) {
            /* Vampire Only Actions */
            IVampire entity = (IVampire) this.entity;
            /* Sunscream Action */
            if (entity.isGettingSundamage() && !entity.isIgnoringSundamage()) {
                actionsMap.computeIfPresent(EntityActions.entity_sunscream, (k, v) -> v + actionsMap.size() < 5 ? 4 : 2);
            }
            /* Bat Spawn Action */
            actionsMap.computeIfPresent(EntityActions.entity_bat_spawn, (k, v) -> v + actionsMap.size() < 4 ? (int) 1 * this.entity.getRNG().nextInt(2) : 0);
            /* Dark Projectile Action */
            if (distanceToTarget > 20)
                actionsMap.computeIfPresent(EntityActions.entity_speed, (k, v) -> v + 2);
            else if (distanceToTarget > 10)
                actionsMap.computeIfPresent(EntityActions.entity_speed, (k, v) -> v + 1);
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
                    resetAction();
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
            resetAction(VampirismAPI.entityActionManager().getRegistry().getValue(new ResourceLocation("vampirism", nbt.getString("activeAction"))));
            isPlayerTarget = true;
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        if (isPlayerTarget() && getAction() != null) {
            nbt.setString("activeAction", action.getRegistryName().getResourcePath());
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
