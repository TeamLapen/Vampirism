package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.storage.Attachment;
import de.teamlapen.lib.lib.storage.UpdateParams;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 */
public abstract class FactionBasePlayer<T extends IFactionPlayer<T>> extends Attachment implements IFactionPlayer<T>, IPlayerEventListener {

    private static final Logger LOGGER = LogManager.getLogger(FactionBasePlayer.class);

    protected final Player player;

    public FactionBasePlayer(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player asEntity() {
        return this.player;
    }

    @Override
    public int getLevel() {
        return VampirismAPI.factionPlayerHandler(player).getCurrentLevel(getFaction());
    }

    /**
     * Only use this if you are dealing with minions.
     * Otherwise use {@link FactionBasePlayer#getRepresentingPlayer()}
     */
    @Override
    public LivingEntity getRepresentingEntity() {
        return player;
    }

    @Override
    public Player getRepresentingPlayer() {
        return player;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isRemote() {
        if (player.level() == null) {
            LOGGER.error("Trying to check if remote, but world is not set yet", new Throwable("World not loaded").fillInStackTrace());
            return false;
        }
        return player.level().isClientSide;
    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {

    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeUpdateNBTInternal(HolderLookup.@NotNull Provider provider, UpdateParams sendAllData) {
        return new CompoundTag();
    }

}
