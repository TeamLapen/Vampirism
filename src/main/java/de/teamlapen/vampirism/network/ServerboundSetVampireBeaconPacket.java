package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ServerboundSetVampireBeaconPacket(Optional<MobEffect> effect, Optional<Integer> amplifier) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "set_vampire_beacon");
    public static final Codec<ServerboundSetVampireBeaconPacket> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                BuiltInRegistries.MOB_EFFECT.byNameCodec().optionalFieldOf("effect").forGetter(l -> l.effect),
                Codec.INT.optionalFieldOf("amplifier").forGetter(l -> l.amplifier)
        ).apply(builder, ServerboundSetVampireBeaconPacket::new);
    });

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
