package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
    private final ResourceLocation key;
    private boolean renderLevel = true;

    PlayableFaction(String name, Class<T> entityInterface, int color, ResourceLocation key, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel) {
        super(name, entityInterface, color);
        this.highestLevel = highestLevel;
        this.playerCapabilitySupplier = playerCapabilitySupplier;
        this.key = key;
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
    public ResourceLocation getKey() {
        return key;
    }

    @Override
    public T getPlayerCapability(PlayerEntity player) {
        return player.getCapability(playerCapabilitySupplier.get(), null).orElseThrow(() -> new IllegalStateException("Cannot get Faction Capability"));
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
                "name='" + name + '\'' +
                '}';
    }
}
