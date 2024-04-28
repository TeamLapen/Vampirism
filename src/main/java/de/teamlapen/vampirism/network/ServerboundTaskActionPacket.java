package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.TaskMenu;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          TaskMenu.TaskAction action) implements CustomPacketPayload {

    public static final Type<ServerboundTaskActionPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "task_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTaskActionPacket> CODEC = StreamCodec.composite(
            ByteBufferCodecUtil.UUID, ServerboundTaskActionPacket::task,
            ByteBufferCodecUtil.UUID, ServerboundTaskActionPacket::entityId,
            NeoForgeStreamCodecs.enumCodec(TaskMenu.TaskAction.class), ServerboundTaskActionPacket::action,
            ServerboundTaskActionPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
