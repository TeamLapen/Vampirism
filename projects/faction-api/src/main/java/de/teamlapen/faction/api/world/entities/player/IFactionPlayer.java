package de.teamlapen.faction.api.world.entities.player;

import de.teamlapen.faction.api.factions.*;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.sync.api.IAttachmentSync;
import net.minecraft.core.Holder;

/**
 * Basic interface for all of Vampirism's player types (VampirePlayer, HunterPlayer, ...)
 * The child classes are used as capabilities and attached to the player.
 * A player can have levels.
 * A player can only be part of one faction at once; this means only one IFactionPlayer capability belonging to a single player can have a level >0.
 * <p>
 * If you are writing an addon and not a standalone mod, consider extending FactionPlayerBase instead of implementing this
 */
public interface IFactionPlayer<T extends IFactionPlayer<T>> extends IFactionEntity, IPlayer, IAttachmentSync {
    /**
     * Mostly relevant in the set level command
     * Vampirism's factions always return true here.
     * Can be used if another mod does not want that a player leaves its faction via the command
     */
    @SuppressWarnings("SameReturnValue")
    boolean canLeaveFaction();

    IDisguise getDisguise();

    /**
     * Preferably implement this by calling {@link IFactionPlayerHandler#getCurrentLevel(net.minecraft.core.Holder)}
     *
     * @return 0 if the player is not part of this faction, something > 0 if the player is part of the faction.
     */
    int getLevel();

    /**
     * @return Max level this player type can reach
     */
    int getMaxLevel();

    @Override
    Holder<? extends IPlayableFaction<?>> getFaction();

    default boolean is(Holder<? extends IFaction<?>> faction) {
        return IFaction.is(faction, getFaction());
    }

    /**
     * You can also use {@link IFactionPlayer#getDisguise()} to get the faction the player looks like
     *
     * @return If the player is disguised.
     */
    boolean isDisguised();

    /**
     * Returns false for a null world
     *
     * @return if the player is in a remote world
     */
    boolean isRemote();

    void leaveFaction();

    void levelChanged(LevelingChange changes);

}
