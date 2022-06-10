package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, REFERENCE.MODID);

    public static final RegistryObject<ParticleType<FlyingBloodParticleData>> FLYING_BLOOD = PARTICLES.register("flying_blood", () -> new ParticleType<FlyingBloodParticleData>(false, FlyingBloodParticleData.DESERIALIZER) {
        @Override
        public Codec<FlyingBloodParticleData> codec() {
            return FlyingBloodParticleData.CODEC;
        }
    });
    public static final RegistryObject<ParticleType<FlyingBloodEntityParticleData>> FLYING_BLOOD_ENTITY = PARTICLES.register("flying_blood_entity", () -> new ParticleType<FlyingBloodEntityParticleData>(false, FlyingBloodEntityParticleData.DESERIALIZER) {
                @Override
                public Codec<FlyingBloodEntityParticleData> codec() {
                    return FlyingBloodEntityParticleData.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<GenericParticleData>> GENERIC = PARTICLES.register("generic", () -> new ParticleType<GenericParticleData>(false, GenericParticleData.DESERIALIZER) {
                @Override
                public Codec<GenericParticleData> codec() {
                    return GenericParticleData.CODEC;
                }
            });

    static void registerParticles(IEventBus bus) {
        PARTICLES.register(bus);
    }

    public static void spawnParticlesClient(World worldIn, IParticleData particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int count, double maxDist, Random rand) {
        assert !(worldIn instanceof ServerWorld) : "Calling spawnParticlesClient on ServerWorld is pointless";
        for (int i = 0; i < count; i++) {
            worldIn.addParticle(particle, x + maxDist * (2 * rand.nextDouble() - 1), y + (2 * rand.nextDouble() - 1) * maxDist, z + (2 * rand.nextDouble() - 1) * maxDist, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void spawnParticlesClient(World worldIn, IParticleData particle, double x, double y, double z, int count, double maxDist, Random rand) {
        spawnParticlesClient(worldIn, particle, x, y, z, 0, 0, 0, count, maxDist, rand);
    }

    public static void spawnParticleClient(World worldIn, IParticleData particle, double x, double y, double z) {
        spawnParticleClient(worldIn, particle, x, y, z, 0, 0, 0);
    }

    public static void spawnParticleClient(World worldIn, IParticleData particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        assert !(worldIn instanceof ServerWorld) : "Calling spawnParticleClient on ServerWorld is pointless";
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
    public static int spawnParticlesServer(World worldIn, IParticleData particle, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        assert worldIn instanceof ServerWorld : "Calling spawnParticlesServer on client side is pointless";
        if (worldIn instanceof ServerWorld) {
            return ((ServerWorld) worldIn).sendParticles(particle, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        }
        return 0;
    }
}