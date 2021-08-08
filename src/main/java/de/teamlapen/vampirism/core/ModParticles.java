package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModParticles {
    public static final ParticleType<FlyingBloodParticleData> flying_blood = getNull();
    public static final ParticleType<FlyingBloodEntityParticleData> flying_blood_entity = getNull();
    public static final ParticleType<GenericParticleData> generic = getNull();

    static void registerParticles(IForgeRegistry<ParticleType<?>> registry) {
        registry.register(new ParticleType<FlyingBloodParticleData>(false, FlyingBloodParticleData.DESERIALIZER) {

            @Override
            public Codec<FlyingBloodParticleData> codec() {
                return FlyingBloodParticleData.CODEC;
            }
        }.setRegistryName(new ResourceLocation(REFERENCE.MODID, "flying_blood")));
        registry.register(new ParticleType<FlyingBloodEntityParticleData>(false, FlyingBloodEntityParticleData.DESERIALIZER) {

            @Override
            public Codec<FlyingBloodEntityParticleData> codec() {
                return FlyingBloodEntityParticleData.CODEC;
            }
        }.setRegistryName(new ResourceLocation(REFERENCE.MODID, "flying_blood_entity")));
        registry.register(new ParticleType<GenericParticleData>(false, GenericParticleData.DESERIALIZER) {

            @Override
            public Codec<GenericParticleData> codec() {
                return GenericParticleData.CODEC;
            }
        }.setRegistryName(new ResourceLocation(REFERENCE.MODID, "generic")));
    }

    public static void spawnParticlesClient(Level worldIn, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int count, double maxDist, Random rand) {
        assert !(worldIn instanceof ServerLevel) : "Calling spawnParticlesClient on ServerWorld is pointless";
        for (int i = 0; i < count; i++) {
            worldIn.addParticle(particle, x + maxDist * (2 * rand.nextDouble() - 1), y + (2 * rand.nextDouble() - 1) * maxDist, z + (2 * rand.nextDouble() - 1) * maxDist, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void spawnParticlesClient(Level worldIn, ParticleOptions particle, double x, double y, double z, int count, double maxDist, Random rand) {
        spawnParticlesClient(worldIn, particle, x, y, z, 0, 0, 0, count, maxDist, rand);
    }

    public static void spawnParticleClient(Level worldIn, ParticleOptions particle, double x, double y, double z) {
        spawnParticleClient(worldIn, particle, x, y, z, 0, 0, 0);
    }

    public static void spawnParticleClient(Level worldIn, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        assert !(worldIn instanceof ServerLevel) : "Calling spawnParticleClient on ServerWorld is pointless";
        worldIn.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * Sends particle packages to client if the given world is a server world
     *
     * @param worldIn       World, should be instanceof ServerWorld
     * @param particleCount How many to spawn
     * @param xOffset       Used for random offset
     * @param yOffset       Used for random offset
     * @param zOffset       Used for random offset
     * @param speed         Direction is randomized but multiplied by x/y/zOffset
     * @return Number of players this has been sent to.
     */
    public static int spawnParticlesServer(Level worldIn, ParticleOptions particle, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        assert worldIn instanceof ServerLevel : "Calling spawnParticlesServer on client side is pointless";
        if (worldIn instanceof ServerLevel) {
            return ((ServerLevel) worldIn).sendParticles(particle, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        }
        return 0;
    }
}