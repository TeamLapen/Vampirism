package de.teamlapen.vampirism.common.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IFogDiffuserFuel;

public record FogDiffuserFuel(int burnDuration) implements IFogDiffuserFuel {

    public static final Codec<IFogDiffuserFuel> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("burn_duration").forGetter(IFogDiffuserFuel::burnDuration)
            ).apply(inst, FogDiffuserFuel::new)
    );
    public static final Codec<IFogDiffuserFuel> NETWORK_CODEC = Codec.INT.xmap(FogDiffuserFuel::new, IFogDiffuserFuel::burnDuration);
}
