package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.player.ActionKeys;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ServerboundActionBindingPacket(ActionKeys actionBindingId, @Nullable Holder<IAction<?>> action) implements CustomPacketPayload {

    public static final Type<ServerboundActionBindingPacket> TYPE = new Type<>(VResourceLocation.mod("action_binding"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundActionBindingPacket> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ActionKeys.class), ServerboundActionBindingPacket::actionBindingId,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.ACTION)).map(s -> s.orElse(null), Optional::ofNullable), pkt -> pkt.action,
            ServerboundActionBindingPacket::new
    );

    public ServerboundActionBindingPacket(ActionKeys actionBindingId) {
        this(actionBindingId, null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
