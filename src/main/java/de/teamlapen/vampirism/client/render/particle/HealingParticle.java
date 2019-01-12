package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class HealingParticle extends Particle {
    private Entity entity;
    float particleScaleOverTime;

    public HealingParticle(World worldIn, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, Entity entity) {
        super(worldIn, entity.posX, entity.posY, entity.posZ, 0, 0.001, 0);
        this.entity = entity;
        this.particleTextureIndexX = 0;
        this.particleTextureIndexY = 0;
        this.setParticleTexture(ModParticles.modParticleAtlas);
        this.particleScale = 15.0f;
        this.particleScaleOverTime = particleScale;
        this.particleMaxAge = 16;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.motionY -= 0.04D * (double) this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

    }

}
