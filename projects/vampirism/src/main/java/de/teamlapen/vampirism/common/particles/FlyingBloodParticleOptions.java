package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public abstract class FlyingBloodParticleOptions implements ParticleOptions {

    protected final int maxAge;
    protected final boolean direct;
    protected final double targetX;
    protected final double targetY;
    protected final double targetZ;
    protected final float scale;

    public FlyingBloodParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        this.maxAge = maxAge;
        this.direct = direct;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.scale = scale;
    }

    @FunctionalInterface
    protected interface OptionsCreator<T extends FlyingBloodParticleOptions> {

        T create(int maxAge, boolean direct, double targetX, double targetY, double targetZ, float scale);
    }

    protected static <T extends FlyingBloodParticleOptions> MapCodec<T> codec(OptionsCreator<T> creator) {
        return RecordCodecBuilder.mapCodec((inst) -> inst
                .group(
                        Codec.INT.fieldOf("maxAge").forGetter(FlyingBloodParticleOptions::maxAge),
                        Codec.BOOL.fieldOf("direct").forGetter(FlyingBloodParticleOptions::direct),
                        Codec.DOUBLE.fieldOf("targetX").forGetter(FlyingBloodParticleOptions::targetX),
                        Codec.DOUBLE.fieldOf("targetY").forGetter(FlyingBloodParticleOptions::targetY),
                        Codec.DOUBLE.fieldOf("targetZ").forGetter(FlyingBloodParticleOptions::targetZ),
                        Codec.FLOAT.fieldOf("scale").forGetter(FlyingBloodParticleOptions::scale)
                ).apply(inst, creator::create));
    }

    protected static <T extends FlyingBloodParticleOptions> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(OptionsCreator<T> creator) {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FlyingBloodParticleOptions::maxAge,
                ByteBufCodecs.BOOL, FlyingBloodParticleOptions::direct,
                ByteBufCodecs.DOUBLE, FlyingBloodParticleOptions::targetX,
                ByteBufCodecs.DOUBLE, FlyingBloodParticleOptions::targetY,
                ByteBufCodecs.DOUBLE, FlyingBloodParticleOptions::targetZ,
                ByteBufCodecs.FLOAT, FlyingBloodParticleOptions::scale,
                creator::create
        );
    }

    public FlyingBloodParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(maxAgeIn, direct, targetX, targetY, targetZ, 1f);
    }

//    public FlyingBloodParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, float scale) {
//        this(maxAgeIn, direct, targetX, targetY, targetZ, VResourceLocation.mc("critical_hit"), scale);
//    }

    public int getMaxAge() {
        return maxAge;
    }

    public int maxAge() {
        return maxAge;
    }

    public boolean direct() {
        return direct;
    }

    public double targetX() {
        return targetX;
    }

    public double targetY() {
        return targetY;
    }

    public double targetZ() {
        return targetZ;
    }

    public float scale() {
        return scale;
    }

}
