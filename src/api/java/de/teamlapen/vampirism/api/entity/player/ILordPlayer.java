package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for the player lord related data.
 */
public interface ILordPlayer {

    /**
     * @return The faction of this lord player or null if not currently a lord
     */
    @Nullable
    IPlayableFaction<?> getLordFaction();

    int getLordLevel();

    @Nonnull
    PlayerEntity getPlayer();
}
