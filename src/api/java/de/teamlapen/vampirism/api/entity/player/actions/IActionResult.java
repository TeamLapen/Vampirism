package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

/**
 * The result of an attempt to activate an action, active the action or check if an action can be activated
 */
public interface IActionResult {

    IActionResult SUCCESS = new Result(true, Component.empty());
    IActionResult ON_COOLDOWN = new Result(false, Component.translatable("text.vampirism.action.cooldown_not_over"));
    IActionResult RESTRICTED = new Result(false, Component.translatable("text.vampirism.action.restricted"));
    IActionResult NOT_UNLOCKED = new Result(false, Component.translatable("text.vampirism.action.not_unlocked"));
    IActionResult DISALLOWED_PERMISSION = new Result(false, Component.translatable("text.vampirism.action.permission_disallowed"), false);
    IActionResult DISABLED_CONFIG = new Result(false, Component.translatable("text.vampirism.action.deactivated_by_serveradmin"), false);
    IActionResult DISALLOWED_FACTION = new Result(false, Component.translatable("text.vampirism.action.invalid_faction"), false);
    IActionResult DISABLED_EFFECT = new Result(false, Component.translatable("text.vampirism.action.active_effect"));


    /**
     * If the action was successful activated or can be activated
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
        return handler.isActionActive(otherAction) ? new Result(false, Component.translatable("text.vampirism.action.other_action", Component.translatable(Util.makeDescriptionId("action", otherAction.unwrapKey().map(ResourceKey::location).orElseThrow())))) : SUCCESS;
    }
}
