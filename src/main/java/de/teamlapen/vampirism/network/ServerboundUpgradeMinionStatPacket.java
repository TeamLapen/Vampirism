package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record ServerboundUpgradeMinionStatPacket(int entityId, int statId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "upgrade_minion_stat");
    public static final Codec<ServerboundUpgradeMinionStatPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("entityId").forGetter(ServerboundUpgradeMinionStatPacket::entityId),
                    Codec.INT.fieldOf("statId").forGetter(ServerboundUpgradeMinionStatPacket::statId)
            ).apply(inst, ServerboundUpgradeMinionStatPacket::new)
    );

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
