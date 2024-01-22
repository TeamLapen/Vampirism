package de.teamlapen.vampirism.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;

public record GarlicDiffuserFuel(int burnDuration) implements IGarlicDiffuserFuel {

    public static final Codec<IGarlicDiffuserFuel> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("burn_duration").forGetter(IGarlicDiffuserFuel::burnDuration)
            ).apply(inst, GarlicDiffuserFuel::new)
    );
    public static final Codec<IGarlicDiffuserFuel> NETWORK_CODEC = Codec.INT.xmap(GarlicDiffuserFuel::new, IGarlicDiffuserFuel::burnDuration);
}
