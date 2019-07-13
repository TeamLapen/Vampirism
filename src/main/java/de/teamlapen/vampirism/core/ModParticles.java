package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModParticles {
    public static final ParticleType<FlyingBloodParticleData> flying_blood = getNull();
    public static final ParticleType<FlyingBloodEntityParticleData> flying_blood_entity = getNull();
    public static final BasicParticleType halloween = getNull();
    public static final BasicParticleType heal = getNull();

    public static void registerParticles(IForgeRegistry<ParticleType<?>> registry) {
        registry.register(new ParticleType<FlyingBloodParticleData>(false, FlyingBloodParticleData.DESERIALIZER).setRegistryName(new ResourceLocation(REFERENCE.MODID, "flying_blood")));
        registry.register(new ParticleType<FlyingBloodEntityParticleData>(false, FlyingBloodEntityParticleData.DESERIALIZER).setRegistryName(new ResourceLocation(REFERENCE.MODID, "flying_blood_entity")));
        registry.register(new BasicParticleType(false).setRegistryName(new ResourceLocation(REFERENCE.MODID, "halloween")));
        registry.register(new BasicParticleType(false).setRegistryName(new ResourceLocation(REFERENCE.MODID, "heal")));
    }

    public static void spawnParticles(World worldIn, IParticleData particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int count, double maxDist, Random rand) {
        for (int i = 0; i < count; i++) {
            worldIn.addParticle(particle, x + maxDist * (2 * rand.nextDouble() - 1), y + (2 * rand.nextDouble() - 1) * maxDist, z + (2 * rand.nextDouble() - 1) * maxDist, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void spawnParticles(World worldIn, IParticleData particle, double x, double y, double z, int count, double maxDist, Random rand) {
        spawnParticles(worldIn, particle, x, y, z, 0, 0, 0, count, maxDist, rand);
    }

    public static void spawnParticle(World worldIn, IParticleData particle, double x, double y, double z) {
        spawnParticle(worldIn, particle, x, y, z, 0, 0, 0);
    }

    public static void spawnParticle(World worldIn, IParticleData particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        worldIn.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}