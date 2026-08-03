package de.teamlapen.vampirism.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * {@link SpellParticle}, but with random sprites and no alpha channel adjustments
 */
public class SanguinareParticle extends SingleQuadParticle {

    private static final RandomSource RANDOM = RandomSource.create();

    public SanguinareParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, SpriteSet sprites) {
        super(level, x, y, z, (double) 0.5F - RANDOM.nextDouble(), ya, (double) 0.5F - RANDOM.nextDouble(), sprites.first());
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.yd *= 0.2F;
        if (xa == (double) 0.0F && za == (double) 0.0F) {
            this.xd *= 0.1F;
            this.zd *= 0.1F;
        }

        this.quadSize *= 1.25F;
        this.lifetime = (int) ((double) 8.0F / ((double) this.random.nextFloat() * 0.8 + 0.2));
        this.hasPhysics = false;

        setSprite(sprites.get(RANDOM));
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new SanguinareParticle(level, x, y, z, xAux, yAux, zAux, this.sprite);
        }
    }
}
