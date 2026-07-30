package de.teamlapen.faction.common.factions.neutral;

import de.teamlapen.faction.api.factions.IDisguise;
import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.api.world.entities.player.INeutralPlayer;
import de.teamlapen.faction.common.core.DefaultFactions;
import de.teamlapen.faction.common.core.FactionAttachments;
import de.teamlapen.faction.common.factions.FactionBasePlayer;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

public class NeutralPlayer extends FactionBasePlayer<INeutralPlayer> implements INeutralPlayer {

    private final IDisguise disguise;

    public NeutralPlayer(Player player) {
        super(player);
        this.disguise = new IDisguise.None(DefaultFactions.NEUTRAL);
    }

    @Override
    public IDisguise getDisguise() {
        return this.disguise;
    }

    @Override
    public AttachmentType<?> getType() {
        return FactionAttachments.NEUTRAL_PLAYER.get();
    }

    @Override
    public void levelChanged(FactionUpdate changes) {

    }

    @Override
    public void leaveFaction() {

    }

    @Override
    public @Nullable Component getShortLevelDisplay() {
        return null;
    }

    @Override
    public @Nullable Component getLevelDisplay() {
        return null;
    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<NeutralPlayer> {
        @Override
        protected NeutralPlayer create(Player player) {
            return new NeutralPlayer(player);
        }
    }
}
