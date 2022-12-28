package de.teamlapen.vampirism.client.particle;

import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
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

/**
 * Flying Blood Particle for rituals
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public class FlyingBloodParticle extends TextureSheetParticle {
    private final String TAG = "FlyingBloodParticle";
    private final double destX, destY, destZ;
    private final boolean direct;


    public FlyingBloodParticle(@NotNull ClientLevel world, double posX, double posY, double posZ, double destX, double destY, double destZ, int maxage, boolean direct, @NotNull ResourceLocation particleId) {
        super(world, posX, posY, posZ);
        this.lifetime = maxage;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.direct = direct;
        this.rCol = 0.95F;
        this.bCol = this.gCol = 0.05F;
        double wayX = destX - this.x;
        double wayZ = destZ - this.z;
        double wayY = destY - this.y;
        if (direct) {
            this.xd = wayX / maxage;
            this.yd = wayY / maxage;
            this.zd = wayZ / maxage;
        } else {
            this.xd = (this.level.random.nextDouble() / 10 - 0.05) + wayX / lifetime;
            this.yd = (this.level.random.nextDouble() / 10 - 0.01) + wayY / lifetime;
            this.zd = (this.level.random.nextDouble() / 10 - 0.05) + wayZ / lifetime;
        }

        this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(particleId));
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

        double wayX = destX - this.x;
        double wayY = destY - this.y;
        double wayZ = destZ - this.z;

        int tleft = this.lifetime - this.age;
        if (direct || tleft < this.lifetime / 1.2) {
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
    public static class Factory implements ParticleProvider<FlyingBloodParticleData> {


        @Nullable
        @Override
        public Particle createParticle(@NotNull FlyingBloodParticleData typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FlyingBloodParticle(worldIn, x, y, z, typeIn.getTargetX(), typeIn.getTargetY(), typeIn.getTargetZ(), typeIn.getMaxAge(), typeIn.isDirect(), typeIn.getTexturePos());
        }
    }
}