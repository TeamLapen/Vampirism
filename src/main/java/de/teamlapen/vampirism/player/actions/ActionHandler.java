package de.teamlapen.vampirism.player.actions;

import com.google.common.collect.ImmutableBiMap;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles skill for vampire players
 */
public class ActionHandler<T extends IFactionPlayer> implements IActionHandler<T> {
    private final static String TAG = "ActionHandler";

    /**
     * Saves timers for skill ids
     * Values:
     * 0 - Inactive
     * <0 - Cooldown
     * >0 - Active {@link ILastingAction}
     */
    private final int[] actionTimer;
    private final T player;

    /**
     * Action ids might differ between client and server
     */
    private final ImmutableBiMap<Integer, IAction> actionIdMap;
    private final List<IAction> unlockedActions = new ArrayList<>();
    private boolean dirty = false;

    public ActionHandler(T player) {
        this.player = player;
        List<IAction> actions = VampirismAPI.actionManager().getActionsForFaction(player.getFaction());
        ImmutableBiMap.Builder<Integer, IAction> idBuilder = ImmutableBiMap.builder();
        int i = 0;
        for (IAction action : actions) {
            idBuilder.put(i++, action);
        }
        actionIdMap = idBuilder.build();
        this.actionTimer = new int[actions.size()];
    }

    public void deactivateAllActions() {
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] > 0) {
                actionTimer[i] = -getActionFromId(i).getCooldown();
                ((ILastingAction) getActionFromId(i)).onDeactivated(player);

            }
        }
    }

    @Override
    public List<IAction> getAvailableActions() {
        ArrayList<IAction> actions = new ArrayList<>();
        for (IAction action : unlockedActions) {
            if (action.canUse(player) == IAction.PERM.ALLOWED) {
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    public float getPercentageForAction(IAction action) {
        Integer id = getIdFromAction(action);
        int i = actionTimer[id];
        if (i == 0) return 0F;
        if (i > 0) return i / ((float) ((ILastingAction) action).getDuration(player.getLevel()));
        return i / (float) action.getCooldown();
    }

    @Override
    public boolean isActionActive(ILastingAction action) {
        return actionTimer[getIdFromAction(action)] > 0;
    }

    @Override
    public boolean isActionActive(ResourceLocation id) {
        IAction action = VampirismRegistries.ACTIONS.getValue(id);
        if (action != null) {
            return isActionActive((ILastingAction) action);
        } else {
            VampirismMod.log.w(TAG, "Action with id %s is not registered");
            return false;
        }

    }

    @Override
    public boolean isActionUnlocked(IAction action) {
        return unlockedActions.contains(action);
    }

    /**
     * Should only be called by the corresponding Capability instance
     *
     * @param nbt
     */
    public void loadFromNbt(NBTTagCompound nbt) {
        NBTTagCompound actions = nbt.getCompoundTag("actions");
        for (String key : actions.getKeySet()) {
            IAction action = VampirismRegistries.ACTIONS.getValue(new ResourceLocation(key));
            if (action == null) {
                VampirismMod.log.w(TAG, "Did not find action with key %s", key);
            } else {
                actionTimer[getIdFromAction(action)] = actions.getInteger(key);
            }
        }

    }

    /**
     * Should only be called by the corresponding Capability instance
     */
    public void onActionsReactivated() {
        if (!player.isRemote()) {
            for (int i = 0; i < actionTimer.length; i++) {
                if (actionTimer[i] > 0) {
                    ((ILastingAction) getActionFromId(i)).onReActivated(player);
                }
            }
        }

    }

    /**
     * Should only be called by the corresponding Capability instance
     *
     * @param nbt
     */
    public void readUpdateFromServer(NBTTagCompound nbt) {
        if (nbt.hasKey("action_timers")) {
            NBTTagCompound actions = nbt.getCompoundTag("action_timers");

            for (IAction action : unlockedActions) {
                String regname = action.getRegistryName().toString();
                int id = getIdFromAction(action);
                int oldValue = actionTimer[id];
                int newValue = 0;
                if (actions.hasKey(regname)) {
                    newValue = actions.getInteger(regname);
                }
                actionTimer[id] = newValue;
                if (newValue > 0 && oldValue <= 0) {
                    ((ILastingAction) getActionFromId(id)).onActivatedClient(player);
                } else if (newValue <= 0 && oldValue > 0) {
                    ((ILastingAction) getActionFromId(id)).onDeactivated(player);//Called here if the skill is deactivated
                }
            }
        }
    }

    @Override
    public void relockActions(Collection<IAction> actions) {
        unlockedActions.removeAll(actions);
        for (IAction action : actions) {
            if (action instanceof ILastingAction && isActionActive((ILastingAction) action)) {
                toggleAction(action);
            }
        }
    }

    @Override
    public void resetTimers() {
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] > 0) {
                ((ILastingAction) getActionFromId(i)).onDeactivated(player);
            }
            actionTimer[i] = 0;
        }
        dirty = true;
    }

    /**
     * Saves action timings to nbt
     * Should only be called by the corresponding Capability instance
     *
     * @param nbt
     */
    public void saveToNbt(NBTTagCompound nbt) {
        NBTTagCompound actions = new NBTTagCompound();
        for (int i = 0; i < actionTimer.length; i++) {
            IAction a = getActionFromId(i);
            String key = VampirismRegistries.ACTIONS.getKey(a).toString();
            actions.setInteger(key, actionTimer[i]);
        }
        nbt.setTag("actions", actions);
    }

    @Override
    public IAction.PERM toggleAction(IAction action) {

        int id = getIdFromAction(action);
        int t = actionTimer[id];
        if (t > 0) {
            int cooldown = action.getCooldown();
            if (((ILastingAction) action).allowReducedCooldown()) {
                cooldown -= t;
            }
            actionTimer[id] = Math.min(-cooldown, 0);
            ((ILastingAction) action).onDeactivated(player);
            dirty = true;
            return IAction.PERM.ALLOWED;
        } else if (t == 0) {
            if (!isActionUnlocked(action)) return IAction.PERM.NOT_UNLOCKED;
            IAction.PERM r = action.canUse(player);
            if (r == IAction.PERM.ALLOWED) {
                if (action.onActivated(player)) {
                    if (action instanceof ILastingAction) {
                        actionTimer[id] = ((ILastingAction) action).getDuration(player.getLevel());
                    } else {
                        actionTimer[id] = -action.getCooldown();
                    }
                    dirty = true;
                }

                return IAction.PERM.ALLOWED;
            } else {
                return r;
            }
        } else {
            return IAction.PERM.COOLDOWN;
        }

    }

    @Override
    public void unlockActions(Collection<IAction> actions) {
        unlockedActions.addAll(actions);
    }

    /**
     * Update the actions
     * Should only be called by the corresponding Capability instance
     *
     * @return If a sync is recommend, only relevant on server side
     */
    public boolean updateActions() {
        for (int i = 0; i < actionTimer.length; i++) {
            int t = actionTimer[i];
            if (t != 0) {
                if (t < 0) {
                    actionTimer[i]++;
                } else {
                    actionTimer[i]--;
                    ILastingAction skill = (ILastingAction) getActionFromId(i);
                    if (t == 1) {
                        skill.onDeactivated(player);//Called here if the skill runs out.
                        actionTimer[i] = -skill.getCooldown();
                        dirty = true;
                    } else {
                        if (skill.onUpdate(player)) {
                            actionTimer[i] = 1;
                        }
                    }

                }
            }
        }
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    /**
     * Writes an update for the client.
     * Should only be called by the corresponding Capability instance
     *
     * @param nbt
     */
    public void writeUpdateForClient(NBTTagCompound nbt) {
        NBTTagCompound actions = new NBTTagCompound();
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] != 0) {
                actions.setInteger(getActionFromId(i).getRegistryName().toString(), actionTimer[i]);
            }
        }
        nbt.setTag("action_timers", actions);
    }

    /**
     * INTERNAL USE ONLY
     *
     * @return The skill currently mapped to this id. Could be different after a restart
     */
    private IAction getActionFromId(int id) {
        return actionIdMap.get(id);
    }

    /**
     * Throws an exception if action is not registered
     * <p>
     * INTERNAL USE ONLY
     *
     * @param action
     * @return The id currently mapped to this action. Could be different after a restart.
     */
    private int getIdFromAction(IAction action) {
        Integer i = actionIdMap.inverse().get(action);
        if (i == null) {
            throw new ActionNotRegisteredException(action);
        }
        return i;
    }

    /**
     * Is thrown if an unregistered action is used
     */
    public static class ActionNotRegisteredException extends RuntimeException {
        public ActionNotRegisteredException(String name) {
            super("Action " + name + " is not registered. You cannot use it otherwise");
        }

        public ActionNotRegisteredException(IAction action) {
            this(action.toString());
        }
    }
}
