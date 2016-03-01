package de.teamlapen.vampirism.api.entity.player.vampire;

import java.util.List;

/**
 * Interface for {@link IVampirePlayer}'s action handler
 */
public interface IActionHandler {


    void deactivateAllActions();

    List<IVampireAction> getAvailableActions();

    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     * @param action
     * @return
     */
    float getPercentageForAction(IVampireAction action);

    /**
     * Checks if the lasting action is currently activated.
     * Prefer {@link IActionHandler#isActionActive(ILastingVampireAction)} over this one
     *
     * @return
     */
    boolean isActionActive(String id);

    /**
     * Checks if the action is currently activated
     *
     * @param action
     * @return
     */
    boolean isActionActive(ILastingVampireAction action);

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
    IVampireAction.PERM toggleAction(IVampireAction action);
}
