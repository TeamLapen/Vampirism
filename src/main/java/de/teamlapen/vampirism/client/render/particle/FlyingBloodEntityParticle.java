package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Flying blood particle for rituals.
 * Follows an entity
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodEntityParticle extends SpriteTexturedParticle {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int MAX_AGE = 60;
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

        if (direct) {
            this.motionX = ((this.world.rand.nextDouble() - 0.5F) / 5f);
            this.motionY = (this.world.rand.nextDouble() / 5f);
            this.motionZ = ((this.world.rand.nextDouble() - 0.5F) / 5f);
        } else {
            this.motionX = (this.world.rand.nextDouble() - 0.5);
            this.motionY = (this.world.rand.nextDouble() + 0.2);
            this.motionZ = (this.world.rand.nextDouble() - 0.5);
        }
        this.setSprite(Minecraft.getInstance().particles.atlas.getSprite(new ResourceLocation("minecraft", "particle/critical_hit")));
        //this.tick();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        double wayX = entity.getPosX() - this.posX;
        double wayY = entity.getPosY() + entity.getEyeHeight() - this.posY;
        double wayZ = entity.getPosZ() - this.posZ;

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
            Entity e = worldIn.getEntityByID(typeIn.getEntityID());
            if (e == null) {
                LOGGER.warn("Could not find entity {} for flying blood particle", typeIn.getEntityID());
                return null;
            }
            return new FlyingBloodEntityParticle(worldIn, x, y, z, e, typeIn.getDirect());
        }
    }

}