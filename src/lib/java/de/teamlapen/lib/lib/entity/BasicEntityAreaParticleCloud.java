package de.teamlapen.lib.lib.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Only spawns particles, similar to {@link AreaEffectCloudEntity}
 */
public class BasicEntityAreaParticleCloud extends Entity {

    private static final DataParameter<Float> RADIUS = EntityDataManager.defineId(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.defineId(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.defineId(BasicEntityAreaParticleCloud.class, DataSerializers.PARTICLE);
    private static final DataParameter<Float> SPAWN_RATE = EntityDataManager.defineId(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);

    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BasicEntityAreaParticleCloud.class, DataSerializers.INT);
    private int duration;
    private int waitTime;
    private float radiusPerTick;

    public BasicEntityAreaParticleCloud(EntityType type, World worldIn) {
        super(type, worldIn);
        this.duration = 60;
        this.waitTime = 0;
        this.radiusPerTick = 0F;
        this.noPhysics = true;
        this.setRadius(3);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getColor() {
        return this.getEntityData().get(COLOR);
    }

    public void setColor(int colorIn) {
        this.getEntityData().set(COLOR, colorIn);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public IParticleData getParticle() {
        return this.getEntityData().get(PARTICLE);
    }

    public void setParticle(IParticleData particleData) {
        this.getEntityData().set(PARTICLE, particleData);
    }


    public float getRadius() {
        return this.getEntityData().get(RADIUS);
    }

    public void setRadius(float radius) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        this.dimensions = new EntitySize(radius * 2.0F, getBbHeight(), dimensions.fixed);
        this.setPos(d0, d1, d2);

        if (!this.level.isClientSide) {
            this.getEntityData().set(RADIUS, radius);
        }
    }

    public float getSpawnRate() {
        return this.getEntityData().get(SPAWN_RATE);
    }

    public void setSpawnRate(float rate) {
        this.getEntityData().set(SPAWN_RATE, rate);
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public boolean save(CompoundNBT compound) {
        return false;
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
    }

    @Override
    public boolean saveAsPassenger(CompoundNBT compound) {
        return false;
    }

    public void setHeight(float height) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        this.dimensions = new EntitySize(getRadius() * 2, height, dimensions.fixed);
        this.setPos(d0, d1, d2);

        if (!this.level.isClientSide) {
            this.getEntityData().set(HEIGHT, height);
        }
    }

    @Override
    public void tick() {
        super.tick();
        float radius = this.getRadius();
        if (this.level.isClientSide) {
            IParticleData particle = getParticle();
            float amount = (float) (Math.PI * radius * radius) * getSpawnRate();
            for (int i = 0; i < amount; i++) {
                float phi = this.random.nextFloat() * (float) Math.PI * 2;
                float r = MathHelper.sqrt(this.random.nextFloat()) * radius;
                float dx = MathHelper.cos(phi) * r;
                float dz = MathHelper.sin(phi) * r;
                float dy = this.random.nextFloat() * getBbHeight();


                if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
                    int rgb = this.getColor();
                    int cr = rgb >> 16 & 255;
                    int cg = rgb >> 8 & 255;
                    int cb = rgb & 255;
                    this.level.addParticle(particle, this.getX() + (double) dx, this.getY() + dy, this.getZ() + (double) dz, (float) cr / 255.0F, (float) cg / 255.0F, (float) cb / 255.0F);
                } else {
                    this.level.addParticle(particle, this.getX() + (double) dx, this.getY() + dy, this.getZ() + (double) dz, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
                }
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.remove();
                return;
            }

            if (this.tickCount < this.waitTime) return;
            if (this.radiusPerTick != 0.0F) {
                radius += this.radiusPerTick;

                if (radius < 0.3F) {
                    this.remove();
                    return;
                }

                this.setRadius(radius);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(COLOR, 0);
        this.getEntityData().define(RADIUS, 0.5F);
        this.getEntityData().define(HEIGHT, 0.5F);
        this.getEntityData().define(PARTICLE, ParticleTypes.ENTITY_EFFECT);
        this.getEntityData().define(SPAWN_RATE, 1F);

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {

    }
}
