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
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Only spawns particles, similar to {@link AreaEffectCloudEntity}
 */
public class BasicEntityAreaParticleCloud extends Entity {

    private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.PARTICLE_DATA);
    private static final DataParameter<Float> SPAWN_RATE = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.FLOAT);

    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(BasicEntityAreaParticleCloud.class, DataSerializers.VARINT);
    private int duration;
    private int waitTime;
    private float radiusPerTick;

    public BasicEntityAreaParticleCloud(EntityType type, World worldIn) {
        super(type, worldIn);
        this.duration = 60;
        this.waitTime = 0;
        this.radiusPerTick = 0F;
        this.noClip = true;
        this.setRadius(3);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
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

    public IParticleData getParticle() {
        return this.getDataManager().get(PARTICLE);
    }

    public void setParticle(IParticleData p_195059_1_) {
        this.getDataManager().set(PARTICLE, p_195059_1_);
    }


    public float getRadius() {
        return this.getDataManager().get(RADIUS);
    }

    public void setRadius(float radius) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.size = new EntitySize(radius * 2.0F, getHeight(), size.fixed);
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

    public void setHeight(float height) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.size = new EntitySize(getRadius() * 2, height, size.fixed);
        this.setPosition(d0, d1, d2);

        if (!this.world.isRemote) {
            this.getDataManager().set(HEIGHT, height);
        }
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
    }

    @Override
    public void tick() {
        super.tick();
        float radius = this.getRadius();
        if (this.world.isRemote) {
            IParticleData particle = getParticle();
            float amount = (float) (Math.PI * radius * radius) * getSpawnRate();
            for (int i = 0; i < amount; i++) {
                float phi = this.rand.nextFloat() * (float) Math.PI * 2;
                float r = MathHelper.sqrt(this.rand.nextFloat()) * radius;
                float dx = MathHelper.cos(phi) * r;
                float dz = MathHelper.sin(phi) * r;
                float dy = this.rand.nextFloat() * getHeight();


                if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
                    int rgb = this.getColor();
                    int cr = rgb >> 16 & 255;
                    int cg = rgb >> 8 & 255;
                    int cb = rgb & 255;
                    this.world.addParticle(particle, this.posX + (double) dx, this.posY + dy, this.posZ + (double) dz, (float) cr / 255.0F, (float) cg / 255.0F, (float) cb / 255.0F);
                } else {
                    this.world.addParticle(particle, this.posX + (double) dx, this.posY + dy, this.posZ + (double) dz, (0.5D - this.rand.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.rand.nextDouble()) * 0.15D);
                }
            }
        } else {
            if (this.ticksExisted >= this.waitTime + this.duration) {
                this.remove();
                return;
            }

            if (this.ticksExisted < this.waitTime) return;
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
    public boolean writeUnlessPassenger(CompoundNBT compound) {
        return false;
    }

    @Override
    public boolean writeUnlessRemoved(CompoundNBT compound) {
        return false;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void registerData() {
        this.getDataManager().register(COLOR, 0);
        this.getDataManager().register(RADIUS, 0.5F);
        this.getDataManager().register(HEIGHT, 0.5F);
        this.getDataManager().register(PARTICLE, ParticleTypes.ENTITY_EFFECT);
        this.getDataManager().register(SPAWN_RATE, 1F);

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }
}
