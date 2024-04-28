package de.teamlapen.vampirism.items.component;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record SwordTraining(Map<UUID, Float> training) {

    public static final SwordTraining EMPTY = new SwordTraining(ImmutableMap.of());

    public static final Codec<SwordTraining> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.FLOAT).xmap(SwordTraining::new, SwordTraining::training);
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordTraining> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(s -> new HashMap<>(), UUIDUtil.STREAM_CODEC, ByteBufCodecs.FLOAT), SwordTraining::training, SwordTraining::new);

    public SwordTraining(Map<UUID, Float> training) {
        this.training = Collections.unmodifiableMap(training);
    }

    public SwordTraining addTraining(UUID id, float amount) {
        Map<UUID, Float> newTraining = new HashMap<>(training);
        newTraining.put(id, amount);
        return new SwordTraining(newTraining);
    }

}
