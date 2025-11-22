package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class PedestalParticleOptions extends FlyingBloodParticleOptions {

    public static final MapCodec<PedestalParticleOptions> CODEC = codec(PedestalParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, PedestalParticleOptions> STREAM_CODEC = streamCodec(PedestalParticleOptions::new);

    public PedestalParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        super(maxAgeIn, direct, targetX, targetY, targetZ, scale);
    }

    public PedestalParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(maxAgeIn, direct, targetX, targetY, targetZ, 1);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticles.PEDESTAL.get();
    }
}
