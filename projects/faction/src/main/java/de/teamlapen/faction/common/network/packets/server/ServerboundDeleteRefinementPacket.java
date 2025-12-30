package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;


public record ServerboundDeleteRefinementPacket(IRefinementItem.AccessorySlotType slot) implements CustomPacketPayload {

    public static final Type<ServerboundDeleteRefinementPacket> TYPE = new Type<>(FIdentifier.mod("delete_refinement"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDeleteRefinementPacket> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(IRefinementItem.AccessorySlotType.class), ServerboundDeleteRefinementPacket::slot,
            ServerboundDeleteRefinementPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
