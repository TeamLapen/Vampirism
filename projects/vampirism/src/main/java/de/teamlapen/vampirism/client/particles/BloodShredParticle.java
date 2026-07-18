package de.teamlapen.vampirism.client.particles;

import de.teamlapen.vampirism.common.particles.BloodShredParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BloodShredParticle extends SingleQuadParticle {

    private final Vec3 destination;
    private final SpriteSet sprites;

    public BloodShredParticle(ClientLevel level, double x, double y, double z, Vec3 destination, SpriteSet sprites, int arrivalInTicks, boolean straight, int packedColor, float size) {
        super(level, x, y, z, sprites.first());
        this.destination = destination;
        this.sprites = sprites;
        this.lifetime = arrivalInTicks;
        this.hasPhysics = false;
        this.quadSize = 0.135f * size;

        double deltaX = destination.x - this.x;
        double deltaY = destination.y - this.y;
        double deltaZ = destination.z - this.z;

        RandomSource random = this.level.getRandom();

        if (straight) {
            this.xd = deltaX / arrivalInTicks;
            this.yd = deltaY / arrivalInTicks;
            this.zd = deltaZ / arrivalInTicks;
        } else {
            this.xd = (random.nextDouble() / 10 - 0.05) + deltaX / arrivalInTicks;
            this.yd = (random.nextDouble() / 10 - 0.01) + deltaY / arrivalInTicks;
            this.zd = (random.nextDouble() / 10 - 0.05) + deltaZ / arrivalInTicks;
        }

        this.setSpriteFromAge(sprites);

        float r = ((packedColor >> 16) & 0xFF) / 255f;
        float g = ((packedColor >> 8) & 0xFF) / 255f;
        float b = (packedColor & 0xFF) / 255f;
        this.setColor(r, g, b);
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        int ticksLeft = this.lifetime - this.age;

        double deltaX = this.destination.x - this.x;
        double deltaY = this.destination.y - this.y;
        double deltaZ = this.destination.z - this.z;

        if (ticksLeft < this.lifetime / 1.2) {
            this.xd = deltaX / ticksLeft;
            this.yd = deltaY / ticksLeft;
            this.zd = deltaZ / ticksLeft;
        }

        this.move(this.xd, this.yd, this.zd);
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<BloodShredParticleOptions> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(BloodShredParticleOptions options, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource) {
            return new BloodShredParticle(clientLevel, x, y, z, options.destination(), this.sprites, options.arrivalInTicks(), options.straight(), options.color(), options.size());
        }
    }
}
