package de.teamlapen.vampirism.datamaps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IEntityBloodEntry;
import org.jetbrains.annotations.Nullable;

public record EntityBloodEntry(int blood) implements IEntityBloodEntry {
    static final Codec<EntityBloodEntry> NETWORK_CODEC = Codec.intRange(0, Integer.MAX_VALUE).xmap(EntityBloodEntry::new, IEntityBloodEntry::blood);
    static final Codec<EntityBloodEntry> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("blood").forGetter(IEntityBloodEntry::blood)
            ).apply(inst, EntityBloodEntry::new)
    );

    public static final Codec<IEntityBloodEntry> GLOBAL_NETWORK_CODEC = Codec.either(
    ConvertibleEntityBloodEntry.NETWORK_CODEC,
            Codec.either(NETWORK_CODEC, EmptyEntityBloodEntry.CODEC)
                    .xmap(either -> either.map(l -> l, r -> r), x -> x instanceof EntityBloodEntry entry ? Either.left(entry) : Either.right((EmptyEntityBloodEntry)x)))
                    .xmap(either -> either.map(l -> l, r ->r), x -> x instanceof ConvertibleEntityBloodEntry ? Either.left((ConvertibleEntityBloodEntry) x) : Either.right(x));
    public static final Codec<IEntityBloodEntry> GLOBAL_CODEC = Codec.either(
            ConvertibleEntityBloodEntry.CODEC,
            Codec.either(CODEC, EmptyEntityBloodEntry.CODEC)
                    .xmap(either -> either.map(l -> l, r -> r), x -> x instanceof EmptyEntityBloodEntry empty ? Either.right(empty) : Either.left((EntityBloodEntry)x))
    ).xmap(either -> either.map(l -> l,r ->r), x -> x instanceof ConvertibleEntityBloodEntry ? Either.left((ConvertibleEntityBloodEntry) x) : Either.right(x));

    @Override
    public @Nullable IConverterEntry converter() {
        return null;
    }

    @Override
    public boolean canBeConverted() {
        return false;
    }
}
