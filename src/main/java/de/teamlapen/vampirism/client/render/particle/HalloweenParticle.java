package de.teamlapen.vampirism.client.render.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.special.DraculaHalloweenEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Quick and dirty
 * Only used on halloween
 */
@OnlyIn(Dist.CLIENT)
public class HalloweenParticle extends SpriteTexturedParticle {
    private LivingEntity entity;

    public HalloweenParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleGravity = 0.0F;
        this.maxAge = 150;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.entity != null) {
            EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
            rendermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
            float f = 0.42553192F;
            float f1 = ((float) this.age + partialTicks) / (float) this.maxAge;
            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();
            GlStateManager.enableDepthTest();
            entity.rotationPitch = 60F;
            entity.prevRotationPitch = 60F;
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            float f2 = 240.0F;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
            GlStateManager.pushMatrix();
            float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float) Math.PI);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, f3);
            GlStateManager.translatef(0.0F, 1.8F, 0.0F);
            GlStateManager.rotatef(180.0F - entityIn.getYaw(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(10.0F - 150.0F * f1 - entityIn.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.4F, -1.5F);
            //GlStateManager.scale(0.42553192F, 0.42553192F, 0.42553192F);
            GlStateManager.scalef(2F, 2F, 2F);
            GlStateManager.pushMatrix();
            this.entity.rotationYaw = 0.0F;
            this.entity.rotationYawHead = 0.0F;
            this.entity.prevRotationYaw = 0.0F;
            this.entity.prevRotationYawHead = 0F;
            rendermanager.renderEntity(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();


        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity == null) {
            DraculaHalloweenEntity entityelderguardian = ModEntities.special_dracula_halloween.create(this.world);
            entityelderguardian.setParticle(true);
            this.entity = entityelderguardian;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HalloweenParticle(worldIn, x, y, z);
        }
    }
}

