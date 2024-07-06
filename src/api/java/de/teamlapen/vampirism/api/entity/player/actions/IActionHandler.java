package de.teamlapen.vampirism.api.entity.player.actions;


import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.annotations.FloatRange;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T extends IFactionPlayer<T> & ISkillPlayer<T>> {

    /**
     * Deactivate any active action and start cooldown timer
     */
    void deactivateAllActions();


    @Deprecated(forRemoval = true, since = "1.11")
    default void extendActionTimer(@NotNull ILastingAction<T> action, int duration) {
        extendActionTimer(RegUtil.holder(action), duration);
    }

    /**
     * If active, the remaining duration is extended by the giving duration
     */
    void extendActionTimer(@NotNull Holder<? extends ILastingAction<T>> action, int duration);

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction<T>> getAvailableActions();

    /**
     * @return A list of actions which currently are available to the player
     */
    List<Holder<? extends IAction<T>>> getAvailableActionsHolder();

    @Deprecated(forRemoval = true, since = "1.11")
    default float getPercentageForAction(@NotNull IAction<T> action) {
        return getPercentageForAction(RegUtil.holder(action));
    }

    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     * @deprecated Use {@link #getCooldownPercentage(Holder)} or {@link #getDurationPercentage(Holder)} instead
     */
    @Deprecated(since = "1.11")
    float getPercentageForAction(@NotNull Holder<? extends IAction<T>> action);

    /**
     * cooldown begins at 1 and goes to 0
     */
    @FloatRange(from = 0, to = 1)
    float getCooldownPercentage(Holder<? extends IAction<T>> action);

    /**
     * duration begins at 1 and goes to 0
     */
    @FloatRange(from = 0, to = 1)
    float getDurationPercentage(Holder<? extends ILastingAction<?>> action);

    /**
     * @return A list of actions which are unlocked for the player
     */
    ImmutableList<IAction<T>> getUnlockedActions();

    /**
     * @return A list of actions which are unlocked for the player
     */
    @Unmodifiable
    List<Holder<? extends IAction<T>>> getUnlockedActionHolder();

    @NotNull
    List<Holder<? extends ILastingAction<T>>> getActiveActions();

    @Deprecated(forRemoval = true, since = "1.11")
    default boolean isActionActive(@NotNull ILastingAction<T> action) {
        return isActionActive(RegUtil.holder(action));
    }

    /**
     * Checks if the action is currently activated
     */
    boolean isActionActive(@NotNull Holder<? extends ILastingAction<T>> action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingAction)} over this one
     */
    @SuppressWarnings("unchecked")
    @Deprecated(forRemoval = true, since = "1.11")
    default boolean isActionActive(ResourceLocation id) {
        return VampirismRegistries.ACTION.get().getHolder(id).map(s -> isActionActive((Holder<ILastingAction<T>>) (Object) s)).orElse(false);
    }

    @Deprecated(forRemoval = true, since = "1.11")
    default boolean isActionOnCooldown(IAction<T> action) {
        return isActionOnCooldown(RegUtil.holder(action));
    }

    boolean isActionOnCooldown(@NotNull Holder<? extends IAction<T>> action);

    @Deprecated(forRemoval = true, since = "1.11")
    default boolean isActionUnlocked(IAction<T> action) {
        return isActionUnlocked(RegUtil.holder(action));
    }

    boolean isActionUnlocked(@NotNull Holder<? extends IAction<T>> action);

    /**
     * Locks the given actions again
     */
    @Deprecated(forRemoval = true, since = "1.11")
    default void relockActions(Collection<IAction<T>> actions) {
        relockActionHolder(actions.stream().map(RegUtil::holder).collect(Collectors.toList()));
    }

    void relockActionHolder(Collection<Holder<? extends IAction<T>>> actions);

    /**
     * Set all timers to 0
     */
    void resetTimers();

    /**
     * Set cooldown to 0
     * <br>
     * Set active timer to 0 if {@link ILastingAction}
     *
     * @param action the action that should be effected
     */
    @Deprecated(forRemoval = true, since = "1.11")
    default void resetTimer(@NotNull IAction<T> action) {
        resetTimer(RegUtil.holder(action));
    }

    void resetTimer(@NotNull Holder<? extends IAction<T>> action);

    /**
     * Toggle the action (server side).
     * If you just want to make sure it is deactivated, call {@link #deactivateAction(ILastingAction)}
     *
     * @param action  Action
     * @param context Context holding Block/Entity the player was looking at when activating if any
     * @return result
     */
    IActionResult toggleAction(Holder<? extends IAction<T>> action, IAction.ActivationContext context);

    @Deprecated(forRemoval = true, since = "1.11")
    default void deactivateAction(ILastingAction<T> action) {
        deactivateAction(RegUtil.holder(action));
    }

    /**
     * Deactivate a lasting action, if it was active.
     */
    void deactivateAction(Holder<? extends ILastingAction<T>> action);

    @Deprecated(forRemoval = true, since = "1.11")
    default void deactivateAction(@NotNull ILastingAction<T> action, boolean ignoreCooldown, boolean fullCooldown) {
        deactivateAction(RegUtil.holder(action), ignoreCooldown, fullCooldown);
    }

    /**
     * Lasting actions are deactivated here, which fires the {@link de.teamlapen.vampirism.api.event.ActionEvent.ActionDeactivatedEvent}
     *
     * @param action         - The lasting action being deactivated
     * @param ignoreCooldown - Whether the cooldown is ignored for the action
     * @param fullCooldown   - Whether the lasting action should get the full or reduced cooldown
     */
    void deactivateAction(@NotNull Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown, boolean fullCooldown);


    @Deprecated(forRemoval = true, since = "1.11")
    default void unlockActions(Collection<IAction<T>> actions) {
        unlockActionHolder(actions.stream().map(RegUtil::holder).collect(Collectors.toList()));
    }

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     */
    void unlockActionHolder(Collection<Holder<? extends IAction<T>>> actions);
}
