package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<?>> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final int highestLordLevel;
    private final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
    private boolean renderLevel = true;

    PlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel, int highestLordLevel, @Nonnull IVillageFactionData villageFactionData) {
        super(id, entityInterface, color, hostileTowardsNeutral, villageFactionData);
        this.highestLevel = highestLevel;
        this.playerCapabilitySupplier = playerCapabilitySupplier;
        this.highestLordLevel = highestLordLevel;
    }

    @Override
    public Class<T> getFactionEntityInterface() {
        return super.getFactionEntityInterface();
    }

    @Override
    public Class<T> getFactionPlayerInterface() {
        return super.getFactionEntityInterface();
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }


    @Override
    public LazyOptional<T> getPlayerCapability(PlayerEntity player) {
        return player.getCapability(playerCapabilitySupplier.get(), null);
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
    public int getHighestLordLevel() {
        return highestLordLevel;
    }

    @Override
    public String toString() {
        return "PlayableFaction{" +
                "id='" + id + '\'' +
                '}';
    }
}
