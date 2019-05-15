package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.lang3.Validate;

/**
 * Flying blood particle for rituals.
 * Follows an entity
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodEntityParticle extends Particle {
    private final int MAX_AGE = 60;
    private final String TAG = "FlyingBloodParticle";

    private final Entity entity;

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

        this.setParticleTextureIndex(65);
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

}