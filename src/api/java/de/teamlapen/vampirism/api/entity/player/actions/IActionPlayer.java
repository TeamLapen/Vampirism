package de.teamlapen.vampirism.api.entity.player.actions;

import net.minecraft.world.entity.player.Player;

/**
 * Interface for player capability that can use actions
 * TODO 1.17 remove this and skillplayer, just use faction player
 */
public interface IActionPlayer<T extends IActionPlayer> {
    IActionHandler<T> getActionHandler();

    Player getRepresentingPlayer();
}
