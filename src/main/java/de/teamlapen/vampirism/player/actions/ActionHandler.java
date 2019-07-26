package de.teamlapen.vampirism.player.actions;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Handles actions for vampire players
 *
 * This uses fastutil maps to store the cooldown/active timers for the individual action.
 * Actions are identified by their registry name (ResourceLocation) in the maps.
 *
 * Probably not the fastest or cleanest approach, but I did not find the perfect solution yet.
 */
public class ActionHandler<T extends IFactionPlayer> implements IActionHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger(ActionHandler.class);


    /**
     * Holds any action in cooldown state. Maps it to the corresponding cooldown timer
     * Actions represented by any key in this map have to be registered..
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     *
     * Keys should be mutually exclusive with {@link #activeTimers}
     *
     */
    private final Object2IntMap<ResourceLocation> cooldownTimers;
    /**
     * Holds any active action. Maps it to the corresponding action timer.
     * Actions represented by any key in this map have to be registered and must implement ILastingAction.
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #cooldownTimers}
     */
    private final Object2IntMap<ResourceLocation> activeTimers;

    private final T player;

    private final List<IAction> unlockedActions = new ArrayList<>();

    /**
     * If active/cooldown timers have changed and should be synced
     */
    private boolean dirty = false;

    public ActionHandler(T player) {
        this.player = player;
        List<IAction> actions = VampirismAPI.actionManager().getActionsForFaction(player.getFaction());

        cooldownTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        activeTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);

    }

    public void deactivateAllActions() {
        for (ResourceLocation r : activeTimers.keySet()) {
            ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(r);
            assert action != null;
            int cooldown = action.getCooldown();
            cooldownTimers.put(r, cooldown);
            action.onDeactivated(player);
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
    public ImmutableList<IAction> getUnlockedActions() {
        return ImmutableList.copyOf(unlockedActions);
    }

    @Override
    public float getPercentageForAction(@Nonnull IAction action) {
        if (activeTimers.containsKey(action.getRegistryName())) {
            return activeTimers.get(action.getRegistryName()) / ((float) ((ILastingAction) action).getDuration(player.getLevel()));
        }
        if (cooldownTimers.containsKey(action.getRegistryName())) {
            return -cooldownTimers.get(action.getRegistryName()) / (float) action.getCooldown();
        }
        return 0f;
    }

    @Override
    public boolean isActionActive(@Nonnull ILastingAction action) {
        return activeTimers.containsKey(action.getRegistryName());
    }

    @Override
    public boolean isActionActive(ResourceLocation id) {
        return activeTimers.containsKey(id);
    }

    @Override
    public boolean isActionUnlocked(IAction action) {
        return unlockedActions.contains(action);
    }

    /**
     * Should only be called by the corresponding Capability instance
     **/
    public void loadFromNbt(CompoundNBT nbt) {
        //If loading from save we want to clear everything beforehand.
        //NBT only contains actions that are active/cooldown
        activeTimers.clear();
        cooldownTimers.clear();
        if (nbt.contains("actions_active")) loadTimerMapFromNBT(nbt.getCompound("actions_active"), activeTimers);
        if (nbt.contains("actions_cooldown")) loadTimerMapFromNBT(nbt.getCompound("actions_cooldown"), cooldownTimers);
    }

    /**
     * Should only be called by the corresponding Capability instance
     */
    public void onActionsReactivated() {
        if (!player.isRemote()) {
            for (ResourceLocation id : activeTimers.keySet()) {
                ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(id);
                assert action != null;
                action.onReActivated(player);
            }
        }

    }

    /**
     * Should only be called by the corresponding Capability instance
     *
     * Attention: nbt is modified in the process
     **/
    public void readUpdateFromServer(CompoundNBT nbt) {
        /*
         * This happens client side
         * We want to:
         * 1) Disable and remove all actions that are present in the activeMap, but not in the synced nbt. We also need to add them to the cooldown map.
         * 2) Add and activate all actions that are present in the synced nbt, but not in the active map.
         * 3) Update the timing for any action that is present in both activeMap and nbt.
         * 4) Override the cooldown map with the server update
         *
         * To accomplish 1-3 we first iterate over the active actions in the local map and check if they have a updated value in the nbt or if they have been disabled.
         * Any locally active action is removed from the NBT so after the iteration only actions that are not locally active should be present in the map. Therefore any remaining actions are activated.
         *
         */
        if (nbt.contains("actions_active")) {
            CompoundNBT active = nbt.getCompound("actions_active");
            for (ObjectIterator<Object2IntMap.Entry<ResourceLocation>> it = activeTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
                Object2IntMap.Entry<ResourceLocation> client_active = it.next();
                String key = client_active.getKey().toString();
                if (active.contains(key)) {
                    client_active.setValue(active.getInt(key));
                    nbt.remove(key);
                } else {
                    ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(client_active.getKey());
                    assert action != null;
                    action.onDeactivated(player);
                    it.remove();
                }
            }
            for (String key : active.keySet()) {
                ResourceLocation id = new ResourceLocation(key);
                ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(id);
                if (action == null) {
                    LOGGER.error("Action %s is not available client side", key);
                } else {
                    action.onActivatedClient(player);
                    activeTimers.put(id, active.getInt(key));
                }
            }

        }

        if (nbt.contains("actions_cooldown")) {
            cooldownTimers.clear();
            loadTimerMapFromNBT(nbt.getCompound("actions_cooldown"), cooldownTimers);
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
        for (ResourceLocation id : activeTimers.keySet()) {
            ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(id);
            assert action != null;
            action.onDeactivated(player);
        }
        activeTimers.clear();
        cooldownTimers.clear();
        dirty = true;
    }

    /**
     * Saves action timings to nbt
     * Should only be called by the corresponding Capability instance
     *
     * @param nbt
     */
    public void saveToNbt(CompoundNBT nbt) {

        nbt.put("actions_active", writeTimersToNBT(activeTimers.object2IntEntrySet()));
        nbt.put("actions_cooldown", writeTimersToNBT(cooldownTimers.object2IntEntrySet()));
    }

    @Override
    public IAction.PERM toggleAction(IAction action) {

        ResourceLocation id = action.getRegistryName();
        if (activeTimers.containsKey(id)) {
            int cooldown = action.getCooldown();
            if (((ILastingAction) action).allowReducedCooldown()) {
                cooldown -= activeTimers.get(id);
            }
            ((ILastingAction<T>) action).onDeactivated(player);
            activeTimers.remove(id);
            cooldownTimers.put(id, Math.max(cooldown, 1));//Entries should to be at least 1

            dirty = true;
            return IAction.PERM.ALLOWED;
        } else if (cooldownTimers.containsKey(id)) {
            return IAction.PERM.COOLDOWN;
        } else{
            if (!isActionUnlocked(action)) return IAction.PERM.NOT_UNLOCKED;
            IAction.PERM r = action.canUse(player);
            if (r == IAction.PERM.ALLOWED) {
                if (action.onActivated(player)) {
                    if (action instanceof ILastingAction) {
                        activeTimers.put(id, ((ILastingAction) action).getDuration(player.getLevel()));
                    } else {
                        cooldownTimers.put(id, action.getCooldown());
                    }
                    dirty = true;
                }

                return IAction.PERM.ALLOWED;
            } else {
                return r;
            }
        }

    }

    @Override
    public void unlockActions(Collection<IAction> actions) {
        for (IAction action : actions) {
            if (!ModRegistries.ACTIONS.containsValue(action)) {
                throw new ActionNotRegisteredException(action);
            }
        }
        unlockedActions.addAll(actions);
    }

    /**
     * Update the actions
     * Should only be called by the corresponding Capability instance
     *
     * @return If a sync is recommend, only relevant on server side
     */
    public boolean updateActions() {
        //First update cooldown timers so active actions that become deactivated are not ticked.
        for (Iterator<Object2IntMap.Entry<ResourceLocation>> it = cooldownTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
            Object2IntMap.Entry<ResourceLocation> entry = it.next();
            int value = entry.getIntValue();
            if (value <= 1) { //<= Just in case we have missed something
                it.remove();
            } else {
                entry.setValue(value - 1);
            }
        }

        for (Iterator<Object2IntMap.Entry<ResourceLocation>> it = activeTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
            Object2IntMap.Entry<ResourceLocation> entry = it.next();
            int newtimer = entry.getIntValue() - 1;
            ILastingAction<T> action = (ILastingAction<T>) ModRegistries.ACTIONS.getValue(entry.getKey());
            assert action != null;
            if (newtimer == 0) {
                action.onDeactivated(player);
                cooldownTimers.put(entry.getKey(), action.getCooldown());
                it.remove();//Do not access entry after this
                dirty = true;
            } else {
                if (action.onUpdate(player)) {
                    entry.setValue(1); //Value of means they are deactivated next tick and onUpdate is not called again
                } else {
                    entry.setValue(newtimer);
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
    public void writeUpdateForClient(CompoundNBT nbt) {
        nbt.put("actions_active", writeTimersToNBT(activeTimers.object2IntEntrySet()));
        nbt.put("actions_cooldown", writeTimersToNBT(cooldownTimers.object2IntEntrySet()));
    }

    private void loadTimerMapFromNBT(CompoundNBT nbt, Object2IntMap<ResourceLocation> map) {
        for (String key : nbt.keySet()) {
            ResourceLocation id = new ResourceLocation(key);
            IAction action = ModRegistries.ACTIONS.getValue(id);
            if (action == null) {
                LOGGER.warn("Did not find action with key %s", key);
            } else {
                map.put(id, nbt.getInt(key));
            }
        }
    }

    private CompoundNBT writeTimersToNBT(ObjectSet<Object2IntMap.Entry<ResourceLocation>> set) {
        CompoundNBT nbt = new CompoundNBT();
        for (Object2IntMap.Entry<ResourceLocation> entry : set) {
            nbt.putInt(entry.getKey().toString(), entry.getIntValue());
        }
        return nbt;
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
