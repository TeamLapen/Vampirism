package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BottleBlood(int blood) {

    public static final BottleBlood EMPTY = new BottleBlood(0);
    public static final Codec<BottleBlood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("blood").forGetter(BottleBlood::blood)
    ).apply(instance, BottleBlood::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BottleBlood> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, BottleBlood::blood, BottleBlood::new);

    public BottleBlood {
        if (blood < 0 || blood > 9) {
            throw new IllegalArgumentException("Blood amount must be between 0 and 9");
        }
    }
}
