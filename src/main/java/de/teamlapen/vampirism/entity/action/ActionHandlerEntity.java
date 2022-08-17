package de.teamlapen.vampirism.entity.action;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.actions.*;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Usage for every {@link IFactionEntity} like Hunter/Vampire entities,
 * is used with {@link ActionHandlerEntity#handle()} in UpdateLiving in an VampirismEntity
 * Entity Actions are server side only
 */
public class ActionHandlerEntity<T extends PathfinderMob & IEntityActionUser> implements IActionHandlerEntity {

    private final T entity;
    private final List<IEntityAction> availableActions;
    private int preActivation = 0;
    private int cooldown = 0;
    private int duration = 0;
    private float minimumHealthThreshold;
    @Nullable
    private IEntityAction action;
    private boolean isPlayerTarget;

    public ActionHandlerEntity(@NotNull T entityIn) {
        this.entity = entityIn;
        this.availableActions = entityIn.getAvailableActions();
    }

    public void deactivateAction() {
        deactivateAction(null);
    }

    @Nullable
    public IEntityAction getAction() {
        return action;
    }

    public void handle() {
        if (!entity.level.isClientSide && availableActions != null && !availableActions.isEmpty()) {
            if (entity.getTarget() instanceof Player) {
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

    public boolean isActionActive(IEntityAction action) {
        return this.action != null && this.action.equals(action) && duration > 0;
    }

    public boolean isPlayerTarget() {
        return isPlayerTarget;
    }

    public void read(@NotNull CompoundTag nbt) {
        if (nbt.contains("activeAction")) {
            deactivateAction(RegUtil.getEntityAction(new ResourceLocation(nbt.getString("activeAction"))));
            isPlayerTarget = true;
        }
    }

    public void startExecuting() {
        action = null;
        cooldown = 50;
        duration = -1;
        preActivation = -1;
    }

    public void write(@NotNull CompoundTag nbt) {
        if (isPlayerTarget() && action != null) {
            nbt.putString("activeAction", RegUtil.id(action).toString());
        }
    }

    /**
     * activates the {@link ActionHandlerEntity#action}
     */
    @SuppressWarnings("unchecked")
    private void activateAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).activate(entity);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).activate(entity);
        }
    }

    /**
     * cancels the action by setting {@link ActionHandlerEntity#preActivation}, {@link ActionHandlerEntity#duration} and {@link ActionHandlerEntity#cooldown} to default
     */
    private void cancelActivation() {
        preActivation = -1;
        duration = -1;
        cooldown = 100;
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
            return WeightedRandom.getRandomItem(entity.getRandom(), entry, weightsum).map(EntityActionEntry::getAction).orElse(null);
        }
        return null;
    }

    /**
     * reset the given {@link IEntityAction} or if null, reset the {@link ActionHandlerEntity#action}
     */
    @SuppressWarnings("unchecked")
    private void deactivateAction(@Nullable IEntityAction actionIn) {
        IEntityAction action = actionIn != null ? actionIn : this.action;
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).deactivate(entity);
        }
    }

    /**
     * updates the {@link ActionHandlerEntity#action}
     */
    @SuppressWarnings("unchecked")
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
            if (entity.getHealth() < minimumHealthThreshold) {
                cancelActivation();
            }
        } else if (duration == 0) { /* deactivate action */
            /* calls deactivateAction() for {@link ILastingAction} once per action */
            deactivateAction();
            duration--;
        } else if (duration > 0) { /* action is in progress */
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the action's duration last */
            duration--;
            updateAction();
        } else if (cooldown > 0) {/* action on cooldown */
            /* calls updateAction() for {@link ILastingAction} & {@link IInstantAction} as long as the action's cooldown last */
            cooldown--;
        } else { /* new action */
            /* sets a new action if cooldown is over */
            action = chooseNewAction();
            if (action == null) {
                cancelActivation();
                return;
            }
            cooldown = action.getCooldown(entity.getEntityLevel());
            preActivation = action.getPreActivationTime();
            minimumHealthThreshold = (float) (entity.getHealth() - (entity.getMaxHealth() * VampirismConfig.BALANCE.eaHealthThreshold.get()));
            if (action instanceof ILastingAction) {
                //noinspection unchecked
                duration = ((ILastingAction<T>) action).getDuration(entity.getEntityLevel());
            }
        }
    }

    /**
     * updates the {@link ActionHandlerEntity#action}
     **/
    @SuppressWarnings("unchecked")
    private void updatePreAction() {
        if (action instanceof ILastingAction) {
            ((ILastingAction<T>) action).updatePreAction(entity, preActivation);
        } else if (action instanceof IInstantAction) {
            ((IInstantAction<T>) action).updatePreAction(entity, preActivation);
        }
    }
}
