package de.teamlapen.vampirism.client.particle;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.mixin.client.accessor.ParticleEngineAccessor;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Flying blood particle for rituals.
 * Follows an entity
 */
public class FlyingBloodEntityParticle extends TextureSheetParticle {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_AGE = 60;

    private final @NotNull Entity entity;

    public FlyingBloodEntityParticle(@NotNull ClientLevel world, double posX, double posY, double posZ, @NotNull Entity entity, boolean direct) {
        super(world, posX, posY, posZ, 0D, 0D, 0D);

        Objects.requireNonNull(entity);
        this.entity = entity;
        this.rCol = 0.95F;
        this.bCol = this.gCol = 0.05F;
        if (direct) {
            this.lifetime = MAX_AGE / 2;
        } else {
            this.lifetime = MAX_AGE;
        }

        if (direct) {
            this.xd = ((this.level.random.nextDouble() - 0.5F) / 5f);
            this.yd = (this.level.random.nextDouble() / 5f);
            this.zd = ((this.level.random.nextDouble() - 0.5F) / 5f);
        } else {
            this.xd = (this.level.random.nextDouble() - 0.5);
            this.yd = (this.level.random.nextDouble() + 0.2);
            this.zd = (this.level.random.nextDouble() - 0.5);
        }
        this.setSprite(((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getTextureAtlas().getSprite(VResourceLocation.mc("critical_hit")));
        //this.tick();
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        double wayX = entity.getX() - this.x;
        double wayY = entity.getY() + entity.getEyeHeight() - this.y;
        double wayZ = entity.getZ() - this.z;

        int tleft = this.lifetime - this.age;
        if (tleft < this.lifetime / 2) {
            this.xd = wayX / tleft;
            this.yd = wayY / tleft;
            this.zd = wayZ / tleft;
        }

        this.move(this.xd, this.yd, this.zd);

        if (++this.age >= this.lifetime) {
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<FlyingBloodEntityParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(@NotNull FlyingBloodEntityParticleOptions typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Entity e = worldIn.getEntity(typeIn.entity());
            if (e == null) {
                LOGGER.warn("Could not find entity {} for flying blood particle", typeIn.entity());
                return null;
            }
            return new FlyingBloodEntityParticle(worldIn, x, y, z, e, typeIn.direct());
        }
    }

}