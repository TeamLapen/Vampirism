package de.teamlapen.vampirism.client.particle;

import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class GenericParticle extends TextureSheetParticle {

    private GenericParticle(@NotNull ClientLevel world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, @NotNull ResourceLocation texture, int maxAge, int color, float speedModifier) {
        super(world, posX, posY, posZ, speedX, speedY, speedZ);
        this.lifetime = maxAge;
        this.xd *= speedModifier;
        this.yd *= speedModifier;
        this.zd *= speedModifier;
        this.rCol = (color >> 16 & 255) / 255.0F;
        this.bCol = (color & 255) / 255.0F;
        this.gCol = (color >> 8 & 255) / 255.0F;
        if ((color >> 24 & 255) != 0) { //Only use alpha value if !=0.
            this.alpha = (color >> 24 & 255) / 255.0F;
        }
        this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(texture));
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<GenericParticleData> {
        @Nullable
        @Override
        public Particle createParticle(@NotNull GenericParticleData typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GenericParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getTexturePos(), typeIn.getMaxAge(), typeIn.getColor(), typeIn.getSpeed());
        }
    }
}
