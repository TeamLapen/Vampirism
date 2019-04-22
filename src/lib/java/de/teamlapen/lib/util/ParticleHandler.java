package de.teamlapen.lib.util;


import net.minecraft.client.particle.Particle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Handle Particles
 * Client or Server Side
 */
public abstract class ParticleHandler {

    protected final static String TAG = "ParticleHandler";
    public static Map<ResourceLocation, ICustomParticleFactory> factories = new HashMap<>();

    /**
     * Register a particle, to allow it to be created by this handler
     *
     * @param id
     * @param factory
     */
    public static void registerParticle(ResourceLocation id, ICustomParticleFactory factory) {
        Validate.notNull(factory);
        if (factories.put(id, factory) != null) {
            throw new IllegalArgumentException("Particle with id " + id + " is already registered");
        }
    }

    public abstract void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, Object... param);

    /**
     * Client side only
     */
    public void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound nbt) {

    }

    public abstract void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param);

    /**
     */
    public void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, NBTTagCompound nbt) {

    }

    public interface ICustomParticleFactory {
        /**
         * Create a particle
         *
         * @param world
         * @param posX
         * @param posY
         * @param posZ
         * @param param
         * @return
         */
        @OnlyIn(Dist.CLIENT)
        Particle createParticle(World world, double posX, double posY, double posZ, Object... param);

        @Nonnull
        NBTTagCompound createParticleInfo(Object... param);

        /**
         * Reverse of {@link ICustomParticleFactory#createParticleInfo(Object...)}.
         * If the reversion fails return null to prevent the particle from being created.
         * If no parameter are required, return new Object[0] instead of null.
         *
         * @param nbt
         * @return
         */
        @OnlyIn(Dist.CLIENT)
        @Nonnull
        Object[] readParticleInfo(NBTTagCompound nbt);
    }


}

