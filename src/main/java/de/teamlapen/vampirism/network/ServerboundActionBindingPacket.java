package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ServerboundActionBindingPacket(int actionBindingId, @Nullable IAction<?> action) implements CustomPacketPayload {

    public static final Type<ServerboundActionBindingPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "action_binding"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundActionBindingPacket> CODEC = StreamCodec.<RegistryFriendlyByteBuf, ServerboundActionBindingPacket, Integer, IAction<?>>composite(
            ByteBufCodecs.VAR_INT, ServerboundActionBindingPacket::actionBindingId,
            ByteBufCodecs.optional(ByteBufCodecs.registry(VampirismRegistries.Keys.ACTION)).map(s -> s.orElse(null), Optional::ofNullable), pkt -> pkt.action,
            ServerboundActionBindingPacket::new
    );

    public ServerboundActionBindingPacket(int actionBindingId) {
        this(actionBindingId, (IAction<?>) null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
