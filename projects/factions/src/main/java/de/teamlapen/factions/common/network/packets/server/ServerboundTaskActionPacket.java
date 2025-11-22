package de.teamlapen.factions.common.network.packets.server;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.inventory.ITaskMenu;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          ITaskMenu.TaskAction action) implements CustomPacketPayload {

    public static final Type<ServerboundTaskActionPacket> TYPE = new Type<>(FResourceLocation.mod("task_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTaskActionPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ServerboundTaskActionPacket::task,
            UUIDUtil.STREAM_CODEC, ServerboundTaskActionPacket::entityId,
            NeoForgeStreamCodecs.enumCodec(ITaskMenu.TaskAction.class), ServerboundTaskActionPacket::action,
            ServerboundTaskActionPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
