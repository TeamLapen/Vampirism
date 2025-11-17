package de.teamlapen.vampirism.client.particles;

import de.teamlapen.vampirism.common.particles.GenericParticleOptions;
import de.teamlapen.vampirism.misc.extension.client.IListBasedSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericParticle extends SingleQuadParticle {

    private GenericParticle(@NotNull ClientLevel world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, TextureAtlasSprite texture, int maxAge, int color, float speedModifier) {
        super(world, posX, posY, posZ, speedX, speedY, speedZ, texture);
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
    }

    @Override
    protected @NotNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<GenericParticleOptions> {

        @Nullable
        private final IListBasedSpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet instanceof IListBasedSpriteSet listBased ? listBased : null;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull GenericParticleOptions particleType, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NotNull RandomSource random) {
            if (this.spriteSet == null) {
                return null;
            }
            return this.spriteSet.vampirism$getSprites().stream().filter(sprite -> sprite.contents().name().equals(particleType.texture())).findFirst().map(sprite -> {
                return new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, particleType.maxAge(), particleType.color(), particleType.speed());
            }).orElse(null);
        }
    }
}
