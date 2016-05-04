package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final Capability<T> playerCapability;
    private final ResourceLocation key;
    private boolean renderLevel = true;

    PlayableFaction(String name, Class<T> entityInterface, int color, ResourceLocation key, Capability<T> playerCapability, int highestLevel) {
        super(name, entityInterface, color);
        this.highestLevel = highestLevel;
        this.playerCapability = playerCapability;
        this.key = key;
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }

    @Override
    public ResourceLocation getKey() {
        return key;
    }

    @Override
    public T getPlayerCapability(EntityPlayer player) {
        return player.getCapability(playerCapability, null);
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
                "playerCapability='" + playerCapability + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
