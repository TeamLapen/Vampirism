package de.teamlapen.vampirism.common.network.packets.server;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.inventory.ITaskMenu;
import de.teamlapen.vampirism.common.serialization.ModStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          ITaskMenu.TaskAction action) implements CustomPacketPayload {

    public static final Type<ServerboundTaskActionPacket> TYPE = new Type<>(VResourceLocation.mod("task_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTaskActionPacket> CODEC = StreamCodec.composite(
            ModStreamCodecs.UUID, ServerboundTaskActionPacket::task,
            ModStreamCodecs.UUID, ServerboundTaskActionPacket::entityId,
            NeoForgeStreamCodecs.enumCodec(ITaskMenu.TaskAction.class), ServerboundTaskActionPacket::action,
            ServerboundTaskActionPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
