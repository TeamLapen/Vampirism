package de.teamlapen.vampirism.datamaps;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IEntityBloodEntry;

public record ConvertibleEntityBloodEntry(int blood, IConverterEntry converter) implements IEntityBloodEntry {
    public static final Codec<ConvertibleEntityBloodEntry> NETWORK_CODEC = Codec.pair(Codec.INT, ConverterEntry.CODEC).xmap(integerOptionalPair -> new ConvertibleEntityBloodEntry(integerOptionalPair.getFirst(), integerOptionalPair.getSecond()), inst -> Pair.of(inst.blood(), inst.converter()));
    public static final Codec<ConvertibleEntityBloodEntry> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("blood").forGetter(IEntityBloodEntry::blood),
                    ConverterEntry.CODEC.fieldOf("convertible").forGetter(IEntityBloodEntry::converter)
            ).apply(inst, ConvertibleEntityBloodEntry::new)
    );

    @Override
    public boolean canBeConverted() {
        return true;
    }
}
