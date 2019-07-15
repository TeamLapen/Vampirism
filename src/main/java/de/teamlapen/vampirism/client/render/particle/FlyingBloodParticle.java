package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Flying Blood Particle for rituals
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodParticle extends SpriteTexturedParticle {
    private final String TAG = "FlyingBloodParticle";
    private final double destX, destY, destZ;
    private final ResourceLocation particlePos;


    /**
     * Interesting id ranges:
     * 65 default
     * 144-152
     * 160-168
     * 176-182
     */
    public FlyingBloodParticle(World world, double posX, double posY, double posZ, double destX, double destY, double destZ, int maxage, ResourceLocation particleId) {
        super(world, posX, posY, posZ);
        this.maxAge = maxage;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.particleRed = 0.95F;
        this.particleBlue = this.particleGreen = 0.05F;
        double wayX = destX - this.posX;
        double wayZ = destZ - this.posZ;
        double wayY = destY - this.posY;
        this.motionX = (this.world.rand.nextDouble() / 10 - 0.05) + wayX / maxAge;
        this.motionY = (this.world.rand.nextDouble() / 10 - 0.01) + wayY / maxAge;
        this.motionZ = (this.world.rand.nextDouble() / 10 - 0.05) + wayZ / maxAge;
        this.particlePos = particleId;
        this.tick();
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

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<FlyingBloodParticleData> {
        @Nullable
        @Override
        public Particle makeParticle(FlyingBloodParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FlyingBloodParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getMaxAge(), typeIn.getTexturePos());
        }
    }
}