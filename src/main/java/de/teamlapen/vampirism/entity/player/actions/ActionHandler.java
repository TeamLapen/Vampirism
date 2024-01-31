package de.teamlapen.vampirism.entity.player.actions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.lib.lib.storage.ISyncableSaveData;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.event.ActionEvent;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.RegUtil;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles actions for vampire players
 * <p>
 * This uses fastutil maps to store the cooldown/active timers for the individual action.
 * Actions are identified by their registry name (ResourceLocation) in the maps.
 * <p>
 * Probably not the fastest or cleanest approach, but I did not find the perfect solution yet.
 */
public class ActionHandler<T extends IFactionPlayer<T>> implements IActionHandler<T>, ISyncableSaveData {
    private static final String NBT_KEY = "action_handler";
    private final static Logger LOGGER = LogManager.getLogger(ActionHandler.class);


    /**
     * Holds any action in cooldown state. Maps it to the corresponding cooldown timer
     * Actions represented by any key in this map have to be registerer…
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #activeTimers}
     */
    private final @NotNull Object2IntMap<ResourceLocation> cooldownTimers;
    /**
     * Holds any active action. Maps it to the corresponding action timer.
     * Actions represented by any key in this map have to be registered and must implement ILastingAction.
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #cooldownTimers}
     */
    private final @NotNull Object2IntMap<ResourceLocation> activeTimers;
    /**
     * Stores the expected cooldown of an action after it was activated.
     * This is used to check the action cooldown instead of {@link de.teamlapen.vampirism.api.entity.player.actions.IAction#getCooldown(IFactionPlayer)} as the cooldown might be modified before activation.
     * The values stored here are only changed when the cooldown is added, it is not decremented like the map for cooldown timers, but removed when the action's cooldown is over.
     */
    private final @NotNull Object2IntMap<ResourceLocation> expectedCooldownTimes;
    /**
     * Stores the expected duration of an action after it was activated.
     * This is used to check the action duration instead of {@link de.teamlapen.vampirism.api.entity.player.actions.ILastingAction#getDuration(IFactionPlayer)} (IFactionPlayer)} as the duration might be modified before activation.
     * The values stored here are only changed when the duration is added, it is not decremented like the map for duration timers, but removed when the action's duration is over.
     */
    private final @NotNull Object2IntMap<ResourceLocation> expectedDurations;

    private final T player;

    private final List<IAction<T>> unlockedActions = new ArrayList<>();

    /**
     * If active/cooldown timers have changed and should be synced
     */
    private boolean dirty = false;

    public ActionHandler(@NotNull T player) {
        this.player = player;
        List<IAction<T>> actions = VampirismAPI.actionManager().getActionsForFaction(player.getFaction());

        cooldownTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        activeTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedCooldownTimes = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedDurations = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
    }

    public void deactivateAllActions() {
        for (ResourceLocation r : activeTimers.keySet()) {
            @SuppressWarnings("unchecked")
            ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(r);
            assert action != null;
            deactivateAction(action, false, true);
        }
        this.activeTimers.clear();
        dirty = true;
    }

    @Override
    public void extendActionTimer(@NotNull ILastingAction<T> action, int duration) {
        int i = activeTimers.getOrDefault(RegUtil.id(action), -1);
        if (i > 0) {
            activeTimers.put(RegUtil.id(action), i + duration);
        }
    }

    @Override
    public @NotNull List<IAction<T>> getAvailableActions() {
        ArrayList<IAction<T>> actions = new ArrayList<>();
        for (IAction<T> action : unlockedActions) {
            if (action.canUse(player) == IAction.PERM.ALLOWED) {
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    public float getPercentageForAction(@NotNull IAction<T> action) {
        ResourceLocation id = RegUtil.id(action);
        if (activeTimers.containsKey(id)) {
            return activeTimers.getInt(id) / ((float) expectedDurations.getInt(id));
        }
        if (cooldownTimers.containsKey(id)) {
            return -cooldownTimers.getInt(id) / (float) expectedCooldownTimes.getInt(id);
        }
        return 0f;
    }

    @Override
    public @NotNull ImmutableList<IAction<T>> getUnlockedActions() {
        return ImmutableList.copyOf(unlockedActions);
    }

    @Override
    public boolean isActionActive(@NotNull ILastingAction<T> action) {
        return activeTimers.containsKey(RegUtil.id(action));
    }

    @Override
    public boolean isActionActive(ResourceLocation id) {
        return activeTimers.containsKey(id);
    }

    @Override
    public boolean isActionOnCooldown(IAction<T> action) {
        return cooldownTimers.containsKey(RegUtil.id(action));
    }

    @Override
    public boolean isActionUnlocked(IAction<T> action) {
        return unlockedActions.contains(action);
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        //If loading from save we want to clear everything beforehand.
        //NBT only contains actions that are active/cooldown
        activeTimers.clear();
        cooldownTimers.clear();
        expectedCooldownTimes.clear();
        expectedDurations.clear();
        if (nbt.contains("actions_active")) loadTimerMapFromNBT(nbt.getCompound("actions_active"), activeTimers);
        if (nbt.contains("actions_cooldown")) loadTimerMapFromNBT(nbt.getCompound("actions_cooldown"), cooldownTimers);
        if (nbt.contains("actions_cooldown_expected")) loadTimerMapFromNBT(nbt.getCompound("actions_cooldown_expected"), expectedCooldownTimes);
        if (nbt.contains("actions_duration_expected")) loadTimerMapFromNBT(nbt.getCompound("actions_duration_expected"), expectedDurations);
    }

    /**
     * Should only be called by the corresponding Capability instance
     */
    public void onActionsReactivated() {
        if (!player.isRemote()) {
            for (ResourceLocation id : activeTimers.keySet()) {
                @SuppressWarnings("unchecked")
                ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
                assert action != null;
                action.onReActivated(player);
            }
        }

    }

    /**
     * Should only be called by the corresponding Capability instance
     * <p>
     * Attention: nbt is modified in the process
     **/
    @Override
    public void deserializeUpdateNBT(@NotNull CompoundTag nbt) {
        /*
         * This happens client side
         * We want to:
         * 1) Disable and remove all actions that are present in the activeMap, but not in the synced nbt. We also need to add them to the cooldown map.
         * 2) Add and activate all actions that are present in the synced nbt, but not in the active map.
         * 3) Update the timing for any action that is present in both activeMap and nbt.
         * 4) Override the cooldown map with the server update
         *
         * To accomplish 1-3 we first iterate over the active actions in the local map and check if they have an updated value in the nbt or if they have been disabled.
         * Any locally active action is removed from the NBT so after the iteration only actions that are not locally active should be present in the map. Therefore, any remaining actions are activated.
         *
         */
        if (nbt.contains("actions_active", Tag.TAG_COMPOUND)) {
            CompoundTag active = nbt.getCompound("actions_active");
            List<ResourceLocation> toRemove = new ArrayList<>();
            for (Object2IntMap.Entry<ResourceLocation> client_active : activeTimers.object2IntEntrySet()) {
                String key = client_active.getKey().toString();
                if (active.contains(key)) {
                    client_active.setValue(active.getInt(key));
                    nbt.remove(key);
                } else {
                    toRemove.add(client_active.getKey());
                }
            }
            toRemove.forEach(id -> {
                @SuppressWarnings("unchecked")
                ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
                deactivateAction(action);
            });
            for (String key : active.getAllKeys()) {
                ResourceLocation id = new ResourceLocation(key);
                @SuppressWarnings("unchecked")
                ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
                if (action == null) {
                    LOGGER.error("Action {} is not available client side", key);
                } else {
                    action.onActivatedClient(player);
                    activeTimers.put(id, active.getInt(key));
                }
            }

        }

        if (nbt.contains("actions_cooldown", Tag.TAG_COMPOUND)) {
            cooldownTimers.clear();
            loadTimerMapFromNBT(nbt.getCompound("actions_cooldown"), cooldownTimers);
        }
        if (nbt.contains("actions_cooldown_expected", Tag.TAG_COMPOUND)) {
            expectedCooldownTimes.clear();
            loadTimerMapFromNBT(nbt.getCompound("actions_cooldown_expected"), expectedCooldownTimes);
        }
        if (nbt.contains("actions_duration_expected", Tag.TAG_COMPOUND)) {
            expectedDurations.clear();
            loadTimerMapFromNBT(nbt.getCompound("actions_duration_expected"), expectedDurations);
        }
    }

    @Override
    public void relockActions(@NotNull Collection<IAction<T>> actions) {
        unlockedActions.removeAll(actions);
        for (IAction<T> action : actions) {
            if (action instanceof ILastingAction<T> lastingAction) {
                deactivateAction(lastingAction);
            }
        }
    }

    @Override
    public void resetTimers() {
        for (ResourceLocation id : activeTimers.keySet()) {
            @SuppressWarnings("unchecked")
            ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
            deactivateAction(action, true);
        }
        activeTimers.clear();
        cooldownTimers.clear();
        expectedCooldownTimes.clear();
        expectedDurations.clear();
        dirty = true;
    }

    @Override
    public void resetTimer(@NotNull IAction<T> action) {
        ResourceLocation id = RegUtil.id(action);
        if(action instanceof ILastingAction<T> lastingAction) {
            deactivateAction(lastingAction, true);
        }
        cooldownTimers.removeInt(id);
        expectedCooldownTimes.removeInt(id);
        dirty = true;

    }

    @Override
    public @NotNull CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("actions_active", writeTimersToNBT(activeTimers.object2IntEntrySet()));
        tag.put("actions_cooldown", writeTimersToNBT(cooldownTimers.object2IntEntrySet()));
        tag.put("actions_cooldown_expected", writeTimersToNBT(expectedCooldownTimes.object2IntEntrySet()));
        tag.put("actions_duration_expected", writeTimersToNBT(expectedDurations.object2IntEntrySet()));
        return tag;
    }

    /**
     * After server receives action toggle packet this is called.
     * Actions can be cancelled, have their cooldown changed, or if a lasting action their duration changed as well through {@link de.teamlapen.vampirism.api.event.ActionEvent.ActionActivatedEvent}
     * @param action  Action being toggled
     * @param context Context holding Block/Entity the player was looking at when activating if any
     *
     */
    @Override
    public IAction.PERM toggleAction(@NotNull IAction<T> action, IAction.ActivationContext context) {
        ResourceLocation id = RegUtil.id(action);
        if (activeTimers.containsKey(id)) {
            deactivateAction((ILastingAction<T>) action);
            dirty = true;
            return IAction.PERM.ALLOWED;
        } else if (cooldownTimers.containsKey(id)) {
            return IAction.PERM.COOLDOWN;
        } else {
            if (this.player.getRepresentingPlayer().isSpectator()) return IAction.PERM.DISALLOWED;
            if (!isActionUnlocked(action)) return IAction.PERM.NOT_UNLOCKED;
            if (!isActionAllowedPermission(action)) return IAction.PERM.PERMISSION_DISALLOWED;

            IAction.PERM r = action.canUse(player);
            if (r == IAction.PERM.ALLOWED) {
                /*
                 * Only lasting actions have a cooldown, so regular actions will return a duration of -1.
                 */
                int duration = action instanceof ILastingAction<T> lasting ? lasting.getDuration(player) : -1;
                ActionEvent.ActionActivatedEvent activationEvent = VampirismEventFactory.fireActionActivatedEvent(player, action, action.getCooldown(player), duration);
                if(activationEvent.isCanceled()) return IAction.PERM.DISALLOWED;
                if (action.onActivated(player, context)) {
                    player.getRepresentingPlayer().awardStat(ModStats.ACTION_USED.get().get(action));
                    //Even though lasting actions do not activate their cooldown until they deactivate
                    //we probably want to keep this here so that they are edited by one event.
                    int cooldown = activationEvent.getCooldown();
                    expectedCooldownTimes.put(id, cooldown);
                    if (action instanceof ILastingAction) {
                        expectedDurations.put(id, activationEvent.getDuration());
                        duration = activationEvent.getDuration();
                        activeTimers.put(id, duration);
                    } else {
                        cooldownTimers.put(id, cooldown);
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
    public void deactivateAction(@NotNull ILastingAction<T> action) {
        deactivateAction(action, false);
    }
    public void deactivateAction(@NotNull ILastingAction<T> action, boolean ignoreCooldown) {
        deactivateAction(action, false, false);
    }

    /**
     * Lasting actions are deactivated here, which fires the {@link de.teamlapen.vampirism.api.event.ActionEvent.ActionDeactivatedEvent}
     * @param action - The lasting action being deactivated
     * @param ignoreCooldown - Whether the cooldown is ignored for the action
     * @param fullCooldown - Whether the lasting action should get the full or reduced cooldown
     */
    public void deactivateAction(@NotNull ILastingAction<T> action, boolean ignoreCooldown, boolean fullCooldown) {
        ResourceLocation id = RegUtil.id(action);
        if (activeTimers.containsKey(id)) {
            int cooldown = expectedCooldownTimes.getInt(id);
            int leftTime = activeTimers.getInt(id);
            int duration = expectedDurations.getInt(id);
            cooldown = VampirismEventFactory.fireActionDeactivatedEvent(player, action, leftTime, cooldown);
            if(!ignoreCooldown && !cooldownTimers.containsKey(id)) {
                if(!fullCooldown) {
                    cooldown -= (int) (cooldown * (leftTime / (float) duration / 2f));
                } else {
                    expectedCooldownTimes.put(id, cooldown);
                }
                //Entries should to be at least 1
                cooldownTimers.put(id, Math.max(cooldown, 1));
                activeTimers.put(id, 1);
            }
            activeTimers.removeInt(id);
            expectedDurations.removeInt(id);
            action.onDeactivated(player);
            dirty = true;
        }
    }


    @Override
    public void unlockActions(@NotNull Collection<IAction<T>> actions) {
        for (IAction<T> action : actions) {
            if (!RegUtil.has(action)) {
                throw new ActionNotRegisteredException(action);
            }
        }
        unlockedActions.addAll(actions);
    }

    /**
     * Update the actions
     * Should only be called by the corresponding Capability instance
     *
     * @return If a sync is recommended, only relevant on server side
     */
    public boolean updateActions() {
        //First update cooldown timers so active actions that become deactivated are not ticked.
        for (Iterator<Object2IntMap.Entry<ResourceLocation>> it = cooldownTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
            Object2IntMap.Entry<ResourceLocation> entry = it.next();
            int value = entry.getIntValue();
            player.getRepresentingPlayer().awardStat(ModStats.ACTION_COOLDOWN_TIME.get().get(RegUtil.getAction(entry.getKey())));
            if (value <= 1) { //<= Just in case we have missed something
                expectedCooldownTimes.removeInt(entry);
                it.remove();
            } else {
                entry.setValue(value - 1);
            }
        }

        List<ResourceLocation> toRemove = new ArrayList<>();
        for (Object2IntMap.Entry<ResourceLocation> entry : activeTimers.object2IntEntrySet()) {
            int newtimer = entry.getIntValue() - 1;
            ResourceLocation id = entry.getKey();

            if (newtimer == 0) {
                toRemove.add(id);
            } else {
                @SuppressWarnings("unchecked")
                ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
                /*
                 * If the event result is DENY, the lasting action will always be deactivated next tick and won't call {@link de.teamlapen.vampirism.api.entity.player.actions.ILastingAction#onUpdate(de.teamlapen.vampirism.api.entity.player.IFactionPlayer)}.
                 * If the event result is ALLOW, the lasting action will call onUpdate, but the return value will be ignored.
                 * If its the default, there will be no change, onUpdate will be called and deactivated if it should.
                 */
                ActionEvent.ActionUpdateEvent event = VampirismEventFactory.fireActionUpdateEvent(player, action, newtimer);
                boolean shouldDeactivate = event.shouldOverrideDeactivation();
                switch (event.getResult()) {
                    case DEFAULT -> shouldDeactivate = action.onUpdate(player);
                    case ALLOW -> action.onUpdate(player);
                }
                if (shouldDeactivate) {
                    entry.setValue(1); //Value of means they are deactivated next tick and onUpdate is not called again
                } else {
                    player.getRepresentingPlayer().awardStat(ModStats.ACTION_TIME.get().get(action));
                    entry.setValue(newtimer);
                }
            }
        }
        toRemove.forEach(id -> {
            @SuppressWarnings("unchecked")
            ILastingAction<T> action = (ILastingAction<T>) RegUtil.getAction(id);
            deactivateAction(action, true);
            cooldownTimers.put(id, expectedCooldownTimes.getInt(id));
            dirty = true;
        });
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT() {
        return serializeNBT();
    }

    private boolean isActionAllowedPermission(IAction<T> action) {
        if (player.getRepresentingPlayer() instanceof ServerPlayer serverPlayer) {
            return Permissions.ACTION.isAllowed(serverPlayer, action);
        }
        return true;
    }

    private void loadTimerMapFromNBT(@NotNull CompoundTag nbt, @NotNull Object2IntMap<ResourceLocation> map) {
        for (String key : nbt.getAllKeys()) {
            ResourceLocation id = new ResourceLocation(key);
            if (RegUtil.getAction(id) == null) {
                LOGGER.warn("Did not find action with key {}", key);
            } else {
                map.put(id, nbt.getInt(key));
            }
        }
    }

    private @NotNull CompoundTag writeTimersToNBT(@NotNull ObjectSet<Object2IntMap.Entry<ResourceLocation>> set) {
        CompoundTag nbt = new CompoundTag();
        for (Object2IntMap.Entry<ResourceLocation> entry : set) {
            nbt.putInt(entry.getKey().toString(), entry.getIntValue());
        }
        return nbt;
    }

    @Override
    public String nbtKey() {
        return NBT_KEY;
    }

    /**
     * Is thrown if an unregistered action is used
     */
    public static class ActionNotRegisteredException extends RuntimeException {
        public ActionNotRegisteredException(String name) {
            super("Action " + name + " is not registered. You cannot use it otherwise");
        }

        public ActionNotRegisteredException(@NotNull IAction<?> action) {
            this(action.toString());
        }
    }

    public static class ActivationContext implements IAction.ActivationContext {

        private final @Nullable Entity entity;
        private final @Nullable BlockPos blockPos;

        public ActivationContext(@Nullable Entity entity) {
            this.entity = entity;
            this.blockPos = null;
        }

        public ActivationContext(@Nullable BlockPos pos) {
            this.entity = null;
            this.blockPos = pos;
        }

        public ActivationContext() {
            this.entity = null;
            this.blockPos = null;
        }

        @Override
        public @NotNull Optional<BlockPos> targetBlock() {
            return Optional.ofNullable(blockPos);
        }

        @Override
        public @NotNull Optional<Entity> targetEntity() {
            return Optional.ofNullable(entity);
        }
    }
}
