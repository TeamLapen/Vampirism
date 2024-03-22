package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @param actionId the id of the action that was toggled
 * @param target   The target the player was looking at when activating the action.
 */
public record ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable Either<Integer, BlockPos> target) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "toggle_action");
    public static final Codec<ServerboundToggleActionPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ResourceLocation.CODEC.fieldOf("action_id").forGetter(ServerboundToggleActionPacket::actionId),
                    ExtraCodecs.strictOptionalField(Codec.either(Codec.INT, BlockPos.CODEC),"target").forGetter(s -> Optional.ofNullable(s.target))
            ).apply(inst, ServerboundToggleActionPacket::new));

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
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
