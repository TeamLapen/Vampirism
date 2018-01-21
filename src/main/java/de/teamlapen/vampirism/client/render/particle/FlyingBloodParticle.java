package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Flying Blood Particle for rituals
 *
 * @author maxanier
 */
@SideOnly(Side.CLIENT)
public class FlyingBloodParticle extends Particle {
    private final String TAG = "FlyingBloodParticle";
    private final double destX, destY, destZ;

    public FlyingBloodParticle(World world, double posX, double posY, double posZ, double destX, double destY, double
            destZ, int maxage) {
        super(world, posX, posY, posZ, 0D, 0D, 0D);
        this.particleMaxAge = maxage;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.particleRed = 0.95F;
        this.particleBlue = this.particleGreen = 0.05F;
        this.setParticleTextureIndex(129);//65
        double wayX = destX - this.posX;
        double wayZ = destZ - this.posZ;
        double wayY = destY - this.posY;
        this.motionX = (this.world.rand.nextDouble() / 10 - 0.05) + wayX / particleMaxAge;
        this.motionY = (this.world.rand.nextDouble() / 10 - 0.01) + wayY / particleMaxAge;
        this.motionZ = (this.world.rand.nextDouble() / 10 - 0.05) + wayZ / particleMaxAge;
        this.onUpdate();
    }


    @Override
    public void onUpdate() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        double wayX = destX - this.posX;
        double wayY = destY - this.posY;
        double wayZ = destZ - this.posZ;

        int tleft = this.particleMaxAge - this.particleAge;
        if (tleft < this.particleMaxAge / 1.2) {
            this.motionX = wayX / tleft;
            this.motionY = wayY / tleft;
            this.motionZ = wayZ / tleft;
        }
        this.move(this.motionX, this.motionY, this.motionZ);
    }

}