package de.teamlapen.lib.util;

import de.teamlapen.lib.VampLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Handles particles client side.
 * Actually creates the particles.
 * Deserializes nbts send from server
 */
public class ParticleHandlerClient extends ParticleHandler {
    @Override
    public void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, Object... param) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            VampLib.log.w(TAG, "Particle %s is not registered", particle);
            return;
        }
        EntityFX fx = factory.createParticle(world, posX, posY, posZ, param);
        addParticleToWorld(fx);
    }

    @Override
    public void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound nbt) {
        Object[] data = getParticleParam(particle, nbt);
        if (data != null) {
            this.spawnParticle(world, particle, posX, posY, posZ, data);
        }

    }

    @Override
    public void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param) {
        for (int i = 0; i < count; i++) {
            spawnParticle(world, particle, posX + maxDist * (2 * random.nextDouble() - 1), posY + (2 * random.nextDouble() - 1) * maxDist, posZ + (2 * random.nextDouble() - 1) * maxDist, param);
        }
    }

    @Override
    public void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, NBTTagCompound nbt) {
        Object[] data = getParticleParam(particle, nbt);
        if (data != null) {
            this.spawnParticles(world, particle, posX, posY, posZ, count, maxDist, random, data);

        }
    }

    private void addParticleToWorld(EntityFX particle) {
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    private
    @Nullable
    Object[] getParticleParam(ResourceLocation particle, NBTTagCompound data) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            VampLib.log.w(TAG, "Particle %s is not registered", particle);
            return null;
        }
        return factory.readParticleInfo(data);
    }
}
