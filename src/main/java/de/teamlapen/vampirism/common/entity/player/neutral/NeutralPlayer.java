package de.teamlapen.vampirism.common.entity.player.neutral;

import de.teamlapen.sync.common.storage.AttachmentSync;
import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.api.entity.factions.IDisguise;
import de.teamlapen.vampirism.api.entity.player.neutral.INeutralPlayer;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.entity.player.FactionBasePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class NeutralPlayer extends FactionBasePlayer<INeutralPlayer> implements INeutralPlayer {

    private final IDisguise disguise;

    public NeutralPlayer(Player player) {
        super(player);
        this.disguise = new IDisguise.None(ModFactions.NEUTRAL);
    }

    @Override
    public IDisguise getDisguise() {
        return this.disguise;
    }

    @Override
    public @NotNull ResourceLocation getAttachedKey() {
        return VampirismAttachments.Keys.NEUTRAL_PLAYER;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return null;
    }

    public static class AttachmentOptions extends AttachmentSync.PlayerOptions<NeutralPlayer> {
        @Override
        protected @NotNull NeutralPlayer create(@NotNull Player player) {
            return new NeutralPlayer(player);
        }

    }
}
