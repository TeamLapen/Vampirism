package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GenericParticle extends Particle {
    public GenericParticle(World worldIn, double posXIn, double posYIn, double posZIn, int particleId, int maxAge, int color) {
        super(worldIn, posXIn, posYIn, posZIn, 0, 0, 0);
        this.setParticleTextureIndex(particleId);
        this.particleMaxAge = maxAge;
        this.particleRed = ((color >> 16) & 0xFF) / 256f;
        this.particleGreen = ((color >> 8) & 0xFF) / 256f;
        this.particleBlue = (color & 0xFF) / 256f;
    }

    public void scaleSpeed(double f) {
        this.motionX *= f;
        this.motionY *= f;
        this.motionZ *= f;
    }

}
