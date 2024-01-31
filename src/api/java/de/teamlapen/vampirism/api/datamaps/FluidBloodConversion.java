package de.teamlapen.vampirism.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record FluidBloodConversion(float conversionRate) {
    public static final Codec<FluidBloodConversion> NETWORK_CODEC = Codec.FLOAT.xmap(FluidBloodConversion::new, FluidBloodConversion::conversionRate);
    public static final Codec<FluidBloodConversion> CODEC = ExtraCodecs.withAlternative(
            RecordCodecBuilder.create(inst ->
                    inst.group(
                            Codec.FLOAT.fieldOf("conversionRate").forGetter(FluidBloodConversion::conversionRate)
                    ).apply(inst, FluidBloodConversion::new)
            ), NETWORK_CODEC);
}
