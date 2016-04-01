package de.teamlapen.vampirism.api.entity.player.actions;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for player capability that can use actions
 */
public interface IActionPlayer<T extends IActionPlayer> {
    IActionHandler<T> getActionHandler();

    EntityPlayer getRepresentingPlayer();
}
