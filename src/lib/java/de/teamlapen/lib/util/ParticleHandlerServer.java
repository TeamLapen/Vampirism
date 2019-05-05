package de.teamlapen.lib.util;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.network.SpawnCustomParticlePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Handles Particles Server side
 * Creates nbts for the particles and sends them to the respective clients
 */
public class ParticleHandlerServer extends ParticleHandler {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void spawnParticle(World world, ResourceLocation particle, double posX, double posY, double posZ, Object... param) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            LOGGER.warn("Particle {} is not registered", particle);
            return;
        }
        SpawnCustomParticlePacket packet = new SpawnCustomParticlePacket(particle, posX, posY, posZ, factory.createParticleInfo(param));
        VampLib.dispatcher.sendToAllAround(packet, world.getDimension().getType(), posX, posY, posZ, 48);

    }

    @Override
    public void spawnParticles(World world, ResourceLocation particle, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param) {
        ICustomParticleFactory factory = factories.get(particle);
        if (factory == null) {
            LOGGER.warn("Particle {} is not registered", particle);
            return;
        }
        SpawnCustomParticlePacket packet = new SpawnCustomParticlePacket(particle, posX, posY, posZ, factory.createParticleInfo(param), count, maxDist);
        VampLib.dispatcher.sendToAllAround(packet, world.getDimension().getType(), posX, posY, posZ, 48);
    }


}
