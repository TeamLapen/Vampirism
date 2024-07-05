package de.teamlapen.vampirism.entity.player.actions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.lib.lib.storage.ISyncableSaveData;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.event.ActionEvent;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles actions for vampire players
 * <p>
 * This uses fastutil maps to store the cooldown/active timers for the individual action.
 * Actions are identified by their registry name (ResourceLocation) in the maps.
 * <p>
 * Probably not the fastest or cleanest approach, but I did not find the perfect solution yet.
 */
public class ActionHandler<T extends IFactionPlayer<T> & ISkillPlayer<T>> implements IActionHandler<T>, ISyncableSaveData {
    private static final String NBT_KEY = "action_handler";

    /**
     * Holds any action in cooldown state. Maps it to the corresponding cooldown timer
     * Actions represented by any key in this map have to be registerer…
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #activeTimers}
     */
    private final @NotNull Object2IntMap<Holder<? extends IAction<T>>> cooldownTimers;
    /**
     * Holds any active action. Maps it to the corresponding action timer.
     * Actions represented by any key in this map have to be registered and must implement ILastingAction.
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #cooldownTimers}
     *
     * @implNote The values must be of type {@link Holder<ILastingAction<T>>}
     */
    private final @NotNull Object2IntMap<Holder<? extends ILastingAction<T>>> activeTimers;
    /**
     * Stores the expected cooldown of an action after it was activated.
     * This is used to check the action cooldown instead of {@link de.teamlapen.vampirism.api.entity.player.actions.IAction#getCooldown(IFactionPlayer)} as the cooldown might be modified before activation.
     * The values stored here are only changed when the cooldown is added, it is not decremented like the map for cooldown timers, but removed when the action's cooldown is over.
     */
    private final @NotNull Object2IntMap<Holder<? extends IAction<T>>> expectedCooldownTimes;
    /**
     * Stores the expected duration of an action after it was activated.
     * This is used to check the action duration instead of {@link de.teamlapen.vampirism.api.entity.player.actions.ILastingAction#getDuration(IFactionPlayer)} (IFactionPlayer)} as the duration might be modified before activation.
     * The values stored here are only changed when the duration is added, it is not decremented like the map for duration timers, but removed when the action's duration is over.
     */
    private final @NotNull Object2IntMap<Holder<? extends IAction<T>>> expectedDurations;

    private final T player;

    private final List<Holder<? extends IAction<T>>> unlockedActions = new ArrayList<>();

    /**
     * If active/cooldown timers have changed and should be synced
     */
    private boolean dirty = true;

    public ActionHandler(@NotNull T player) {
        this.player = player;
        List<Holder<IAction<?>>> actions = VampirismAPI.actionManager().getActionsForFaction(player.getFaction());

        cooldownTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        activeTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedCooldownTimes = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedDurations = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
    }

    public void deactivateAllActions() {
        for (Holder<? extends ILastingAction<T>> r : activeTimers.keySet()) {
            deactivateAction(r, false, true);
        }
        this.activeTimers.clear();
        dirty = true;
    }

    @Override
    public void extendActionTimer(@NotNull Holder<? extends ILastingAction<T>> action, int extension) {
        this.activeTimers.computeIntIfPresent(action, (action1, duration) -> duration + extension);
    }

    @Override
    public @NotNull List<IAction<T>> getAvailableActions() {
        return getAvailableActionsHolder().stream().map(Holder::value).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<Holder<? extends IAction<T>>> getAvailableActionsHolder() {
        return this.unlockedActions.stream().filter(s -> s.value().canUse(this.player) == IAction.PERM.ALLOWED).toList();
    }

    @Override
    public float getPercentageForAction(@NotNull Holder<? extends IAction<T>> action) {
        if (activeTimers.containsKey(action)) {
            return activeTimers.getInt(action) / ((float) expectedDurations.getInt(action));
        }
        if (cooldownTimers.containsKey(action)) {
            return -cooldownTimers.getInt(action) / (float) expectedCooldownTimes.getInt(action);
        }
        return 0f;
    }

    @Override
    public float getCooldownPercentage(@NotNull Holder<? extends IAction<T>> action) {
        if (cooldownTimers.containsKey(action)) {
            return -cooldownTimers.getInt(action) / (float) expectedCooldownTimes.getInt(action);
        }
        return 0;
    }

    @Override
    public float getDurationPercentage(@NotNull Holder<? extends ILastingAction<?>> action) {
        if (activeTimers.containsKey(action)) {
            return activeTimers.getInt(action) / ((float) expectedDurations.getInt(action));
        }
        return 0;
    }

    @Override
    public @NotNull ImmutableList<IAction<T>> getUnlockedActions() {
        return this.unlockedActions.stream().map(Holder::value).collect(ImmutableList.toImmutableList());
    }

    @Override
    public @NotNull List<Holder<? extends IAction<T>>> getUnlockedActionHolder() {
        return ImmutableList.copyOf(this.unlockedActions);
    }

    @Override
    public @NotNull List<Holder<? extends ILastingAction<T>>> getActiveActions() {
        return ImmutableList.copyOf(this.activeTimers.keySet());
    }

    @Override
    public boolean isActionActive(@NotNull Holder<? extends ILastingAction<T>> action) {
        return activeTimers.containsKey(action);
    }

    @Override
    public boolean isActionOnCooldown(@NotNull Holder<? extends IAction<T>> action) {
        return cooldownTimers.containsKey(action);
    }

    @Override
    public boolean isActionUnlocked(@NotNull Holder<? extends IAction<T>> action) {
        return this.unlockedActions.contains(action);
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        //If loading from save we want to clear everything beforehand.
        //NBT only contains actions that are active/cooldown
        activeTimers.clear();
        cooldownTimers.clear();
        expectedCooldownTimes.clear();
        expectedDurations.clear();
        if (nbt.contains("actions_active")) //noinspection unchecked
        {
            loadTimerMapFromNBT(nbt.getCompound("actions_active"), (Object2IntMap<Holder<? extends IAction<T>>>) (Object) activeTimers);
        }
        if (nbt.contains("actions_cooldown")) loadTimerMapFromNBT(nbt.getCompound("actions_cooldown"), cooldownTimers);
        if (nbt.contains("actions_cooldown_expected")) loadTimerMapFromNBT(nbt.getCompound("actions_cooldown_expected"), expectedCooldownTimes);
        if (nbt.contains("actions_duration_expected")) loadTimerMapFromNBT(nbt.getCompound("actions_duration_expected"), expectedDurations);
    }

    /**
     * Should only be called by the corresponding Capability instance
     */
    public void onActionsReactivated() {
        if (!player.isRemote()) {
            for (Holder<? extends ILastingAction<T>> holder : activeTimers.keySet()) {
                holder.value().onReActivated(player);
            }
        }
    }

    /**
     * Should only be called by the corresponding Capability instance
     * <p>
     * Attention: nbt is modified in the process
     **/
    @Override
    public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
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
            List<Holder<? extends ILastingAction<T>>> toRemove = new ArrayList<>();
            for (Object2IntMap.Entry<Holder<? extends ILastingAction<T>>> client_active : activeTimers.object2IntEntrySet()) {
                String key = client_active.getKey().toString();
                if (active.contains(key)) {
                    client_active.setValue(active.getInt(key));
                    nbt.remove(key);
                } else {
                    toRemove.add(client_active.getKey());
                }
            }
            toRemove.forEach(this::deactivateAction);

            for (String key : active.getAllKeys()) {
                ResourceLocation id = ResourceLocation.parse(key);
                //noinspection unchecked
                ModRegistries.ACTIONS.getHolder(id).filter(s -> s.value() instanceof ILastingAction<?>).map(s -> ((Holder.Reference<ILastingAction<T>>) (Object) s)).ifPresent(action -> {
                    action.value().onActivatedClient(this.player);
                    this.activeTimers.put(action, active.getInt(key));
                });
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
    public void relockActionHolder(@NotNull Collection<Holder<? extends IAction<T>>> actions) {
        unlockedActions.removeAll(actions);
        for (Holder<? extends IAction<T>> action : actions) {
            if (action.value() instanceof ILastingAction<T>) {
                //noinspection unchecked
                deactivateAction((Holder<ILastingAction<T>>) action);
            }
        }
    }

    @Override
    public void resetTimers() {
        for (Holder<? extends ILastingAction<T>> action : activeTimers.keySet()) {
            deactivateAction(action, true);
        }
        activeTimers.clear();
        cooldownTimers.clear();
        expectedCooldownTimes.clear();
        expectedDurations.clear();
        dirty = true;
    }

    @Override
    public void resetTimer(@NotNull Holder<? extends IAction<T>> action) {
        if (action.value() instanceof ILastingAction<T>) {
            //noinspection unchecked
            deactivateAction((Holder<ILastingAction<T>>) action, true);
        }
        this.cooldownTimers.removeInt(action);
        this.expectedCooldownTimes.removeInt(action);
        this.dirty = true;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        //noinspection unchecked
        tag.put("actions_active", writeTimersToNBT((ObjectSet<Object2IntMap.Entry<Holder<? extends IAction<T>>>>) (Object) activeTimers.object2IntEntrySet()));
        tag.put("actions_cooldown", writeTimersToNBT(cooldownTimers.object2IntEntrySet()));
        tag.put("actions_cooldown_expected", writeTimersToNBT(expectedCooldownTimes.object2IntEntrySet()));
        tag.put("actions_duration_expected", writeTimersToNBT(expectedDurations.object2IntEntrySet()));
        return tag;
    }

    /**
     * After server receives action toggle packet this is called.
     * Actions can be canceled, have their cooldown changed, or if a lasting action their duration changed as well through {@link de.teamlapen.vampirism.api.event.ActionEvent.ActionActivatedEvent}
     *
     * @param action  Action being toggled
     * @param context Context holding Block/Entity the player was looking at when activating if any
     */
    @Override
    public IAction.@NotNull PERM toggleAction(@NotNull Holder<? extends IAction<T>> action, IAction.@NotNull ActivationContext context) {
        if (activeTimers.containsKey(action)) {
            deactivateAction((Holder<ILastingAction<T>>) action);
            dirty = true;
            return IAction.PERM.ALLOWED;
        } else if (cooldownTimers.containsKey(action)) {
            return IAction.PERM.COOLDOWN;
        } else {
            if (this.player.asEntity().isSpectator()) return IAction.PERM.DISALLOWED;
            if (!isActionUnlocked(action)) return IAction.PERM.NOT_UNLOCKED;
            if (!isActionAllowedPermission(action)) return IAction.PERM.PERMISSION_DISALLOWED;

            IAction.PERM r = action.value().canUse(player);
            if (r == IAction.PERM.ALLOWED) {
                /*
                 * Only lasting actions have a duration, so regular actions will return a duration of -1.
                 */
                int duration = action.value() instanceof ILastingAction<T> lasting ? lasting.getDuration(player) : -1;
                ActionEvent.ActionActivatedEvent<T> activationEvent = VampirismEventFactory.fireActionActivatedEvent(player, action, action.value().getCooldown(player), duration);
                if (activationEvent.isCanceled()) return IAction.PERM.DISALLOWED;
                if (action.value().onActivated(player, context)) {
                    player.asEntity().awardStat(ModStats.ACTION_USED.get().get(action.value()));
                    //Even though lasting actions do not activate their cooldown until they deactivate
                    //we probably want to keep this here so that they are edited by one event.
                    int cooldown = activationEvent.getCooldown();
                    expectedCooldownTimes.put(action, cooldown);
                    if (action.value() instanceof ILastingAction) {
                        expectedDurations.put(action, activationEvent.getDuration());
                        duration = activationEvent.getDuration();
                        //noinspection unchecked
                        activeTimers.put((Holder<ILastingAction<T>>) action, duration);
                    } else {
                        cooldownTimers.put(action, cooldown);
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
    public void deactivateAction(Holder<? extends ILastingAction<T>> action) {
        this.deactivateAction(action, false);
    }

    public void deactivateAction(@NotNull Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown) {
        deactivateAction(action, false, false);
    }

    @Override
    public void deactivateAction(@NotNull Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown, boolean fullCooldown) {
        if (activeTimers.containsKey(action)) {
            int leftTime = activeTimers.getInt(action);
            int duration = expectedDurations.getInt(action);
            var event = VampirismEventFactory.fireActionDeactivatedEvent(player, action, leftTime, expectedCooldownTimes.getInt(action), ignoreCooldown, fullCooldown);
            int cooldown = event.getCooldown();
            if (!event.ignoreCooldown() && !cooldownTimers.containsKey(action)) {
                if (!event.fullCooldown()) {
                    cooldown -= (int) (cooldown * (leftTime / (float) duration / 2f));
                } else {
                    expectedCooldownTimes.put(action, cooldown);
                }
                //Entries should to be at least 1
                cooldownTimers.put(action, Math.max(cooldown, 1));
                activeTimers.put(action, 1);
            }
            activeTimers.removeInt(action);
            expectedDurations.removeInt(action);
            action.value().onDeactivated(player);
            dirty = true;
        }
    }

    @Override
    public void unlockActionHolder(@NotNull Collection<Holder<? extends IAction<T>>> actions) {
        unlockedActions.addAll(actions);
    }

    /**
     * Update the actions
     * Should only be called by the corresponding Capability instance
     *
     * @return If a sync is recommended, only relevant on server side
     */
    public void updateActions() {
        //First update cooldown timers so active actions that become deactivated are not ticked.
        for (Iterator<Object2IntMap.Entry<Holder<? extends IAction<T>>>> it = cooldownTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
            Object2IntMap.Entry<Holder<? extends IAction<T>>> entry = it.next();
            int value = entry.getIntValue();
            player.asEntity().awardStat(ModStats.ACTION_COOLDOWN_TIME.get().get(entry.getKey().value()));
            if (value <= 1) { //<= Just in case we have missed something
                expectedCooldownTimes.removeInt(entry);
                it.remove();
            } else {
                entry.setValue(value - 1);
            }
        }

        List<Holder<? extends ILastingAction<T>>> toRemove = new ArrayList<>();
        for (Object2IntMap.Entry<Holder<? extends ILastingAction<T>>> entry : activeTimers.object2IntEntrySet()) {
            int newtimer = entry.getIntValue() - 1;

            if (newtimer == 0) {
                toRemove.add(entry.getKey());
            } else {
                Holder<? extends ILastingAction<T>> action = entry.getKey();
                ActionEvent.ActionUpdateEvent<T> event = VampirismEventFactory.fireActionUpdateEvent(player, action, newtimer);
                int expectedDuration = expectedDurations.getInt(action);
                if ((!event.shouldSkipActionUpdate() && action.value().onUpdate(player, expectedDuration - newtimer, expectedDuration)) || event.shouldDeactivation()) {
                    entry.setValue(1); //Value of means they are deactivated next tick and onUpdate is not called again
                } else {
                    player.asEntity().awardStat(ModStats.ACTION_TIME.get().get(action.value()));
                    entry.setValue(newtimer);
                }
            }
        }
        toRemove.forEach(holder -> {
            deactivateAction(holder, true);
            cooldownTimers.put(holder, expectedCooldownTimes.getInt(holder));
            dirty = true;
        });
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider, boolean all) {
        if (this.dirty || all) {
            return serializeNBT(provider);
        } else {
            return new CompoundTag();
        }
    }

    private boolean isActionAllowedPermission(Holder<? extends IAction<T>> action) {
        if (player.asEntity() instanceof ServerPlayer serverPlayer) {
            return Permissions.ACTION.isAllowed(serverPlayer, action.value());
        }
        return true;
    }

    private void loadTimerMapFromNBT(@NotNull CompoundTag nbt, @NotNull Object2IntMap<Holder<? extends IAction<T>>> map) {
        for (String key : nbt.getAllKeys()) {
            ResourceLocation id = ResourceLocation.parse(key);
            ModRegistries.ACTIONS.getHolder(id).ifPresent(action -> {
                //noinspection RedundantCast,unchecked
                map.put((Holder<? extends IAction<T>>) (Object) action, nbt.getInt(key));
            });
        }
    }

    private @NotNull CompoundTag writeTimersToNBT(@NotNull ObjectSet<Object2IntMap.Entry<Holder<? extends IAction<T>>>> set) {
        CompoundTag nbt = new CompoundTag();
        for (Object2IntMap.Entry<Holder<? extends IAction<T>>> entry : set) {
            entry.getKey().unwrapKey().ifPresent(key -> {
                nbt.putInt(key.location().toString(), entry.getIntValue());
            });
        }
        return nbt;
    }

    @Override
    public String nbtKey() {
        return NBT_KEY;
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
