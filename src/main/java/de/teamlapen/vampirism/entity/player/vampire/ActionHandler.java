package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.*;
import de.teamlapen.vampirism.entity.player.vampire.actions.*;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Handles skill for vampire players
 */
public class ActionHandler implements IActionHandler {
    private final static String TAG = "ActionHandler";
    public static FreezeAction freezeAction;
    public static InvisibilityAction invisibilityAction;
    public static RegenAction regenAction;
    public static TeleportAction teleportAction;
    public static VampireRageAction rageAction;
    public static BatAction batAction;

    public static void registerDefaultActions() {
        IActionRegistry registry = VampirismAPI.actionRegistry();
        freezeAction = registry.registerAction(new FreezeAction(), "freeze");
        invisibilityAction = registry.registerAction(new InvisibilityAction(), "invisible");
        regenAction = registry.registerAction(new RegenAction(), "regen");
        teleportAction = registry.registerAction(new TeleportAction(), "teleport");
        rageAction = registry.registerAction(new VampireRageAction(), "rage");
        batAction = registry.registerAction(new BatAction(), "bat");
    }
    /**
     * Saves timers for skill ids
     * Values:
     * 0 - Inactive
     * <0 - Cooldown
     * >0 - Active {@link ILastingVampireAction}
     */
    private final int[] actionTimer;
    private final IVampirePlayer vampire;
    private boolean dirty = false;

    ActionHandler(IVampirePlayer player) {
        vampire = player;
        this.actionTimer = new int[VampirismAPI.actionRegistry().getActionCount()];
    }

    public void deactivateAllActions() {
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] > 0) {
                actionTimer[i] = -((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i).getCooldown();
                ((ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i)).onDeactivated(vampire);

            }
        }
    }

    @Override
    public List<IVampireAction> getAvailableActions() {
        return VampirismAPI.actionRegistry().getAvailableActions(vampire);
    }

    @Override
    public float getPercentageForAction(IVampireAction action) {
        Integer id = ((ActionRegistry) VampirismAPI.actionRegistry()).getIdFromAction(action);
        int i = actionTimer[id];
        if (i == 0) return 0F;
        if (i > 0) return i / ((float) ((ILastingVampireAction) action).getDuration(vampire.getLevel()));
        return i / (float) action.getCooldown();
    }

    @Override
    public boolean isActionActive(ILastingVampireAction action) {
        return actionTimer[((ActionRegistry) VampirismAPI.actionRegistry()).getIdFromAction(action)] > 0;
    }

    @Override
    public boolean isActionActive(String id) {
        IVampireAction skill = VampirismAPI.actionRegistry().getActionFromKey(id);
        if (skill != null) {
            return isActionActive((ILastingVampireAction) skill);
        } else {
            VampirismMod.log.w(TAG, "Skill with id %s is not registered");
            return false;
        }

    }

    @Override
    public void resetTimers() {
        for (int i = 0; i < actionTimer.length; i++) {
            if (actionTimer[i] > 0) {
                ((ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i)).onDeactivated(vampire);
            }
            actionTimer[i] = 0;
        }
        dirty = true;
    }

    @Override
    public IVampireAction.PERM toggleAction(IVampireAction action) {

        int id = ((ActionRegistry) VampirismAPI.actionRegistry()).getIdFromAction(action);
        int t = actionTimer[id];
        VampirismMod.log.t("Toggling skill %s with id %d at current time %d", action, id, t);
        if (t > 0) {
            actionTimer[id] = Math.min((-action.getCooldown()) + t, 0);
            ((ILastingVampireAction) action).onDeactivated(vampire);
            dirty = true;
            return IVampireAction.PERM.ALLOWED;
        } else if (t == 0) {
            IVampireAction.PERM r = action.canUse(vampire);
            if (r == IVampireAction.PERM.ALLOWED) {
                if (action.onActivated(vampire)) {
                    if (action instanceof ILastingVampireAction) {
                        actionTimer[id] = ((ILastingVampireAction) action).getDuration(vampire.getLevel());
                    } else {
                        actionTimer[id] = -action.getCooldown();
                    }
                    dirty = true;
                }

                return IVampireAction.PERM.ALLOWED;
            } else {
                return r;
            }
        } else {
            return IVampireAction.PERM.COOLDOWN;
        }

    }

    void loadFromNbt(NBTTagCompound nbt) {
        NBTTagCompound actions = nbt.getCompoundTag("actions");
        if (actions != null) {
            for (String key : actions.getKeySet()) {
                IVampireAction action = VampirismAPI.actionRegistry().getActionFromKey(key);
                if (action == null) {
                    VampirismMod.log.w(TAG, "Did not find action with key %s", key);
                } else {
                    actionTimer[((ActionRegistry) VampirismAPI.actionRegistry()).getIdFromAction(action)] = actions.getInteger(key);
                }
            }
        }
    }

    void onActionsReactivated() {
        if (!vampire.isRemote()) {
            for (int i = 0; i < actionTimer.length; i++) {
                if (actionTimer[i] > 0) {
                    ((ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i)).onReActivated(vampire);
                }
            }
        }

    }

    void readUpdateFromServer(NBTTagCompound nbt) {

        if (nbt.hasKey("action_timers")) {
            int[] updated = nbt.getIntArray("action_timers");
            for (int i = 0; i < actionTimer.length; i++) {
                int old = actionTimer[i];
                actionTimer[i] = updated[i];
                if (updated[i] > 0 && old <= 0) {
                    ((ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i)).onActivatedClient(vampire);
                } else if (updated[i] <= 0 && old > 0) {
                    ((ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i)).onDeactivated(vampire);//Called here if the skill is deactivated
                }

            }
        }
    }

    void saveToNbt(NBTTagCompound nbt) {
        NBTTagCompound skills = new NBTTagCompound();
        for (int i = 0; i < actionTimer.length; i++) {
            IVampireAction s = ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i);
            String key = VampirismAPI.actionRegistry().getKeyFromAction(s);
            skills.setInteger(key, actionTimer[i]);
        }
        nbt.setTag("actions", skills);
    }

    /**
     * Update the actions
     *
     * @return If a sync is recommend, only relevant on server side
     */
    boolean updateActions() {
        for (int i = 0; i < actionTimer.length; i++) {
            int t = actionTimer[i];
            if (t != 0) {
                if (t < 0) {
                    actionTimer[i]++;
                } else {
                    actionTimer[i]--;
                    ILastingVampireAction skill = (ILastingVampireAction) ((ActionRegistry) VampirismAPI.actionRegistry()).getActionFromId(i);
                    if (t == 1) {
                        skill.onDeactivated(vampire);//Called here if the skill runs out.
                        dirty = true;
                    } else {
                        if (skill.onUpdate(vampire)) {
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

    void writeUpdateForClient(NBTTagCompound nbt) {
        nbt.setIntArray("action_timers", actionTimer);
    }
}
