package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.world.World;

public abstract class VampirismTexturedParticle extends TexturedParticle {
    private final int texturePos;

    public VampirismTexturedParticle(World worldIn, double x, double y, double z, int texturePosIn) {
        super(worldIn, x, y, z);
        this.texturePos = texturePosIn;
    }

    public VampirismTexturedParticle(World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int texturePosIn) {
        super(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        this.texturePos = texturePosIn;
    }

    @Override
    protected float getMaxU() {
        return texturePos % 16 + 16;
    }

    @Override
    protected float getMaxV() {
        return texturePos / 16 + 16;
    }

    @Override
    protected float getMinU() {
        return texturePos % 16;
    }

    @Override
    protected float getMinV() {
        return texturePos / 16;
    }
}
