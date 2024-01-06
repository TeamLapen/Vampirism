package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record ServerboundAppearancePacket(int entityId, String name, int... data) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "appearance");
    public static final Codec<ServerboundAppearancePacket> CODEC = RecordCodecBuilder.create(inst ->
    inst.group(
            Codec.INT.fieldOf("entityId").forGetter(ServerboundAppearancePacket::entityId),
            Codec.STRING.fieldOf("name").forGetter(ServerboundAppearancePacket::name),
            Codec.INT.listOf().fieldOf("data").xmap(s -> s.stream().mapToInt(l -> l).toArray(), s -> Arrays.stream(s).boxed().toList()).forGetter(s -> s.data)
    ).apply(inst, ServerboundAppearancePacket::new));

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
