package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundTaskActionPacket(UUID task, UUID entityId,
                                          TaskMenu.TaskAction action) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "task_action");
    public static final Codec<ServerboundTaskActionPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ClientboundTaskPacket.UUID_CODEC.fieldOf("task").forGetter(ServerboundTaskActionPacket::task),
                    ClientboundTaskPacket.UUID_CODEC.fieldOf("entityId").forGetter(ServerboundTaskActionPacket::entityId),
                    StringRepresentable.fromEnum(TaskMenu.TaskAction::values).fieldOf("action").forGetter(ServerboundTaskActionPacket::action)
            ).apply(inst, ServerboundTaskActionPacket::new));


    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
