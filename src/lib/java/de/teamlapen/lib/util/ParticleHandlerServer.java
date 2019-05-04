package de.teamlapen.lib.util;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.network.SpawnCustomParticlePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Handles Particles Server side
 * Creates nbts for the particles and sends them to the respective clients
 */
public class ParticleHandlerServer extends ParticleHandler {
    @Override
    public void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, Object... param) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            VampLib.log.w(TAG, "Particle %s is not registered", particle);
            return;
        }
        SpawnCustomParticlePacket packet = new SpawnCustomParticlePacket(particle, posX, posY, posZ, factory.createParticleInfo(param));
        VampLib.dispatcher.sendToAllAround(packet, world.getDimension(), posX, posY, posZ, 48);

    }

    @Override
    public void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            VampLib.log.w(TAG, "Particle %s is not registered", particle);
            return;
        }
        SpawnCustomParticlePacket packet = new SpawnCustomParticlePacket(particle, posX, posY, posZ, factory.createParticleInfo(param), count, maxDist);
        VampLib.dispatcher.sendToAllAround(packet, world.getDimension(), posX, posY, posZ, 48);
    }


}
