package de.teamlapen.vampirism.player.actions;

import com.google.common.collect.ImmutableBiMap;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import net.minecraft.nbt.NBTTagCompound;

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

    private final ImmutableBiMap<Integer, IAction<T>> actionIdMap;
    private final List<IAction<T>> unlockedActions = new ArrayList<>();
    private boolean dirty = false;

    public ActionHandler(T player) {
        this.player = player;
        ImmutableBiMap<String, IAction<T>> actionMap = VampirismAPI.actionRegistry().getActionMapForFaction(player.getFaction());
        ImmutableBiMap.Builder<Integer, IAction<T>> idBuilder = ImmutableBiMap.builder();
        int i = 0;
        for (IAction<T> action : actionMap.values()) {
            idBuilder.put(i++, action);
        }
        actionIdMap = idBuilder.build();
        this.actionTimer = new int[actionMap.size()];
    }

    public void deactivateAllActions() {
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] > 0) {
                actionTimer[i] = -getActionFromId(i).getCooldown();
                ((ILastingAction) getActionFromId(i)).onDeactivated(player);

            }
        }
    }

    /**
     * @return The skill currently mapped to this id. Could be different after a restart
     */
    public IAction<T> getActionFromId(int id) {
        return actionIdMap.get(id);
    }



    @Override
    public List<IAction<T>> getAvailableActions() {
        ArrayList<IAction<T>> actions = new ArrayList<>();
        for (IAction<T> action : unlockedActions) {
            if (action.canUse(player) == IAction.PERM.ALLOWED) {
                actions.add(action);
            }
        }

        return actions;
    }

    /**
     * Throws an exception if action is not registered
     *
     * @param action
     * @return The id currently mapped to this action. Could be different after a restart.
     */
    public int getIdFromAction(IAction action) {
        Integer i = actionIdMap.inverse().get(action);
        if (i == null) {
            throw new ActionNotRegisteredException(action);
        }
        return i;
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
    public boolean isActionActive(String id) {
        IAction skill = VampirismAPI.actionRegistry().getActionFromKey(player.getFaction(), id);
        if (skill != null) {
            return isActionActive((ILastingAction) skill);
        } else {
            VampirismMod.log.w(TAG, "Skill with id %s is not registered");
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
        if (actions != null) {
            for (String key : actions.getKeySet()) {
                IAction action = VampirismAPI.actionRegistry().getActionFromKey(player.getFaction(), key);
                if (action == null) {
                    VampirismMod.log.w(TAG, "Did not find action with key %s", key);
                } else {
                    actionTimer[getIdFromAction(action)] = actions.getInteger(key);
                }
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
            int[] updated = nbt.getIntArray("action_timers");
            for (int i = 0; i < actionTimer.length; i++) {
                int old = actionTimer[i];
                actionTimer[i] = updated[i];
                if (updated[i] > 0 && old <= 0) {
                    ((ILastingAction) getActionFromId(i)).onActivatedClient(player);
                } else if (updated[i] <= 0 && old > 0) {
                    ((ILastingAction) getActionFromId(i)).onDeactivated(player);//Called here if the skill is deactivated
                }

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
            String key = VampirismAPI.actionRegistry().getKeyFromAction(a);
            actions.setInteger(key, actionTimer[i]);
        }
        nbt.setTag("actions", actions);
    }

    @Override
    public IAction.PERM toggleAction(IAction action) {

        int id = getIdFromAction(action);
        int t = actionTimer[id];
        if (t > 0) {
            actionTimer[id] = Math.min((-action.getCooldown()) + t, 0);
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
    public void unlockActions(Collection<IAction<T>> actions) {
        unlockedActions.addAll(actions);
    }

    @Override
    public void ununlockActions(Collection<IAction<T>> actions) {
        unlockedActions.removeAll(actions);
        for (IAction action : actions) {
            if (action instanceof ILastingAction && isActionActive((ILastingAction) action)) {
                toggleAction(action);
            }
        }
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
        nbt.setIntArray("action_timers", actionTimer);
    }

    /**
     * Is thrown if an unregistered skill is used
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
