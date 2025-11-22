package de.teamlapen.factions.common.factions;

import com.mojang.logging.LogUtils;
import de.teamlapen.factions.api.FactionApi;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.entities.IPlayerEventListener;
import de.teamlapen.sync.Attachment;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
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
    public Player asEntity() {
        return this.player;
    }

    @Override
    public int getLevel() {
        return FactionApi.factionPlayerHandler(player).getCurrentLevel(getFaction());
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
}
