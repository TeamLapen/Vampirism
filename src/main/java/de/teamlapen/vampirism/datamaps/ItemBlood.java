package de.teamlapen.vampirism.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import net.minecraft.util.ExtraCodecs;

public record ItemBlood(int blood) implements IItemBlood {
    public static final Codec<IItemBlood> NETWORK_CODEC = Codec.intRange(0, Integer.MAX_VALUE).xmap(ItemBlood::new, IItemBlood::blood);
    public static final Codec<IItemBlood> CODEC = ExtraCodecs.withAlternative(
            RecordCodecBuilder.create(inst ->
                    inst.group(
                            Codec.INT.fieldOf("blood").forGetter(IItemBlood::blood)
                    ).apply(inst, ItemBlood::new)
            ), NETWORK_CODEC);

    public ItemBlood() {
        this(0);
    }
}
