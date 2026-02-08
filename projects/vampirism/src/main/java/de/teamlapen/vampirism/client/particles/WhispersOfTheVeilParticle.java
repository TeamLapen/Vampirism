package de.teamlapen.vampirism.client.particles;

import de.teamlapen.vampirism.common.particles.WhispersOfTheVeilParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class WhispersOfTheVeilParticle extends SingleQuadParticle {

    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float speed;

    public WhispersOfTheVeilParticle(ClientLevel level, double xPos, double yPos, double zPos, SpriteSet sprites, int lifetime, double targetX, double targetY, double targetZ, float speed) {
        super(level, xPos, yPos, zPos, sprites.get(level.getRandom()));
        this.lifetime = lifetime;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.speed = speed;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            double deltaX = this.targetX - this.x;
            double deltaY = this.targetY - this.y;
            double deltaZ = this.targetZ - this.z;

            double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (length > 0.01) {
                this.xd = (deltaX / length) * this.speed;
                this.yd = (deltaY / length) * this.speed;
                this.zd = (deltaZ / length) * this.speed;
            } else {
                this.xd = 0;
                this.yd = 0;
                this.zd = 0;
            }

            this.move(this.xd, this.yd, this.zd);
        }
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Factory implements ParticleProvider<WhispersOfTheVeilParticleOptions> {

        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(WhispersOfTheVeilParticleOptions options, @NonNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NonNull RandomSource random) {
            return new WhispersOfTheVeilParticle(level, x, y, z, this.sprites, options.lifetime(), options.targetX(), options.targetY(), options.targetZ(), options.speed());
        }
    }
}
