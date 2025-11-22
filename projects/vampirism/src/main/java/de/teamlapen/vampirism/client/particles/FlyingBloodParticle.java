package de.teamlapen.vampirism.client.particles;

import de.teamlapen.vampirism.common.particles.FlyingBloodParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flying Blood Particle for rituals
 *
 * @author maxanier
 */
public class FlyingBloodParticle extends SingleQuadParticle {
    private final double destX, destY, destZ;
    private final boolean direct;


    public FlyingBloodParticle(@NotNull ClientLevel level, double posX, double posY, double posZ, double destX, double destY, double destZ, int maxage, boolean direct, SpriteSet spriteSet, float scale) {
        super(level, posX, posY, posZ, spriteSet.first());
        this.lifetime = maxage;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.direct = direct;
        this.rCol = 0.95F;
        this.bCol = this.gCol = 0.05F;
        double wayX = destX - this.x;
        double wayZ = destZ - this.z;
        double wayY = destY - this.y;
        if (direct) {
            this.xd = wayX / maxage;
            this.yd = wayY / maxage;
            this.zd = wayZ / maxage;
        } else {
            this.xd = (this.level.random.nextDouble() / 10 - 0.05) + wayX / lifetime;
            this.yd = (this.level.random.nextDouble() / 10 - 0.01) + wayY / lifetime;
            this.zd = (this.level.random.nextDouble() / 10 - 0.05) + wayZ / lifetime;
        }

        this.scale(scale);
        this.hasPhysics = false;
    }

    @Override
    protected @NotNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        double wayX = destX - this.x;
        double wayY = destY - this.y;
        double wayZ = destZ - this.z;

        int tleft = this.lifetime - this.age;
        if (direct || tleft < this.lifetime / 1.2) {
            this.xd = wayX / tleft;
            this.yd = wayY / tleft;
            this.zd = wayZ / tleft;
        }
        this.move(this.xd, this.yd, this.zd);

        if (++this.age >= this.lifetime) {
            this.remove();
        }
    }

    public static class Provider<T extends FlyingBloodParticleOptions> implements ParticleProvider<T> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull T typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NotNull RandomSource random) {
            return new FlyingBloodParticle(worldIn, x, y, z, typeIn.targetX(), typeIn.targetY(), typeIn.targetZ(), typeIn.getMaxAge(), typeIn.direct(), this.spriteSet, typeIn.scale());
        }
    }
}