package de.teamlapen.vampirism.api.entity.player.actions;


import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T extends IActionPlayer> {

    /**
     * Deactivate any active action and start cooldown timer
     */
    void deactivateAllActions();

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction> getAvailableActions();


    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     * @param action
     * @return
     */
    float getPercentageForAction(@Nonnull IAction action);

    /**
     * Checks if the action is currently activated
     *
     * @param action
     * @return
     */
    boolean isActionActive(@Nonnull ILastingAction action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingAction)} over this one
     *
     * @param id
     */
    boolean isActionActive(ResourceLocation id);

    boolean isActionUnlocked(IAction action);

    /**
     * Locks the given actions again
     *
     * @param actions
     */
    void relockActions(Collection<IAction> actions);

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
    void unlockActions(Collection<IAction> actions);
}
