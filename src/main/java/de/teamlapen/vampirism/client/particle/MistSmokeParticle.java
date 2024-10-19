package de.teamlapen.vampirism.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

public class MistSmokeParticle extends SimpleAnimatedParticle {

    MistSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, int pPackedColor, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0F);
        this.friction = 0.92F;
        this.quadSize = 0.5F;
        this.setAlpha(0.8F);
        this.setColor(0.3f, 0.3f, 0.3f);
        this.lifetime = (int)((double)(this.quadSize * 12.0F) / (Math.random() * 0.8F + 0.2F));
        this.setSpriteFromAge(pSprites);
        this.hasPhysics = false;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            if (this.age > this.lifetime / 2) {
                this.setAlpha((1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime) * 0.8f);
            }

            if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
                this.yd -= 0.0074F;
            }
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new MistSmokeParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, FastColor.ARGB32.color(128, 255, 255, 255), this.sprites);
        }
    }
}
