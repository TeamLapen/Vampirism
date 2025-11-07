package de.teamlapen.vampirism.common.entity.player;

import com.mojang.logging.LogUtils;
import de.teamlapen.sync.common.entities.IPlayerEventListener;
import de.teamlapen.sync.common.storage.Attachment;
import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 */
public abstract class FactionBasePlayer<T extends IFactionPlayer<T>> extends Attachment implements IFactionPlayer<T>, IPlayerEventListener {

    protected static final Logger LOGGER = LogUtils.getLogger();

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isRemote() {
        if (player.level() == null) {
            LOGGER.error("Trying to check if remote, but world is not set yet", new Throwable("World not loaded").fillInStackTrace());
            return false;
        }
        return player.level().isClientSide();
    }

    @MustBeInvokedByOverriders
    @Override
    public void serialize(@NotNull ValueOutput output) {
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserialize(@NotNull ValueInput input) {
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeUpdate(@NotNull ValueInput input) {

    }

    @MustBeInvokedByOverriders
    @Override
    public void serializeUpdateInternal(ValueOutput output, UpdateParams sendAllData) {
    }


}
