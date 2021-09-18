package de.teamlapen.vampirism.api.entity.player.actions;


import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T> {

    /**
     * Deactivate any active action and start cooldown timer
     */
    void deactivateAllActions();

    /**
     * If active, the remaining duration is extended by the giving duration
     *
     */
    void extendActionTimer(@Nonnull ILastingAction action, int duration);

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction> getAvailableActions();

    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     */
    float getPercentageForAction(@Nonnull IAction action);

    /**
     * @return A list of actions which are unlocked for the player
     */
    ImmutableList<IAction> getUnlockedActions();

    /**
     * Checks if the action is currently activated
     *
     */
    boolean isActionActive(@Nonnull ILastingAction action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingAction)} over this one
     *
     */
    boolean isActionActive(ResourceLocation id);

    boolean isActionOnCooldown(IAction action);

    boolean isActionUnlocked(IAction action);

    /**
     * Locks the given actions again
     *
     */
    void relockActions(Collection<IAction> actions);

    /**
     * Set all timers to 0
     */
    void resetTimers();

    /**
     * toggle the action (server side)
     *
     */
    IAction.PERM toggleAction(IAction action);

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     *
     */
    void unlockActions(Collection<IAction> actions);
}
