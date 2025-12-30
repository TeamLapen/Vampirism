package de.teamlapen.faction.api.factions.actions;


import com.google.common.collect.ImmutableList;
import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.annotations.FloatRange;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.sync.api.ISyncable;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface for player's faction's action handler
 */
public interface IActionHandler<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends ISyncable {

    static <T extends IFactionPlayer<T> & ISkillPlayer<T>> Optional<IActionHandler<T>> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getActionHandler();
    }

    /**
     * Deactivate any active action and start cooldown timer
     */
    void deactivateAllActions();

    /**
     * If active, the remaining duration is extended by the giving duration
     */
    void extendActionTimer(Holder<? extends ILastingAction<T>> action, int duration);

    /**
     * @return A list of actions which currently are available to the player
     */
    List<IAction<T>> getAvailableActions();

    /**
     * @return A list of actions which currently are available to the player
     */
    List<Holder<? extends IAction<T>>> getAvailableActionsHolder();

    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if action is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if action is in cooldown
     *
     */
    float getPercentageForAction(Holder<? extends IAction<T>> action);

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

    List<Holder<? extends ILastingAction<T>>> getActiveActions();

    /**
     * Checks if the action is currently activated
     */
    boolean isActionActive(Holder<? extends ILastingAction<T>> action);

    boolean isActionOnCooldown(Holder<? extends IAction<T>> action);

    boolean isActionUnlocked(Holder<? extends IAction<T>> action);

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
    void resetTimer(Holder<? extends IAction<T>> action);

    /**
     * Toggle the action (server side).
     * If you just want to make sure it is deactivated, call {@link #deactivateAction(Holder)}
     *
     * @param action  Action
     * @param context Context holding Block/Entity the player was looking at when activating if any
     * @return result
     */
    IActionResult toggleAction(Holder<? extends IAction<T>> action, IAction.ActivationContext context);

    IActionResult checkDefaultToggleConditions(Holder<? extends IAction<T>> action);

    /**
     * Deactivate a lasting action, if it was active.
     */
    void deactivateAction(Holder<? extends ILastingAction<T>> action);

    /**
     * Lasting actions are deactivated here, which fires the {@link de.teamlapen.faction.api.event.ActionEvent.ActionDeactivatedEvent}
     *
     * @param action         - The lasting action being deactivated
     * @param ignoreCooldown - Whether the cooldown is ignored for the action
     * @param fullCooldown   - Whether the lasting action should get the full or reduced cooldown
     */
    void deactivateAction(Holder<? extends ILastingAction<T>> action, boolean ignoreCooldown, boolean fullCooldown);

    /**
     * Unlock the given actions. The given action have to belong to the players faction and have to be registered
     */
    void unlockActionHolder(Collection<Holder<? extends IAction<T>>> actions);
}
