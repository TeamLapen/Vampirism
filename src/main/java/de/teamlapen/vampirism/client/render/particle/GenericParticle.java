package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GenericParticle extends SpriteTexturedParticle {

    private GenericParticle(World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, ResourceLocation texture, int maxAge, int color, float speedModifier) {
        super(world, posX, posY, posZ, speedX, speedY, speedZ);
        this.maxAge = maxAge;
        this.motionX *= speedModifier;
        this.motionY *= speedModifier;
        this.motionZ *= speedModifier;
        this.particleRed = (color >> 16 & 255) / 255.0F;
        this.particleBlue = (color >> 8 & 255) / 255.0F;
        this.particleGreen = (color & 255) / 255.0F;
        if ((color >> 24 & 255) != 0) { //Only use alpha value if !=0.
            this.particleAlpha = (color >> 24 & 255) / 255.0F;
        }
        this.setSprite(Minecraft.getInstance().particles.atlas.getSprite(texture));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<GenericParticleData> {
        @Nullable
        @Override
        public Particle makeParticle(GenericParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GenericParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getTexturePos(), typeIn.getMaxAge(), typeIn.getColor(), typeIn.getSpeed());
        }
    }
}
