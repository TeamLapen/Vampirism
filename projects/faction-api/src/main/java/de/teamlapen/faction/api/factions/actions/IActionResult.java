package de.teamlapen.faction.api.factions.actions;

import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

/**
 * The result of an attempt to activate an action, active the action, or check if an action can be activated
 */
public interface IActionResult {

    IActionResult SUCCESS = new Result(true, Component.empty());
    IActionResult ON_COOLDOWN = new Result(false, Component.translatable("message.factionapi.action.on_cooldown"));
    IActionResult RESTRICTED = new Result(false, Component.translatable("message.factionapi.action.blocked"));
    IActionResult NOT_UNLOCKED = new Result(false, Component.translatable("message.factionapi.action.not_unlocked"));
    IActionResult DISALLOWED_PERMISSION = new Result(false, Component.translatable("message.factionapi.action.no_permission"), false);
    IActionResult DISABLED_CONFIG = new Result(false, Component.translatable("message.factionapi.action.disabled_by_admin"), false);
    IActionResult DISALLOWED_FACTION = new Result(false, Component.translatable("message.factionapi.action.wrong_faction"), false);
    IActionResult DISABLED_EFFECT = new Result(false, Component.translatable("message.factionapi.action.blocked_by_effect"));

    /**
     * Creates a fail result with the given message
     */
    static IActionResult fail(Component message) {
        return new Result(false, message);
    }

    /**
     * Creates a fail result if the other action is active
     */
    static <T extends ISkillPlayer<T>> IActionResult otherAction(IActionHandler<T> handler, Holder<? extends ILastingAction<T>> otherAction) {
        return handler.isActionActive(otherAction) ? new Result(false, Component.translatable("message.factionapi.action.conflicts_with", otherAction.value().getName())) : SUCCESS;
    }

    /**
     * If the action was successfully activated or can be activated
     */
    boolean successful();

    /**
     * The message to be displayed to the player if the action activation was not successful
     */
    Component message();

    /**
     * If the message should be sent to the status bar or the chat
     */
    boolean sendToStatusBar();

    record Result(boolean successful, Component message, boolean sendToStatusBar) implements IActionResult {

        Result(boolean successful, Component message) {
            this(successful, message, true);
        }
    }
}
