package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interface for player actions
 */
public interface IAction<T extends IFactionPlayer<T>> {
    /**
     * Checks if the player can use this action
     *
     * @param player Must be an instance of class that belongs to {@link IAction#getFaction()}
     */
    PERM canUse(T player);


    /**
     * @return Cooldown time in ticks until the action can be used again
     */
    int getCooldown(T player);

    /**
     * @return the faction, which players can use this action
     */
    @Nonnull
    Optional<IPlayableFaction<?>> getFaction();

    default Component getName() {
        return Component.translatable(getTranslationKey());
    }

    /**
     * Use {@link IAction#getName()}
     */
    @Deprecated
    String getTranslationKey();

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player Must be an instance of class that belongs to {@link IAction#getFaction()}
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(T player);

    /**
     * @return if the action should be shown in the action select screen
     */
    @SuppressWarnings("SameReturnValue")
    default boolean showInSelectAction(Player player) {
        return true;
    }

    /**
     * @return if the action's cooldown should be rendered in the HUD
     */
    default boolean showHudCooldown(Player player) {
        return false;
    }

    enum PERM {
        ALLOWED, DISABLED, NOT_UNLOCKED, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
    }
}
