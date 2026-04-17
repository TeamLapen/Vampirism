package de.teamlapen.vampirism.client.particles;

import de.teamlapen.vampirism.common.particles.FlyingBloodEntityParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Flying blood particle for rituals.
 * Follows an entity
 */
public class FlyingBloodEntityParticle extends SingleQuadParticle {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_AGE = 60;

    private final @NotNull Entity entity;

    public FlyingBloodEntityParticle(@NotNull ClientLevel world, double posX, double posY, double posZ, @NotNull Entity entity, boolean direct, SpriteSet spriteSet) {
        super(world, posX, posY, posZ, 0D, 0D, 0D, spriteSet.first());

        Objects.requireNonNull(entity);
        this.entity = entity;
        this.rCol = 0.95F;
        this.bCol = this.gCol = 0.05F;
        if (direct) {
            this.lifetime = MAX_AGE / 2;
        } else {
            this.lifetime = MAX_AGE;
        }

        if (direct) {
            this.xd = ((this.level.getRandom().nextDouble() - 0.5F) / 5f);
            this.yd = (this.level.getRandom().nextDouble() / 5f);
            this.zd = ((this.level.getRandom().nextDouble() - 0.5F) / 5f);
        } else {
            this.xd = (this.level.getRandom().nextDouble() - 0.5);
            this.yd = (this.level.getRandom().nextDouble() + 0.2);
            this.zd = (this.level.getRandom().nextDouble() - 0.5);
        }
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

        double wayX = entity.getX() - this.x;
        double wayY = entity.getY() + entity.getEyeHeight() - this.y;
        double wayZ = entity.getZ() - this.z;

        int tleft = this.lifetime - this.age;
        if (tleft < this.lifetime / 2) {
            this.xd = wayX / tleft;
            this.yd = wayY / tleft;
            this.zd = wayZ / tleft;
        }

        this.move(this.xd, this.yd, this.zd);

        if (++this.age >= this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<FlyingBloodEntityParticleOptions> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }
        @Nullable
        @Override
        public Particle createParticle(@NotNull FlyingBloodEntityParticleOptions typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            Entity e = worldIn.getEntity(typeIn.entity());
            if (e == null) {
                LOGGER.warn("Could not find entity {} for flying blood particle", typeIn.entity());
                return null;
            }
            return new FlyingBloodEntityParticle(worldIn, x, y, z, e, typeIn.direct(), spriteSet);
        }
    }

}