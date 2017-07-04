package de.teamlapen.vampirism.api.entity.player.actions;


import java.util.Collection;
import java.util.List;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T extends IActionPlayer> {
    void deactivateAllActions();

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction<T>> getAvailableActions();


    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     * @param action
     * @return
     */
    float getPercentageForAction(IAction<T> action);

    /**
     * Checks if the action is currently activated
     *
     * @param action
     * @return
     */
    boolean isActionActive(ILastingAction action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingAction)} over this one
     *
     * @return
     */
    boolean isActionActive(String id);

    boolean isActionUnlocked(IAction action);

    /**
     * Locks the given actions again
     *
     * @param actions
     */
    void relockActions(Collection<IAction<T>> actions);

    /**
     * Set all timers to 0
     */
    void resetTimers();

    /**
     * toggle the action (server side)
     *
     * @param action
     * @return
     */
    IAction.PERM toggleAction(IAction action);

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     *
     * @param actions
     */
    void unlockActions(Collection<IAction<T>> actions);
}
