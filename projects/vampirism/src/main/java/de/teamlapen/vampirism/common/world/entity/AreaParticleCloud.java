package de.teamlapen.vampirism.common.world.entity;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

/**
 * Only spawns particles, similar to {@link net.minecraft.world.entity.AreaEffectCloud}
 */
public class AreaParticleCloud extends Entity {

    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(AreaParticleCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AreaParticleCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ParticleOptions> PARTICLE = SynchedEntityData.defineId(AreaParticleCloud.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Float> SPAWN_RATE = SynchedEntityData.defineId(AreaParticleCloud.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(AreaParticleCloud.class, EntityDataSerializers.INT);
    private int duration;
    private int waitTime;
    private float radiusPerTick;

    public AreaParticleCloud(@NotNull EntityType type, @NotNull Level worldIn) {
        super(type, worldIn);
        this.duration = 60;
        this.waitTime = 0;
        this.radiusPerTick = 0F;
        this.noPhysics = true;
        this.setRadius(3);
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
        this.dimensions = EntityDimensions.fixed(radius * 2.0F, getBbHeight());
        this.setPos(d0, d1, d2);

        if (!this.level().isClientSide()) {
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
    public boolean save(@NotNull ValueOutput output) {
        return false;
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
    }

    @Override
    public boolean saveAsPassenger(@NotNull ValueOutput output) {
        return false;
    }

    public void setHeight(float height) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        this.dimensions = EntityDimensions.fixed(getRadius() * 2, height);
        this.setPos(d0, d1, d2);

        if (!this.level().isClientSide()) {
            this.getEntityData().set(HEIGHT, height);
        }
    }

    @Override
    public void tick() {
        super.tick();
        float radius = this.getRadius();
        if (this.level().isClientSide()) {
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
                    this.level().addParticle(particle, this.getX() + (double) dx, this.getY() + dy, this.getZ() + (double) dz, (float) cr / 255.0F, (float) cg / 255.0F, (float) cb / 255.0F);
                } else {
                    this.level().addParticle(particle, this.getX() + (double) dx, this.getY() + dy, this.getZ() + (double) dz, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
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
    public boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, 0);
        builder.define(RADIUS, 0.5F);
        builder.define(HEIGHT, 0.5F);
        builder.define(PARTICLE, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1));
        builder.define(SPAWN_RATE, 1F);

    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {

    }
}
