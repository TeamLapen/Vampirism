package de.teamlapen.vampirism.datamaps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import org.jetbrains.annotations.Nullable;

public record EntityBloodEntry(int blood) implements IEntityBlood {

    public static final IEntityBlood EMPTY = new EntityBloodEntry(0);
    public static final Codec<IEntityBlood> NETWORK_CODEC = Codec.intRange(0, Integer.MAX_VALUE).xmap(EntityBloodEntry::new, IEntityBlood::blood);
    public static final Codec<IEntityBlood> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("blood").forGetter(IEntityBlood::blood)
            ).apply(inst, EntityBloodEntry::new)
    );

}
