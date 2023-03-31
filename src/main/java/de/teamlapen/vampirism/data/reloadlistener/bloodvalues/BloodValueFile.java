package de.teamlapen.vampirism.data.reloadlistener.bloodvalues;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record BloodValueFile(List<BloodValueBuilder.Entry> values, boolean replace) {
    public static final Codec<BloodValueFile> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(BloodValueBuilder.Entry.CODEC.listOf().fieldOf("values").forGetter(BloodValueFile::values), Codec.BOOL.optionalFieldOf("replace", Boolean.FALSE).forGetter(BloodValueFile::replace)).apply(instance, BloodValueFile::new);
    });
}
