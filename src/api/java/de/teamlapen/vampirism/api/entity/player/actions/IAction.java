package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillLike;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface for player actions
 */
public interface IAction<T extends IFactionPlayer<T>> extends ISkillLike<T> {
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
    @NotNull
    Optional<IPlayableFaction<?>> getFaction(); //TODO might be a good idea to have a more useful return type, that allow more that all or one faction

    /**
     * @param faction The faction to test
     * @return of the action can be used by the given faction
     */
    default boolean matchesFaction(@Nullable IPlayableFaction<?> faction) {
        if (getFaction().isEmpty() && faction == null) {
            return true;
        }
        return getFaction().map(f -> f == faction).orElse(true);
    }

    default MutableComponent getName() {
        return Component.translatable(getTranslationKey());
    }

    String getTranslationKey();

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player  Must be instance of class that belongs to {@link IAction#getFaction()}
     * @param context Holds Block/Entity the player was looking at when activating if any
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(T player, ActivationContext context);

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

    ISkill<T> asSkill();

    enum PERM {
        /**
         * The player can use the action
         */
        ALLOWED,
        /**
         * The action is disabled in the config
         */
        DISABLED,
        /**
         * The player does not have the action unlocked
         */
        NOT_UNLOCKED,
        /**
         * The player is not in the position to use the action.
         * <p>
         * This is the case if the player is in spectator mode or the action rejects the player
         */
        DISALLOWED,
        /**
         * The action is on cooldown and cannot be used.
         * <p>
         * Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
         */
        COOLDOWN,
        /**
         * The user does not have the correct permission to use the action {@link de.teamlapen.vampirism.util.Permissions#ACTION}
         */
        @SuppressWarnings("JavadocReference")
        PERMISSION_DISALLOWED
    }

    /**
     * Provide some context of the activation instant sent from client
     */
    interface ActivationContext {
        /**
         * @return The block the player is looking at, if any
         */
        Optional<BlockPos> targetBlock();

        /**
         * @return The creature the player is looking at, if any
         */
        Optional<Entity> targetEntity();
    }
}
