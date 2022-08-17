package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, REFERENCE.MODID);

    public static final RegistryObject<ParticleType<FlyingBloodParticleData>> FLYING_BLOOD = PARTICLE_TYPES.register("flying_blood", () -> new ParticleType<>(false, FlyingBloodParticleData.DESERIALIZER) {

        @NotNull
        @Override
        public Codec<FlyingBloodParticleData> codec() {
            return FlyingBloodParticleData.CODEC;
        }
    });
    public static final RegistryObject<ParticleType<FlyingBloodEntityParticleData>> FLYING_BLOOD_ENTITY = PARTICLE_TYPES.register("flying_blood_entity", () -> new ParticleType<>(false, FlyingBloodEntityParticleData.DESERIALIZER) {

        @NotNull
        @Override
        public Codec<FlyingBloodEntityParticleData> codec() {
            return FlyingBloodEntityParticleData.CODEC;
        }
    });
    public static final RegistryObject<ParticleType<GenericParticleData>> GENERIC = PARTICLE_TYPES.register("generic", () -> new ParticleType<>(false, GenericParticleData.DESERIALIZER) {

        @NotNull
        @Override
        public Codec<GenericParticleData> codec() {
            return GenericParticleData.CODEC;
        }
    });

    static void registerParticles(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }

    public static void spawnParticlesClient(Level worldIn, @NotNull ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int count, double maxDist, @NotNull RandomSource rand) {
        assert !(worldIn instanceof ServerLevel) : "Calling spawnParticlesClient on ServerWorld is pointless";
        for (int i = 0; i < count; i++) {
            worldIn.addParticle(particle, x + maxDist * (2 * rand.nextDouble() - 1), y + (2 * rand.nextDouble() - 1) * maxDist, z + (2 * rand.nextDouble() - 1) * maxDist, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void spawnParticlesClient(Level worldIn, @NotNull ParticleOptions particle, double x, double y, double z, int count, double maxDist, @NotNull RandomSource rand) {
        spawnParticlesClient(worldIn, particle, x, y, z, 0, 0, 0, count, maxDist, rand);
    }

    public static void spawnParticleClient(@NotNull Level worldIn, @NotNull ParticleOptions particle, double x, double y, double z) {
        spawnParticleClient(worldIn, particle, x, y, z, 0, 0, 0);
    }

    public static void spawnParticleClient(@NotNull Level worldIn, @NotNull ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
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
    public static int spawnParticlesServer(Level worldIn, @NotNull ParticleOptions particle, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        assert worldIn instanceof ServerLevel : "Calling spawnParticlesServer on client side is pointless";
        if (worldIn instanceof ServerLevel) {
            return ((ServerLevel) worldIn).sendParticles(particle, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        }
        return 0;
    }
}