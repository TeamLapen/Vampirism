package de.teamlapen.vampirism.core;

import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.vampirism.client.render.particle.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;

public class ModParticles {
    public static final ResourceLocation FLYING_BLOOD = new ResourceLocation(REFERENCE.MODID, "flying_blood");
    public static final ResourceLocation FLYING_BLOOD_ENTITY = new ResourceLocation(REFERENCE.MODID, "flying_blood_entity");
    public static final ResourceLocation HALLOWEEN = new ResourceLocation(REFERENCE.MODID, "halloween");
    public static final ResourceLocation HEAL = new ResourceLocation(REFERENCE.MODID, "heal");

    /**
     * Arguments: motionX [double], motionY [double], motionZ [double]
     */
    public static final ResourceLocation CLOUD = new ResourceLocation(REFERENCE.MODID, "cloud");

    /**
     * Arguments: Particle ID (Vanilla texture,int), TicksToLive(int), Color(int), [speed modifier (double)]
     */
    public static final ResourceLocation GENERIC_PARTICLE = new ResourceLocation(REFERENCE.MODID, "generic");
    
    public static void init() {
        ParticleHandler.registerParticle(GENERIC_PARTICLE, new ParticleHandler.ICustomParticleFactory() {
            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                GenericParticle particle = new GenericParticle(world, posX, posY, posZ, (int) param[0], (int) param[1], (int) param[2]);
                if (param.length > 3) {
                    particle.scaleSpeed((Double) param[3]);
                }
                return particle;
            }

            @Nonnull
            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger("0", (Integer) param[0]);
                nbt.setInteger("1", (Integer) param[1]);
                nbt.setInteger("2", (Integer) param[2]);
                if (param.length > 3) {
                    nbt.setDouble("3", (Double) param[3]);
                }
                return nbt;
            }

            @Nonnull
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                Object[] data = new Object[nbt.hasKey("3") ? 4 : 3];
                data[0] = nbt.getInteger("0");
                data[1] = nbt.getInteger("1");
                data[2] = nbt.getInteger("2");
                if (data.length > 3) {
                    data[3] = nbt.getDouble("3");
                }
                return data;
            }
        });
        ParticleHandler.registerParticle(FLYING_BLOOD, new ParticleHandler.ICustomParticleFactory() {
            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                if (param.length > 4) {
                    return new FlyingBloodParticle(world, posX, posY, posZ, (double) param[0], (double) param[1], (double) param[2], (int) param[3], (int) param[4]);
                } else {
                    return new FlyingBloodParticle(world, posX, posY, posZ, (double) param[0], (double) param[1], (double) param[2], (int) param[3]);
                }
            }

            @Nonnull
            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setDouble("0", (Double) param[0]);
                nbt.setDouble("1", (Double) param[1]);
                nbt.setDouble("2", (Double) param[2]);
                nbt.setInteger("3", (Integer) param[3]);
                if (param.length > 4) {
                    nbt.setInteger("4", (Integer) param[4]);
                }
                return nbt;
            }

            @Nonnull
            @SideOnly(Side.CLIENT)
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                Object[] data = new Object[nbt.hasKey("4") ? 5 : 4];
                data[0] = nbt.getDouble("0");
                data[1] = nbt.getDouble("1");
                data[2] = nbt.getDouble("2");
                data[3] = nbt.getInteger("3");
                if (data.length > 4) {
                    data[4] = nbt.getInteger("4");
                }
                return data;
            }
        });
        ParticleHandler.registerParticle(FLYING_BLOOD_ENTITY, new ParticleHandler.ICustomParticleFactory() {
            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                return new FlyingBloodEntityParticle(world, posX, posY, posZ, (Entity) param[0], (Boolean) param[1]);
            }

            @Nonnull
            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger("0", ((Entity) param[0]).getEntityId());
                nbt.setBoolean("1", (Boolean) param[1]);
                return nbt;
            }

            @Nonnull
            @SideOnly(Side.CLIENT)
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                int i = nbt.getInteger("0");
                World world = Minecraft.getMinecraft().world;
                if (world == null) return null;
                Entity e = world.getEntityByID(i);
                if (e == null) return null;
                Object[] data = new Object[2];
                data[0] = e;
                data[1] = nbt.getBoolean("1");
                return data;
            }
        });

        ParticleHandler.registerParticle(HALLOWEEN, new ParticleHandler.ICustomParticleFactory() {
            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                return new HalloweenParticle(world, posX, posY, posZ);
            }

            @Nonnull
            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                return new NBTTagCompound();
            }

            @Nonnull
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                return new Object[0];
            }
        });

        ParticleHandler.registerParticle(HEAL, new ParticleHandler.ICustomParticleFactory() {
            @SideOnly(Side.CLIENT)
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                return new Object[0];
            }

            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                return new NBTTagCompound();
            }

            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                return new HealingParticle(world, posX, posY, posZ);
            }
        });

        ParticleHandler.registerParticle(CLOUD, new ParticleHandler.ICustomParticleFactory() {

            @SideOnly(Side.CLIENT)
            @Nonnull
            @Override
            public Object[] readParticleInfo(NBTTagCompound nbt) {
                Object[] data = new Object[3];
                data[0] = nbt.getDouble("0");
                data[1] = nbt.getDouble("1");
                data[2] = nbt.getDouble("2");
                return data;
            }

            @Nonnull
            @Override
            public NBTTagCompound createParticleInfo(Object... param) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setDouble("0", (Double) param[0]);
                nbt.setDouble("1", (Double) param[1]);
                nbt.setDouble("2", (Double) param[2]);
                return nbt;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public Particle createParticle(World world, double posX, double posY, double posZ, Object... param) {
                return new ParticleCloud.Factory().createParticle(EnumParticleTypes.CLOUD.getParticleID(), world, posX, posY, posZ, (double) param[0], (double) param[1], (double) param[2]);
            }
        });
    }
}
