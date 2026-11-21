package de.teamlapen.vampirism.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Purple enchanting table glyphs, flying from a bookshelf to a lectern while a heritage is researched.
 */
public class HeritageGlyphParticleProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public HeritageGlyphParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
        FlyTowardsPositionParticle particle = new FlyTowardsPositionParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        // same color and brightness range as the portal particles of an ender chest
        float brightness = random.nextFloat() * 0.6F + 0.4F;
        particle.setColor(0.9F * brightness, 0.3F * brightness, brightness);
        return particle;
    }
}
