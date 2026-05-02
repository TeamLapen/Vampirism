package de.teamlapen.vampirism.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class DarkSpruceParticleProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public DarkSpruceParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource) {
        return new FallingLeavesParticle(clientLevel, x, y, z, this.sprites.get(randomSource), 0.14F, 7.5F, true, false, 2.0F, 0.021F);
    }
}
