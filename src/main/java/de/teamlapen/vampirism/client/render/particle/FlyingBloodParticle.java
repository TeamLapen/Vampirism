package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Flying Blood Particle for rituals
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodParticle extends Particle {
    private final String TAG = "FlyingBloodParticle";
    private final double destX, destY, destZ;


    /**
     * Interesting id ranges:
     * 65 default
     * 144-152
     * 160-168
     * 176-182
     */
    public FlyingBloodParticle(World world, double posX, double posY, double posZ, double destX, double destY, double
            destZ, int maxage, int particleId) {
        super(world, posX, posY, posZ, 0D, 0D, 0D);
        this.maxAge = maxage;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.particleRed = 0.95F;
        this.particleBlue = this.particleGreen = 0.05F;
        this.setParticleTextureIndex(particleId);
        double wayX = destX - this.posX;
        double wayZ = destZ - this.posZ;
        double wayY = destY - this.posY;
        this.motionX = (this.world.rand.nextDouble() / 10 - 0.05) + wayX / maxAge;
        this.motionY = (this.world.rand.nextDouble() / 10 - 0.01) + wayY / maxAge;
        this.motionZ = (this.world.rand.nextDouble() / 10 - 0.05) + wayZ / maxAge;
        this.tick();
    }

    public FlyingBloodParticle(World world, double posX, double posY, double posZ, double destX, double destY, double
            destZ, int maxage) {
        this(world, posX, posY, posZ, destX, destY, destZ, maxage, 65);
    }


    @Override
    public void tick() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        double wayX = destX - this.posX;
        double wayY = destY - this.posY;
        double wayZ = destZ - this.posZ;

        int tleft = this.maxAge - this.age;
        if (tleft < this.maxAge / 1.2) {
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