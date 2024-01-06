package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Client -> Server
 * Player has initiate feeding on an entity
 */
public record ServerboundStartFeedingPacket(Either<Integer, BlockPos> target) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "start_feeding");
    public static final Codec<ServerboundStartFeedingPacket> CODEC = Codec.either(Codec.INT, BlockPos.CODEC).xmap(ServerboundStartFeedingPacket::new, msg -> msg.target);


    public ServerboundStartFeedingPacket(int entityID) {
        this(Either.left(entityID));
    }

    public ServerboundStartFeedingPacket(BlockPos targetPosition) {
        this(Either.right(targetPosition));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
