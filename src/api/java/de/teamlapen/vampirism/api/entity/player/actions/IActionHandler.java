package de.teamlapen.vampirism.api.entity.player.actions;


import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T extends IFactionPlayer<T>> {

    /**
     * Deactivate any active action and start cooldown timer
     */
    void deactivateAllActions();

    /**
     * If active, the remaining duration is extended by the giving duration
     */
    void extendActionTimer(@Nonnull ILastingAction<T> action, int duration);

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction<T>> getAvailableActions();

    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     */
    float getPercentageForAction(@Nonnull IAction<T> action);

    /**
     * @return A list of actions which are unlocked for the player
     */
    ImmutableList<IAction<T>> getUnlockedActions();

    /**
     * Checks if the action is currently activated
     */
    boolean isActionActive(@Nonnull ILastingAction<T> action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingAction)} over this one
     */
    boolean isActionActive(ResourceLocation id);

    boolean isActionOnCooldown(IAction<T> action);

    boolean isActionUnlocked(IAction<T> action);

    /**
     * Locks the given actions again
     */
    void relockActions(Collection<IAction<T>> actions);

    /**
     * Set all timers to 0
     */
    void resetTimers();

    /**
     * Set cooldown to 0
     * <br>
     * Set active timer to 0 if {@link ILastingAction}
     * @param action the action that should be effected
     */
    void resetTimer(@Nonnull IAction action);

    /**
     * Toggle the action (server side).
     * If you just want to make sure it is deactivated, call {@link #deactivateAction(ILastingAction)}
     * @param action Action
     * @param context Context holding Block/Entity the player was looking at when activating if any
     * @return result
     */
    IAction.PERM toggleAction(IAction<T> action, IAction.ActivationContext context);

    /**
     * Deactivate a lasting action, if it was active.
     */
    void deactivateAction(ILastingAction<T> action);

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     */
    void unlockActions(Collection<IAction<T>> actions);
}
