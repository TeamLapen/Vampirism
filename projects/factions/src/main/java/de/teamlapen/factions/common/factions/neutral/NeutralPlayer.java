package de.teamlapen.factions.common.factions.neutral;

import de.teamlapen.factions.api.factions.IDisguise;
import de.teamlapen.factions.api.factions.LevelingChange;
import de.teamlapen.factions.api.world.entities.player.INeutralPlayer;
import de.teamlapen.factions.common.core.DefaultFactions;
import de.teamlapen.factions.common.core.FactionAttachments;
import de.teamlapen.factions.common.factions.FactionBasePlayer;
import de.teamlapen.factions.common.util.AttachmentSynchronization;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

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
    public void levelChanged(LevelingChange changes) {

    }

    @Override
    public void leaveFaction() {

    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<NeutralPlayer> {
        @Override
        protected NeutralPlayer create(Player player) {
            return new NeutralPlayer(player);
        }
    }
}
