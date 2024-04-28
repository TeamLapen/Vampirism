package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundUpgradeMinionStatPacket(int entityId, int statId) implements CustomPacketPayload {

    public static final Type<ServerboundUpgradeMinionStatPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "upgrade_minion_stat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUpgradeMinionStatPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundUpgradeMinionStatPacket::entityId,
            ByteBufCodecs.VAR_INT, ServerboundUpgradeMinionStatPacket::statId,
            ServerboundUpgradeMinionStatPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
