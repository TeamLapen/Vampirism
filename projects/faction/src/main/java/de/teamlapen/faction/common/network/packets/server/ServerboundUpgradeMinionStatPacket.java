package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public record ServerboundUpgradeMinionStatPacket(int entityId, Holder<DataComponentType<?>> stat) implements CustomPacketPayload {

    public static final Type<ServerboundUpgradeMinionStatPacket> TYPE = new Type<>(FIdentifier.mod("upgrade_minion_stat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUpgradeMinionStatPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundUpgradeMinionStatPacket::entityId,
            ByteBufCodecs.holderRegistry(Registries.DATA_COMPONENT_TYPE), ServerboundUpgradeMinionStatPacket::stat,
            ServerboundUpgradeMinionStatPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
