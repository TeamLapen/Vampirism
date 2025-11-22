package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class SwordChargeParticleOptions extends FlyingBloodParticleOptions {

    public static final MapCodec<SwordChargeParticleOptions> CODEC = codec(SwordChargeParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordChargeParticleOptions> STREAM_CODEC = streamCodec(SwordChargeParticleOptions::new);

    public SwordChargeParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        super(maxAgeIn, direct, targetX, targetY, targetZ, scale);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticles.SWORD_CHARGE.get();
    }
}
