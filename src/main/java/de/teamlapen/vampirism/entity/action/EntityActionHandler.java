package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.VampirismMod;
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
import java.util.*;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * is used with {@link handle()} in UpdateLiving in an EntityVampirism
 */
public class EntityActionHandler<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> {

    private T entity;
    private List<IEntityAction> availableActions;
    private int cooldown = 0;
    private int duration = 0;
    private IEntityAction action;
    private Random rand = new Random();
    private boolean isPlayerTarget;

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
            action = getIntelligentAction();
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
        return availableActions.get(rand.nextInt(availableActions.size()));
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
        return actionList.get(entity.getRNG().nextInt(actionList.size() - 1));
    }

    /**
     * use this method if EntityAIUseAction is used in onUpdate() in an IFactionEntity class
     */
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
                    resetAction(null);
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
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        if (isPlayerTarget() && getAction() != null) {
            nbt.setString("activeAction", action.getRegistryName().getResourcePath());
        }
    }

    public void setAvailableActions(List<IEntityAction> actionsIn) {
        this.availableActions = actionsIn;
    }

}
