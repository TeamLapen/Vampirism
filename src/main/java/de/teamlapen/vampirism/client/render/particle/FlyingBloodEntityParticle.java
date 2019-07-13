package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

/**
 * Flying blood particle for rituals.
 * Follows an entity
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodEntityParticle extends SpriteTexturedParticle {
    private final int MAX_AGE = 60;
    private final String TAG = "FlyingBloodParticle";

    private final Entity entity;

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public FlyingBloodEntityParticle(World world, double posX, double posY, double posZ, Entity entity, boolean direct) {
        super(world, posX, posY, posZ, 0D, 0D, 0D);

        Validate.notNull(entity);
        this.entity = entity;
        this.particleRed = 0.95F;
        this.particleBlue = this.particleGreen = 0.05F;
        if (direct) {
            this.maxAge = MAX_AGE / 2;
        } else {
            this.maxAge = MAX_AGE;
        }

        if (direct) {
            this.motionX = ((this.world.rand.nextDouble() - 0.5F) / 5f);
            this.motionY = (this.world.rand.nextDouble() / 5f);
            this.motionZ = ((this.world.rand.nextDouble() - 0.5F) / 5f);
        } else {
            this.motionX = (this.world.rand.nextDouble() - 0.5);
            this.motionY = (this.world.rand.nextDouble() + 0.2);
            this.motionZ = (this.world.rand.nextDouble() - 0.5);
        }

        this.tick();
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        double wayX = entity.posX - this.posX;
        double wayY = entity.posY + entity.getEyeHeight() - this.posY;
        double wayZ = entity.posZ - this.posZ;

        int tleft = this.maxAge - this.age;
        if (tleft < this.maxAge / 2) {
            this.motionX = wayX / tleft;
            this.motionY = wayY / tleft;
            this.motionZ = wayZ / tleft;
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        if (++this.age >= this.maxAge) {
            this.setExpired();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<FlyingBloodEntityParticleData> {
        @Nullable
        @Override
        public Particle makeParticle(FlyingBloodEntityParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FlyingBloodEntityParticle(worldIn, x, y, z, typeIn.getEntity(), typeIn.getDirect());
        }
    }

}