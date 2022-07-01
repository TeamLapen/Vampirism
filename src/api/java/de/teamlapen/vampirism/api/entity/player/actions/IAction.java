package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interface for player actions
 */
public interface IAction extends IForgeRegistryEntry<IAction> {
    /**
     * Checks if the player can use this action
     *
     * @param player Must be instance of class that belongs to {@link IAction#getFaction()}
     */
    PERM canUse(IFactionPlayer player);

    /**
     * //TODO 1.17 remove
     * Use IFactionPlayer sensitive version
     *
     * @return Cooldown time in ticks until the action can be used again
     */
    @Deprecated
    int getCooldown();

    /**
     * @return Cooldown time in ticks until the action can be used again
     */
    default int getCooldown(IFactionPlayer player) {
        return getCooldown();
    }

    /**
     * @return the faction, which players can use this action
     */
    @Nonnull
    IPlayableFaction getFaction();

    default ITextComponent getName() {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * Use {@link IAction#getName()}
     */
    @Deprecated
    String getTranslationKey();


    /**
     * Deprecated. Override/call context-sensitive version instead
     * TODO 1.19 remove
     */
    @Deprecated
    boolean onActivated(IFactionPlayer player);

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player Must be instance of class that belongs to {@link IAction#getFaction()}
     * @param context Holds Block/Entity the player was looking at when activating if any
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    default boolean onActivated(IFactionPlayer<?> player, ActivationContext context){
        return this.onActivated(player);
    }

    /**
     * @return if the action should be shown in the action select screen
     */
    default boolean showInSelectAction(PlayerEntity player) {
        return true;
    }

    /**
     * @return if the action's cooldown should be rendered in the HUD
     */
    default boolean showHudCooldown(PlayerEntity player) {
        return false;
    }

    enum PERM {
        ALLOWED, DISABLED, NOT_UNLOCKED, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
    }

    /**
     * Provide some context of the activation instant sent from client
     */
    interface ActivationContext{
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
