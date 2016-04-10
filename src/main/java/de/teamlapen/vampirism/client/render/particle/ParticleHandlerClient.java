package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.IParticleHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Random;

/**
 * Creates and spawns particles client side
 */
public class ParticleHandlerClient implements IParticleHandler {
    @Override
    public void spawnParticle(World world, Type type, double posX, double posY, double posZ, Object... param) {
        EntityFX part = null;
        try {
            if (type == Type.FlyingBlood) {
                part = new FlyingBloodParticle(world, posX, posY, posZ, (double) param[0], (double) param[1], (double) param[2], (int) param[3]);
            } else if (type == Type.FlyingBloodEntity) {
                part = new FlyingBloodEntityParticle(world, posX, posY, posZ, (Entity) param[0], (Boolean) param[1]);
            }
        } catch (ClassCastException e) {
            VampirismMod.log.e("ParticleHandler", e, "Cannot create %s with extra arguments %s", type, Arrays.toString(param));
        }
        if (part != null) {
            Minecraft.getMinecraft().effectRenderer.addEffect(part);
        }


    }

    @Override
    public void spawnParticles(World world, Type type, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param) {
        for (int i = 0; i < count; i++) {
            spawnParticle(world, type, posX + maxDist * (2 * random.nextDouble() - 1), posY + (2 * random.nextDouble() - 1) * maxDist, posZ + (2 * random.nextDouble() - 1) * maxDist, param);
        }
    }
}
