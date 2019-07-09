package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.actions.*;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * is used with {@link EntityActionHandler#handle()} in UpdateLiving in an EntityVampirism
 */
public class EntityActionHandler<T extends CreatureEntity & IEntityActionUser> {

    private T entity;
    private List<IEntityAction> availableActions;
    private int preActivation = 0;
    private int cooldown = 0;
    private int duration = 0;
    private float healthThresholdForDisruption;
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
     * Sets the given list of actions to the actions the EntityActionHandler should use
     *
     * @param actionsIn
     */
    public void setAvailableActions(List<IEntityAction> actionsIn) {
        this.availableActions = actionsIn;
        if (availableActions.contains(EntityActions.entity_heal)) {
            availableActions.remove(EntityActions.entity_regeneration);
        }
    }

    /**
     * activates the {@link EntityActionHandler#action}
     */
    private void activateAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).activate(entity);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).activate(entity);
        }
    }

    /**
     * cancels the action by setting {@link EntityActionHandler#preActivation}, {@link EntityActionHandler#duration} and {@link EntityActionHandler#cooldown} to default
     */
    private void cancelActivation() {
        preActivation = -1;
        duration = -1;
        cooldown = 100;
    }

    /**
     * reset the given {@link IEntityAction} or if null, reset the {@link EntityActionHandler#action}
     *
     * @param actionIn
     */
    private void deactivateAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).deactivate(entity);
        }
    }

    private void deactivateAction() {
        deactivateAction(null);
    }

    /**
     * updates the {@link EntityActionHandler#action}
     */
    private void updateAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).onUpdate(entity, duration);
        }
    }

    /**
     * called every time the entity is fighting a player, handles the actions
     */
    private void updateHandler() {
        if (preActivation == 0) { /* action starts now */
            /* calls activate() for {@link ILastingAction} & {@link IInstantAction} once per action */
            activateAction();
            preActivation--;
        } else if (preActivation > 0) {/* action will be started soon */
            /* calls updatePreAction() for {@link ILastingAction} & {@link IInstantAction} as long as the action need to be activated */
            updatePreAction();
            preActivation--;
            if (entity.getHealth() < healthThresholdForDisruption) {
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
            if (action == null) {
                cancelActivation();
                return;
            }
            cooldown = action.getCooldown(entity.getLevel());
            preActivation = action.getPreActivationTime();
            healthThresholdForDisruption = entity.getHealth() - (entity.getMaxHealth() * (float) Balance.ea.DISRUPTION_HEALTH_AMOUNT);
            if (action instanceof ILastingAction) {
                duration = ((ILastingAction<T>) action).getDuration(entity.getLevel());
            }
        }
    }

    public void handle() {
        if (availableActions != null && !availableActions.isEmpty()) {
            if (entity.getAttackTarget() instanceof PlayerEntity) {
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

    public void read(CompoundNBT nbt) {
        if (nbt.contains("activeAction")) {
            deactivateAction(VampirismAPI.entityActionManager().getRegistry().getValue(new ResourceLocation(nbt.getString("activeAction"))));
            isPlayerTarget = true;
        }
    }

    public IEntityAction getAction() {
        return action;
    }

    public boolean isPlayerTarget() {
        return isPlayerTarget;
    }

    public void write(CompoundNBT nbt) {
        if (isPlayerTarget() && getAction() != null) {
            nbt.putString("activeAction", action.getRegistryName().toString());
        }
    }

    /**
     * elevates the chance of the actions, based on the entity and its target
     */
    @Nullable
    private IEntityAction chooseNewAction() {
        List<EntityActionEntry> entry = Lists.newArrayList();
        int weightsum = 0;
        for (IEntityAction e : availableActions) {
            int weight = e.getWeight(entity);
            if (weight > 0) {
                entry.add(new EntityActionEntry(weight, e));
                weightsum += weight;
            }
        }
        if (weightsum > 0) {
            return WeightedRandom.getRandomItem(entity.getRNG(), entry, weightsum).getAction();
        }
        return null;
    }

    /**
     * updates the {@link EntityActionHandler#action}
     **/
    private void updatePreAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).updatePreAction(entity, preActivation);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).updatePreAction(entity, preActivation);
        }
    }

    public void removeAction(IEntityAction actionIn) {
        availableActions.remove(actionIn);
    }
}
