package de.teamlapen.vampirism.items.component;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record SwordTraining(float charged, Map<UUID, Float> training) {

    public static final SwordTraining EMPTY = new SwordTraining(0, ImmutableMap.of());
    public static final Codec<SwordTraining> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("charging").forGetter(SwordTraining::charged),
            Codec.unboundedMap(UUIDUtil.CODEC, Codec.FLOAT).fieldOf("training").forGetter(SwordTraining::training)
    ).apply(instance, SwordTraining::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SwordTraining> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SwordTraining::charged,
            ByteBufCodecs.map(s -> (Map<UUID, Float>) new HashMap<UUID, Float>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.FLOAT).map(ImmutableMap::copyOf, s -> s), SwordTraining::training,
            SwordTraining::new
    );

    public SwordTraining charge(float charged) {
        return new SwordTraining(Mth.clamp(charged, 0, 1), training);
    }

    public SwordTraining addTraining(UUID id, float amount) {
        Map<UUID, Float> newTraining = new HashMap<>(training);
        newTraining.put(id, amount);
        return new SwordTraining(charged, newTraining);
    }

}
