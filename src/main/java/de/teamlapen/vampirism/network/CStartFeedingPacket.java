package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

/**
 * Client -> Server
 * Player has initiate feeding on an entity
 */
public class CStartFeedingPacket implements IMessage {
    static void encode(CStartFeedingPacket msg, FriendlyByteBuf buffer) {
        msg.target.ifLeft(entityID -> {
            buffer.writeBoolean(false);
            buffer.writeVarInt(entityID);
        });
        msg.target.ifRight(targetPos -> {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(targetPos);
        });
    }

    static CStartFeedingPacket decode(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return new CStartFeedingPacket(buffer.readBlockPos());
        } else {
            return new CStartFeedingPacket(buffer.readVarInt());
        }
    }

    static void handle(final CStartFeedingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
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

    private final Either<Integer, BlockPos> target;

    public CStartFeedingPacket(int entityID) {
        this.target = Either.left(entityID);
    }

    public CStartFeedingPacket(BlockPos targetPosition) {
        this.target = Either.right(targetPosition);
    }
}
