package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final String playerProp;
    private boolean renderLevel = true;

    PlayableFaction(String name, Class<T> entityInterface, int color, String playerProp, int highestLevel) {
        super(name, entityInterface, color);
        this.highestLevel = highestLevel;
        this.playerProp = playerProp;
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }

    @Override
    public T getPlayerProp(EntityPlayer player) {
        return (T) player.getExtendedProperties(prop());
    }

    @Override
    public String prop() {
        return playerProp;
    }

    @Override
    public boolean renderLevel() {
        return renderLevel;
    }

    @Override
    public PlayableFaction<T> setRenderLevel(boolean render) {
        renderLevel = render;
        return this;
    }

    @Override
    public String toString() {
        return "PlayableFaction{" +
                "playerProp='" + playerProp + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
