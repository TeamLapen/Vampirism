package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.extensions.IPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface for the player lord related data.
 */
public interface ILordPlayer extends IPlayer {

    /**
     * @return The faction of this lord player or null if not currently a lord
     */
    @NotNull
    Optional<Holder<? extends IPlayableFaction<?>>> getLordFaction();

    int getLordLevel();

    /**
     * @return Null, if level ==0
     */
    @Nullable
    Component getLordTitle();

    /**
     * @return Null, if level ==0
     */
    @Nullable
    Component getLordTitleShort();

    @NotNull
    Player getPlayer();

    /**
     * If the lord titles should use female versions (if available)
     */
    IPlayableFaction.TitleGender titleGender();
}
