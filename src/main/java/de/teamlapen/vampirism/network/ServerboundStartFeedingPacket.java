package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundStartFeedingPacket(Either<Integer, BlockContact> target) implements CustomPacketPayload {

    public static final Type<ServerboundStartFeedingPacket> TYPE = new Type<>(VResourceLocation.mod("start_feeding"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundStartFeedingPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.either(ByteBufCodecs.VAR_INT, BlockContact.STREAM_CODEC), ServerboundStartFeedingPacket::target,
            ServerboundStartFeedingPacket::new
    );

    public ServerboundStartFeedingPacket(int entityID) {
        this(Either.left(entityID));
    }

    public ServerboundStartFeedingPacket(BlockPos pos, Direction side) {
        this(Either.right(new BlockContact(pos, side)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record BlockContact(BlockPos pos, Direction side) {

        public static final StreamCodec<RegistryFriendlyByteBuf, BlockContact> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, BlockContact::pos,
                Direction.STREAM_CODEC, BlockContact::side,
                BlockContact::new
        );
    }
}
