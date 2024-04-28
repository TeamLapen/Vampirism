package de.teamlapen.vampirism.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;

public record FluidBloodConversion(float conversionRate) implements IFluidBloodConversion {
    public static final Codec<IFluidBloodConversion> NETWORK_CODEC = Codec.FLOAT.xmap(FluidBloodConversion::new, IFluidBloodConversion::conversionRate);
    public static final Codec<IFluidBloodConversion> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(inst ->
                    inst.group(
                            Codec.FLOAT.fieldOf("conversionRate").forGetter(IFluidBloodConversion::conversionRate)
                    ).apply(inst, FluidBloodConversion::new)
            ), NETWORK_CODEC);
    public static final IFluidBloodConversion NONE = new FluidBloodConversion(0);
}
