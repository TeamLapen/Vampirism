package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public record ServerboundSelectMinionTaskPacket(int minionID, ResourceLocation taskID) implements CustomPacketPayload {
    public static final Type<ServerboundSelectMinionTaskPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "select_minion_task"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSelectMinionTaskPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundSelectMinionTaskPacket::minionID,
            ResourceLocation.STREAM_CODEC, ServerboundSelectMinionTaskPacket::taskID,
            ServerboundSelectMinionTaskPacket::new
    );
    public final static ResourceLocation RECALL = new ResourceLocation(REFERENCE.MODID, "recall");
    public final static ResourceLocation RESPAWN = new ResourceLocation(REFERENCE.MODID, "respawn");

    public static void printRecoveringMinions(@NotNull ServerPlayer player, @NotNull List<MutableComponent> recoveringMinions) {
        if (recoveringMinions.size() == 1) {
            player.displayClientMessage(Component.translatable("text.vampirism.minion_is_still_recovering", recoveringMinions.get(0)), true);
        } else if (recoveringMinions.size() > 1) {
            player.displayClientMessage(Component.translatable("text.vampirism.n_minions_are_still_recovering", recoveringMinions.size()), true);
        }
    }


    public ServerboundSelectMinionTaskPacket {
        assert minionID >= -1;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
