package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @param action the id of the action that was toggled
 * @param target   The target the player was looking at when activating the action.
 */
public record ServerboundToggleActionPacket(Holder<IAction<?>> action, @Nullable Either<Integer, BlockPos> target) implements CustomPacketPayload {
    public static final Type<ServerboundToggleActionPacket> TYPE = new Type<>(VResourceLocation.mod("toggle_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleActionPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.ACTION), ServerboundToggleActionPacket::action,
            ByteBufCodecs.optional(ByteBufCodecs.either(ByteBufCodecs.VAR_INT, BlockPos.STREAM_CODEC)), pkt -> Optional.ofNullable(pkt.target),
            ServerboundToggleActionPacket::new
    );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ServerboundToggleActionPacket(Holder<IAction<?>> action, Optional<Either<Integer, BlockPos>> target) {
        this(action, target.orElse(null));
    }

    public static @NotNull ServerboundToggleActionPacket createFromRaytrace(Holder<IAction<?>> action, @Nullable HitResult traceResult) {
        if (traceResult != null) {
            if (traceResult.getType() == HitResult.Type.ENTITY) {
                return new ServerboundToggleActionPacket(action, ((EntityHitResult) traceResult).getEntity().getId());
            } else if (traceResult.getType() == HitResult.Type.BLOCK) {
                return new ServerboundToggleActionPacket(action, ((BlockHitResult) traceResult).getBlockPos());
            }
        }
        return new ServerboundToggleActionPacket(action);
    }

    public ServerboundToggleActionPacket(Holder<IAction<?>> action, @Nullable Integer target) {
        this(action, Either.left(target));
    }

    public ServerboundToggleActionPacket(Holder<IAction<?>> action, @Nullable BlockPos target) {
        this(action, Either.right(target));
    }

    public ServerboundToggleActionPacket(Holder<IAction<?>> action) {
        this(action, (Either<Integer, BlockPos>) null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
