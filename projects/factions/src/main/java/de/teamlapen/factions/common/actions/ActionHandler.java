package de.teamlapen.factions.common.actions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.event.ActionEvent;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.Permissions;
import de.teamlapen.factions.common.core.FactionAdvancements;
import de.teamlapen.factions.common.core.FactionStats;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.event.FactionEventFactory;
import de.teamlapen.factions.common.util.collections.CollectionUtil;
import de.teamlapen.sync.PropertySync;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
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
public class ActionHandler<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends PropertySync implements IActionHandler<T>, ValueIOSerializable {

    /**
     * Holds any action in cooldown state. Maps it to the corresponding cooldown timer
     * Actions represented by any key in this map have to be registerer…
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #activeTimers}
     */
    private final Object2IntMap<Holder<? extends IAction<T>>> cooldownTimers;

    /**
     * Holds any active action. Maps it to the corresponding action timer.
     * Actions represented by any key in this map have to be registered and must implement ILastingAction.
     * Values should be larger 0, they will be counted down and removed if they would hit 0.
     * <p>
     * Keys should be mutually exclusive with {@link #cooldownTimers}
     *
     * @implNote The values must be of type {@link Holder<ILastingAction>}
     */
    private final Object2IntMap<Holder<? extends ILastingAction<T>>> activeTimers;

    /**
     * Stores the expected cooldown of an action after it was activated.
     * This is used to check the action cooldown instead of {@link IAction#getCooldown(ISkillPlayer)} as the cooldown might be modified before activation.
     * The values stored here are only changed when the cooldown is added, it is not decremented like the map for cooldown timers, but removed when the action's cooldown is over.
     */
    private final Object2IntMap<Holder<? extends IAction<T>>> expectedCooldownTimes;

    /**
     * Stores the expected duration of an action after it was activated.
     * This is used to check the action duration instead of {@link ILastingAction#getDuration(IFactionPlayer)} as the duration might be modified before activation.
     * The values stored here are only changed when the duration is added, it is not decremented like the map for duration timers, but removed when the action's duration is over.
     */
    private final Object2IntMap<Holder<? extends IAction<T>>> expectedDurations;

    private final T player;

    private final List<Holder<? extends IAction<T>>> unlockedActions = new ArrayList<>();

    public ActionHandler(@NotNull T player) {
        this.player = player;
        List<Holder<IAction<?>>> actions = ActionHelper.getActions(player.getFaction());

        cooldownTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        activeTimers = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedCooldownTimes = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
        expectedDurations = new Object2IntOpenHashMap<>(actions.size(), 0.9f);
    }

    @Override
    public void sync() {
        this.player.sync();
    }

    public void deactivateAllActions() {
        for (Holder<? extends ILastingAction<T>> r : activeTimers.keySet()) {
            deactivateAction(r, false, true);
        }
        this.activeTimers.clear();
        sync();
    }

    @Override
    public void extendActionTimer(Holder<? extends ILastingAction<T>> action, int extension) {
        if (this.activeTimers.containsKey(action)) {
            this.activeTimers.put(action, this.activeTimers.getInt(action) + extension);
            this.expectedDurations.put(action, this.expectedDurations.getInt(action) + extension);
        }
    }

    @Override
    public List<IAction<T>> getAvailableActions() {
        return getAvailableActionsHolder().stream().map(Holder::value).collect(Collectors.toList());
    }

    @Override
    public List<Holder<? extends IAction<T>>> getAvailableActionsHolder() {
        return this.unlockedActions.stream().filter(s -> s.value().canUse(this.player).successful()).toList();
    }

    @Override
    public float getPercentageForAction(Holder<? extends IAction<T>> action) {
        if (activeTimers.containsKey(action)) {
            return activeTimers.getInt(action) / ((float) expectedDurations.getInt(action));
        }
        if (cooldownTimers.containsKey(action)) {
            return -cooldownTimers.getInt(action) / (float) expectedCooldownTimes.getInt(action);
        }
        return 0f;
    }

    @Override
    public float getCooldownPercentage(Holder<? extends IAction<T>> action) {
        if (cooldownTimers.containsKey(action)) {
            return -cooldownTimers.getInt(action) / (float) expectedCooldownTimes.getInt(action);
        }
        return 0;
    }

    @Override
    public float getDurationPercentage(Holder<? extends ILastingAction<?>> action) {
        if (activeTimers.containsKey(action)) {
            return activeTimers.getInt(action) / ((float) expectedDurations.getInt(action));
        }
        return 0;
    }

    @Override
    public ImmutableList<IAction<T>> getUnlockedActions() {
        return this.unlockedActions.stream().map(Holder::value).collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<Holder<? extends IAction<T>>> getUnlockedActionHolder() {
        return ImmutableList.copyOf(this.unlockedActions);
    }

    @Override
    public List<Holder<? extends ILastingAction<T>>> getActiveActions() {
        return ImmutableList.copyOf(this.activeTimers.keySet());
    }

    @Override
    public boolean isActionActive(Holder<? extends ILastingAction<T>> action) {
        return activeTimers.containsKey(action);
    }

    @Override
    public boolean isActionOnCooldown(Holder<? extends IAction<T>> action) {
        return cooldownTimers.containsKey(action);
    }

    @Override
    public boolean isActionUnlocked(Holder<? extends IAction<T>> action) {
        return this.unlockedActions.contains(action);
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

    private static final Codec<Map<Holder<IAction<?>>, Integer>> ACTION_TIME_CODEC = Codec.simpleMap(IAction.CODEC, Codec.INT, ModRegistries.ACTIONS).codec();
    private static final Codec<Map<Holder<ILastingAction<?>>, Integer>> LASTING_ACTION_TIME_CODEC = Codec.simpleMap(ILastingAction.CODEC, Codec.INT, ModRegistries.ACTIONS).codec();

    @Override
    protected void registerProperties() {
        this.registerProperty(FResourceLocation.mod("cooldown_timer"), (Codec<Map<Holder<? extends IAction<T>>, Integer>>) (Object) ACTION_TIME_CODEC, new HashMap<>(), () -> this.cooldownTimers, map -> {
            CollectionUtil.updateCollection(this.cooldownTimers, map);
            return true;
        }, true);
        this.registerProperty(FResourceLocation.mod("duration_timer"), (Codec<Map<Holder<? extends ILastingAction<T>>, Integer>>) (Object) LASTING_ACTION_TIME_CODEC, new HashMap<>(), () -> this.activeTimers, map -> {
            CollectionUtil.updateCollection(this.activeTimers, map,(action, duration) -> deactivateAction(action), (action, timer) -> action.value().onActivatedClient(this.player));
            return true;
        }, true);
        this.registerProperty(FResourceLocation.mod("expected_cooldown_timer"), (Codec<Map<Holder<? extends IAction<T>>, Integer>>) (Object) ACTION_TIME_CODEC, new HashMap<>(), () -> this.expectedCooldownTimes, map -> {
            CollectionUtil.updateCollection(this.expectedCooldownTimes, map);
            return true;
        }, true);
        this.registerProperty(FResourceLocation.mod("expected_duration_timer"), (Codec<Map<Holder<? extends IAction<T>>, Integer>>) (Object) ACTION_TIME_CODEC, new HashMap<>(), () -> this.expectedDurations, map -> {
            CollectionUtil.updateCollection(this.expectedDurations, map);
            return true;
        }, true);
        this.registerListProperty(FResourceLocation.mod("unlocked_actions"), (Codec<Holder<? extends IAction<T>>>) (Object) IAction.CODEC, ArrayList::new, () -> this.unlockedActions, l -> {
            this.unlockedActions.clear();
            this.unlockedActions.addAll(l);
            return true;
        }, true);
    }

    @Override
    public void relockActionHolder(Collection<Holder<? extends IAction<T>>> actions) {
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
        sync();
    }

    @Override
    public void resetTimer(Holder<? extends IAction<T>> action) {
        if (action.value() instanceof ILastingAction<T>) {
            //noinspection unchecked
            deactivateAction((Holder<ILastingAction<T>>) action, true);
        }
        this.cooldownTimers.removeInt(action);
        this.expectedCooldownTimes.removeInt(action);
        sync();
    }

    /**
     * After server receives action toggle packet this is called.
     * Actions can be canceled, have their cooldown changed, or if a lasting action their duration changed as well through {@link ActionEvent.ActionActivatedEvent}
     *
     * @param action  Action being toggled
     * @param context Context holding Block/Entity the player was looking at when activating if any
     */
    @Override
    public IActionResult toggleAction(Holder<? extends IAction<T>> action, IAction.ActivationContext context) {
        if (activeTimers.containsKey(action)) {
            deactivateAction((Holder<ILastingAction<T>>) action);
            return IActionResult.SUCCESS;
        } else if (cooldownTimers.containsKey(action)) {
            return IActionResult.ON_COOLDOWN;
        } else {
            if (this.player.asEntity().isSpectator()) return IActionResult.RESTRICTED;
            if (!isActionUnlocked(action)) return IActionResult.NOT_UNLOCKED;
            if (!isActionAllowedPermission(action)) return IActionResult.DISALLOWED_PERMISSION;

            IActionResult r = action.value().canUse(player);
            if (r.successful()) {
                /*
                 * Only lasting actions have a duration, so regular actions will return a duration of -1.
                 */
                int duration = action.value() instanceof ILastingAction<T> lasting ? lasting.getDuration(player) : -1;
                ActionEvent.ActionActivatedEvent<T> activationEvent = FactionEventFactory.fireActionActivatedEvent(player, action, action.value().getCooldown(player), duration);
                if (activationEvent.isCanceled()) return IActionResult.fail(activationEvent.getCancelMessage());
                r = action.value().onActivated(player, context);
                if (r.successful()) {
                    player.asEntity().awardStat(FactionStats.ACTION_USED.get().get(action.value()));
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

                    if (player instanceof ServerPlayer serverPlayer) {
                        FactionAdvancements.ACTION_TRIGGER.get().trigger(serverPlayer, action);
                    }
                }
            }
            return r;
        }
    }

    @Override
    public void deactivateAction(Holder<? extends ILastingAction<T>> action) {
        this.deactivateAction(action, false);
    }

    public void deactivateAction(Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown) {
        deactivateAction(action, false, false);
    }

    @Override
    public void deactivateAction(Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown, boolean fullCooldown) {
        if (activeTimers.containsKey(action)) {
            int leftTime = activeTimers.getInt(action);
            int duration = expectedDurations.getInt(action);
            var event = FactionEventFactory.fireActionDeactivatedEvent(player, action, leftTime, expectedCooldownTimes.getInt(action), ignoreCooldown, fullCooldown);
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
            sync();
        }
    }

    @Override
    public void unlockActionHolder(Collection<Holder<? extends IAction<T>>> actions) {
        unlockedActions.addAll(actions);
    }

    /**
     * Update the actions
     * Should only be called by the corresponding Capability instance
     */
    public void updateActions() {
        //First update cooldown timers so active actions that become deactivated are not ticked.
        for (Iterator<Object2IntMap.Entry<Holder<? extends IAction<T>>>> it = cooldownTimers.object2IntEntrySet().iterator(); it.hasNext(); ) {
            Object2IntMap.Entry<Holder<? extends IAction<T>>> entry = it.next();
            int value = entry.getIntValue();
            player.asEntity().awardStat(FactionStats.ACTION_COOLDOWN_TIME.get().get(entry.getKey().value()));
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
                ActionEvent.ActionUpdateEvent<T> event = FactionEventFactory.fireActionUpdateEvent(player, action, newtimer);
                int expectedDuration = expectedDurations.getInt(action);
                if ((!event.shouldSkipActionUpdate() && action.value().onUpdate(player, expectedDuration - newtimer, expectedDuration)) || event.shouldDeactivation()) {
                    entry.setValue(1); //Value of means they are deactivated next tick and onUpdate is not called again
                } else {
                    player.asEntity().awardStat(FactionStats.ACTION_TIME.get().get(action.value()));
                    entry.setValue(newtimer);
                }
            }
        }
        toRemove.forEach(holder -> {
            deactivateAction(holder, true);
            cooldownTimers.put(holder, expectedCooldownTimes.getInt(holder));
        });
        sync();
    }

    private boolean isActionAllowedPermission(Holder<? extends IAction<T>> action) {
        if (player.asEntity() instanceof ServerPlayer serverPlayer) {
            return Permissions.ACTION.isAllowed(serverPlayer, action.value());
        }
        return true;
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
        public Optional<BlockPos> targetBlock() {
            return Optional.ofNullable(blockPos);
        }

        @Override
        public Optional<Entity> targetEntity() {
            return Optional.ofNullable(entity);
        }
    }
}
