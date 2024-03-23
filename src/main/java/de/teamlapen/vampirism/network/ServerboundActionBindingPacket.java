package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ServerboundActionBindingPacket(int actionBindingId, @Nullable IAction<?> action) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "action_binding");
    public static final Codec<ServerboundActionBindingPacket> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("action_binding_id").forGetter(ServerboundActionBindingPacket::actionBindingId),
            ExtraCodecs.strictOptionalField(ModRegistries.ACTIONS.byNameCodec(), "action").forGetter(x -> Optional.ofNullable(x.action))
    ).apply(inst, ServerboundActionBindingPacket::new));

    public ServerboundActionBindingPacket(int actionBindingId) {
        this(actionBindingId, (IAction<?>) null);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ServerboundActionBindingPacket(int actionBindingId, @Nullable Optional<IAction<?>> action) {
        this(actionBindingId, action.orElse(null));
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
