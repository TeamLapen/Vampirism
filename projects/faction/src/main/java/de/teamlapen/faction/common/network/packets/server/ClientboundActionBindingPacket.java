package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ClientboundActionBindingPacket(ActionKeys actionBindingId, @Nullable Holder<? extends IAction<?>> action) implements CustomPacketPayload {

    public static final Type<ClientboundActionBindingPacket> TYPE = new Type<>(FIdentifier.mod("action_binding"));
    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundActionBindingPacket> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ActionKeys.class), ClientboundActionBindingPacket::actionBindingId,
            ByteBufCodecs.optional(((StreamCodec<RegistryFriendlyByteBuf, Holder<? extends IAction<?>>>)(Object)ByteBufCodecs.holderRegistry(FactionRegistries.Keys.ACTION))), pkt -> (Optional<Holder<? extends IAction<?>>>) (Object)Optional.ofNullable(pkt.action),
            ClientboundActionBindingPacket::new
    );

    public ClientboundActionBindingPacket(ActionKeys actionBindingId) {
        this(actionBindingId, (Holder<? extends IAction<?>>) null);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ClientboundActionBindingPacket(ActionKeys actionBindingId, Optional<Holder<? extends IAction<?>>> action) {
        this(actionBindingId, action.orElse(null));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
