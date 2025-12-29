package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class MotherParticleOptions extends FlyingBloodParticleOptions {

    public static final MapCodec<MotherParticleOptions> CODEC = codec(MotherParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, MotherParticleOptions> STREAM_CODEC = streamCodec(MotherParticleOptions::new);

    public MotherParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        super(maxAgeIn, direct, targetX, targetY, targetZ, scale);
    }

    public MotherParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(maxAgeIn, direct, targetX, targetY, targetZ, 1);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticles.MOTHER.get();
    }
}
