package de.teamlapen.factions.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactionSurroundingParticle extends SingleQuadParticle {

    public FactionSurroundingParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
    }

    public FactionSurroundingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
    }

    @Override
    protected @NotNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<ColorParticleOption> {

        @Override
        public @Nullable Particle createParticle(ColorParticleOption options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            var particle = new FactionSurroundingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.get(random));
            particle.setColor(options.getRed(), options.getGreen(), options.getBlue());
            particle.setAlpha(options.getAlpha());
            return particle;
        }
    }
}
