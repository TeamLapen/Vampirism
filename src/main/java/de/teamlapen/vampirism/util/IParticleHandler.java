package de.teamlapen.vampirism.util;

import net.minecraft.world.World;

import java.util.Random;


public interface IParticleHandler {
    void spawnParticle(World world, Type type, double posX, double posY, double posZ, Object... param);

    void spawnParticles(World world, Type type, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param);

    enum Type {
        FlyingBlood
    }
}
