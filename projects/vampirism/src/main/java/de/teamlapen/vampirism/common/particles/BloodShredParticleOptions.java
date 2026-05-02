package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record BloodShredParticleOptions(Vec3 destination, int arrivalInTicks, boolean straight, int color) implements ParticleOptions {

    public static final int DEFAULT_COLOR = 0x940424;
    public static final int PURE_BLOOD_COLOR = 0x8d0e07;

    public static final MapCodec<BloodShredParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3.CODEC.fieldOf("destination").forGetter(BloodShredParticleOptions::destination),
            Codec.INT.fieldOf("arrivalInTicks").forGetter(BloodShredParticleOptions::arrivalInTicks),
            Codec.BOOL.fieldOf("straight").forGetter(BloodShredParticleOptions::straight),
            Codec.INT.optionalFieldOf("color", DEFAULT_COLOR).forGetter(BloodShredParticleOptions::color)
    ).apply(instance, BloodShredParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BloodShredParticleOptions> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC,
            BloodShredParticleOptions::destination,
            ByteBufCodecs.INT,
            BloodShredParticleOptions::arrivalInTicks,
            ByteBufCodecs.BOOL,
            BloodShredParticleOptions::straight,
            ByteBufCodecs.INT,
            BloodShredParticleOptions::color,
            BloodShredParticleOptions::new
    );

    @Override
    public ParticleType<?> getType() {
        return ModParticles.BLOOD_SHRED.get();
    }
}
