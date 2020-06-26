package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

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
     * @return Cooldown time in ticks until the action can be used again
     */
    int getCooldown();

    /**
     * @return the faction, which players can use this action
     */
    @Nonnull
    IPlayableFaction getFaction();

    String getTranslationKey();

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player Must be instance of class that belongs to {@link IAction#getFaction()}
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(IFactionPlayer player);

    default boolean showInSelectAction(PlayerEntity player) {
        return true;
    }

    enum PERM {
        ALLOWED, DISABLED, NOT_UNLOCKED, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
    }
}
