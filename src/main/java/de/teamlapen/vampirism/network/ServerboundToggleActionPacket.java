package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @param actionId the id of the action that was toggled
 * @param target   The target the player was looking at when activating the action.
 */
public record ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable Either<Integer, BlockPos> target) implements CustomPacketPayload {
    public static final Type<ServerboundToggleActionPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "toggle_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleActionPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ServerboundToggleActionPacket::actionId,
            NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.either(ByteBufCodecs.VAR_INT, BlockPos.STREAM_CODEC)), pkt -> pkt.target,
            ServerboundToggleActionPacket::new
    );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ServerboundToggleActionPacket(ResourceLocation action, Optional<Either<Integer, BlockPos>> target) {
        this(action, target.orElse(null));
    }

    public static @NotNull ServerboundToggleActionPacket createFromRaytrace(ResourceLocation action, @Nullable HitResult traceResult) {
        if (traceResult != null) {
            if (traceResult.getType() == HitResult.Type.ENTITY) {
                return new ServerboundToggleActionPacket(action, ((EntityHitResult) traceResult).getEntity().getId());
            } else if (traceResult.getType() == HitResult.Type.BLOCK) {
                return new ServerboundToggleActionPacket(action, ((BlockHitResult) traceResult).getBlockPos());
            }
        }
        return new ServerboundToggleActionPacket(action);
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable Integer target) {
        this(actionId, Either.left(target));
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable BlockPos target) {
        this(actionId, Either.right(target));
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId) {
        this(actionId, (Either<Integer, BlockPos>) null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
