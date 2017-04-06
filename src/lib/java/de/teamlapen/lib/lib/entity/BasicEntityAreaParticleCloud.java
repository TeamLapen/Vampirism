package de.teamlapen.lib.lib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Only spawns particles, similar to {@link EntityAreaEffectCloud}
 */
public class BasicEntityAreaParticleCloud extends Entity {

    private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> PARTICLE = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SPAWN_RATE = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);

    private static final DataParameter<Integer> PARTICLE_ARGUMENT_ONE = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PARTICLE_ARGUMENT_TWO = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.VARINT);
    private int duration;
    private int waitTime;
    private float radiusPerTick;

    public BasicEntityAreaParticleCloud(World worldIn) {
        super(worldIn);
        this.duration = 60;
        this.waitTime = 0;
        this.radiusPerTick = 0F;
        this.noClip = true;
        this.isImmuneToFire = true;
        this.setRadius(3);
    }

    public int getColor() {
        return this.getDataManager().get(COLOR);
    }

    public void setColor(int colorIn) {
        this.getDataManager().set(COLOR, colorIn);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getHeight() {
        return this.getDataManager().get(HEIGHT);
    }

    public void setHeight(float height) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.setSize(getRadius() * 2, height);
        this.setPosition(d0, d1, d2);

        if (!this.world.isRemote) {
            this.getDataManager().set(HEIGHT, height);
        }
    }

    public EnumParticleTypes getParticle() {
        return EnumParticleTypes.getParticleFromId(this.getDataManager().get(PARTICLE));
    }

    public void setParticle(EnumParticleTypes particleIn) {
        this.getDataManager().set(PARTICLE, particleIn.getParticleID());
    }

    public int getParticleArgumentOne() {
        return this.getDataManager().get(PARTICLE_ARGUMENT_ONE);
    }

    public void setParticleArgumentOne(int value) {
        this.getDataManager().set(PARTICLE_ARGUMENT_ONE, value);
    }

    public int getParticleArgumentTwo() {
        return this.getDataManager().get(PARTICLE_ARGUMENT_TWO);
    }

    public float getRadius() {
        return this.getDataManager().get(RADIUS);
    }

    public void setRadius(float radius) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.setSize(radius * 2.0F, getHeight());
        this.setPosition(d0, d1, d2);

        if (!this.world.isRemote) {
            this.getDataManager().set(RADIUS, radius);
        }
    }

    public float getSpawnRate() {
        return this.getDataManager().get(SPAWN_RATE);
    }

    public void setSpawnRate(float rate) {
        this.getDataManager().set(SPAWN_RATE, rate);
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        float radius = this.getRadius();
        if (this.world.isRemote) {
            EnumParticleTypes enumParticleTypes = getParticle();
            int[] aint = new int[enumParticleTypes.getArgumentCount()];
            if (aint.length > 0) {
                aint[0] = getParticleArgumentOne();
            }
            if (aint.length > 1) {
                aint[1] = getParticleArgumentTwo();
            }
            float amount = (float) (Math.PI * radius * radius) * getSpawnRate();
            for (int i = 0; i < amount; i++) {
                float phi = this.rand.nextFloat() * (float) Math.PI * 2;
                float r = MathHelper.sqrt(this.rand.nextFloat()) * radius;
                float dx = MathHelper.cos(phi) * r;
                float dz = MathHelper.sin(phi) * r;
                float dy = this.rand.nextFloat() * getHeight();


                if (enumParticleTypes == EnumParticleTypes.SPELL_MOB) {
                    int rgb = this.getColor();
                    int cr = rgb >> 16 & 255;
                    int cg = rgb >> 8 & 255;
                    int cb = rgb & 255;
                    this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (double) dx, this.posY + dy, this.posZ + (double) dz, (double) ((float) cr / 255.0F), (double) ((float) cg / 255.0F), (double) ((float) cb / 255.0F));
                } else {
                    this.world.spawnParticle(enumParticleTypes, this.posX + (double) dx, this.posY + dy, this.posZ + (double) dz, (0.5D - this.rand.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.rand.nextDouble()) * 0.15D, aint);
                }
            }
        } else {
            if (this.ticksExisted >= this.waitTime + this.duration) {
                this.setDead();
                return;
            }

            if (this.ticksExisted < this.waitTime) return;
            if (this.radiusPerTick != 0.0F) {
                radius += this.radiusPerTick;

                if (radius < 0.3F) {
                    this.setDead();
                    return;
                }

                this.setRadius(radius);
            }
        }
    }

    public void setGetParticleArgumentTwo(int value) {
        this.getDataManager().set(PARTICLE_ARGUMENT_TWO, value);
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
    }

    @Override
    public boolean writeToNBTAtomically(NBTTagCompound compound) {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }

    @Override
    protected void entityInit() {
        this.getDataManager().register(COLOR, 0);
        this.getDataManager().register(RADIUS, 0.5F);
        this.getDataManager().register(HEIGHT, 0.5F);
        this.getDataManager().register(PARTICLE, EnumParticleTypes.SPELL_MOB.getParticleID());
        this.getDataManager().register(PARTICLE_ARGUMENT_ONE, 0);
        this.getDataManager().register(PARTICLE_ARGUMENT_TWO, 0);
        this.getDataManager().register(SPAWN_RATE, 1F);

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }
}
