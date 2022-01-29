package de.teamlapen.vampirism.api.entity.player.actions;


import com.google.common.collect.ImmutableList;
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
     * If active, the remaining duration is extended by the giving duration
     *
     * @param action
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
     * @param action
     * @return
     */
    float getPercentageForAction(@Nonnull IAction action);

    /**
     * @return A list of actions which are unlocked for the player
     */
    ImmutableList<IAction> getUnlockedActions();

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

    boolean isActionOnCooldown(IAction action);

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
     * Set cooldown to 0
     * <br>
     * Set active timer to 0 if {@link ILastingAction}
     * @param action the action that should be effected
     */
    void resetTimer(@Nonnull IAction action);

    /**
     * Deprecated. Use context-sensitive version below
     *
     * TODO 1.19 remove
     */
    @Deprecated
    IAction.PERM toggleAction(IAction action);

    /**
     * Toggle the action (server side).
     * If you just want to make sure it is deactivated, call {@link #deactivateAction(ILastingAction)}
     * @param action Action
     * @param context Context holding Block/Entity the player was looking at when activating if any
     * @return result
     */
    default IAction.PERM toggleAction(IAction action, IAction.ActivationContext context){
        return toggleAction(action);
    }

    /**
     * Deactivate a lasting action, if it was active.
     */
    default void deactivateAction(ILastingAction<?> action){
        if(isActionActive(action)){
            toggleAction(action);
        }
    }

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     *
     * @param actions
     */
    void unlockActions(Collection<IAction> actions);
}
