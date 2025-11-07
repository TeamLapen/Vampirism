package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3d;

public class SwordChargedParticleOptions extends FlyingBloodParticleOptions {

    public static final MapCodec<SwordChargedParticleOptions> CODEC = codec(SwordChargedParticleOptions::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordChargedParticleOptions> STREAM_CODEC = streamCodec(SwordChargedParticleOptions::new);

    public SwordChargedParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        super(maxAgeIn, direct, targetX, targetY, targetZ);
    }

    public SwordChargedParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        super(maxAge, direct, targetX, targetY, targetZ, scale);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SWORD_CHARGED.get();
    }
}
