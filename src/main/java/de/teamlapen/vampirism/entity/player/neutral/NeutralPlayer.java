package de.teamlapen.vampirism.entity.player.neutral;

import de.teamlapen.vampirism.api.VampirismAttachments;
import de.teamlapen.vampirism.api.entity.factions.IDisguise;
import de.teamlapen.vampirism.api.entity.player.neutral.INeutralPlayer;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
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
    public ResourceLocation getAttachedKey() {
        return VampirismAttachments.Keys.NEUTRAL_PLAYER;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return null;
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, NeutralPlayer> {

        @Override
        public @NotNull NeutralPlayer read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof Player player) {
                var neutral = new NeutralPlayer(player);
                neutral.deserializeNBT(provider, tag);
                return neutral;
            }
            throw new IllegalArgumentException("Holder is not a player");
        }


        @Override
        public CompoundTag write(NeutralPlayer attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, NeutralPlayer> {

        @Override
        public NeutralPlayer apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new NeutralPlayer(player);
            }
            throw new IllegalArgumentException("Cannot create neutral player attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }
}
