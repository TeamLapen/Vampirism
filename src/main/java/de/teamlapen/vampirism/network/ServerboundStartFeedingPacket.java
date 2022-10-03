package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Client -> Server
 * Player has initiate feeding on an entity
 */
public record ServerboundStartFeedingPacket(Either<Integer, BlockPos> target) implements IMessage {
    static void encode(@NotNull ServerboundStartFeedingPacket msg, @NotNull FriendlyByteBuf buffer) {
        msg.target.ifLeft(entityID -> {
            buffer.writeBoolean(false);
            buffer.writeVarInt(entityID);
        });
        msg.target.ifRight(targetPos -> {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(targetPos);
        });
    }

    static @NotNull ServerboundStartFeedingPacket decode(@NotNull FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return new ServerboundStartFeedingPacket(buffer.readBlockPos());
        } else {
            return new ServerboundStartFeedingPacket(buffer.readVarInt());
        }
    }

    static void handle(final @NotNull ServerboundStartFeedingPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            VampirePlayer.getOpt(player).ifPresent(vampire -> {
                msg.target.ifLeft(vampire::biteEntity);
                msg.target.ifRight(vampire::biteBlock);
            });
        });
        ctx.setPacketHandled(true);
    }

    public ServerboundStartFeedingPacket(int entityID) {
        this(Either.left(entityID));
    }

    public ServerboundStartFeedingPacket(BlockPos targetPosition) {
        this(Either.right(targetPosition));
    }
}
