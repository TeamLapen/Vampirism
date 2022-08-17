package de.teamlapen.lib.lib.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/**
 * Only spawns particles, similar to {@link net.minecraft.world.entity.AreaEffectCloud}
 */
public class BasicAreaParticleCloud extends Entity {

    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(BasicAreaParticleCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(BasicAreaParticleCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ParticleOptions> PARTICLE = SynchedEntityData.defineId(BasicAreaParticleCloud.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Float> SPAWN_RATE = SynchedEntityData.defineId(BasicAreaParticleCloud.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(BasicAreaParticleCloud.class, EntityDataSerializers.INT);
    private int duration;
    private int waitTime;
    private float radiusPerTick;

    public BasicAreaParticleCloud(@NotNull EntityType type, @NotNull Level worldIn) {
        super(type, worldIn);
        this.duration = 60;
        this.waitTime = 0;
        this.radiusPerTick = 0F;
        this.noPhysics = true;
        this.setRadius(3);
    }

    @NotNull
    @Override
    public Packet<?> getAddEntityPacket() {
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

    public @NotNull ParticleOptions getParticle() {
        return this.getEntityData().get(PARTICLE);
    }

    public void setParticle(@NotNull ParticleOptions particleData) {
        this.getEntityData().set(PARTICLE, particleData);
    }


    public float getRadius() {
        return this.getEntityData().get(RADIUS);
    }

    public void setRadius(float radius) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        this.dimensions = new EntityDimensions(radius * 2.0F, getBbHeight(), dimensions.fixed);
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
    public boolean save(@NotNull CompoundTag compound) {
        return false;
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
    }

    @Override
    public boolean saveAsPassenger(@NotNull CompoundTag compound) {
        return false;
    }

    public void setHeight(float height) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        this.dimensions = new EntityDimensions(getRadius() * 2, height, dimensions.fixed);
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
            ParticleOptions particle = getParticle();
            float amount = (float) (Math.PI * radius * radius) * getSpawnRate();
            for (int i = 0; i < amount; i++) {
                float phi = this.random.nextFloat() * (float) Math.PI * 2;
                float r = Mth.sqrt(this.random.nextFloat()) * radius;
                float dx = Mth.cos(phi) * r;
                float dz = Mth.sin(phi) * r;
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
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            if (this.tickCount < this.waitTime) return;
            if (this.radiusPerTick != 0.0F) {
                radius += this.radiusPerTick;

                if (radius < 0.3F) {
                    this.remove(RemovalReason.DISCARDED);
                    return;
                }

                this.setRadius(radius);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {

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
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {

    }
}
