package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ServerboundStartFeedingPacket(Either<Integer, BlockPos> target) implements CustomPacketPayload {
    public static final Type<ServerboundStartFeedingPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "start_feeding"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundStartFeedingPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.either(ByteBufCodecs.VAR_INT, BlockPos.STREAM_CODEC), ServerboundStartFeedingPacket::target,
            ServerboundStartFeedingPacket::new
    );


    public ServerboundStartFeedingPacket(int entityID) {
        this(Either.left(entityID));
    }

    public ServerboundStartFeedingPacket(BlockPos targetPosition) {
        this(Either.right(targetPosition));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
