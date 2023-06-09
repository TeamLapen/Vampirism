package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Projectile entity.
 * <p>
 * Damages directly hit entities but also has a small area of effect damage
 */
public class DarkBloodProjectileEntity extends AbstractHurtingProjectile {

    protected float directDamage = 4;
    protected float indirectDamage = 2;
    private boolean initialNoClip = false;
    private float motionFactor = 0.97f;
    private boolean excludeShooter = false;
    private boolean gothrough;
    private int maxTicks = 40;

    public DarkBloodProjectileEntity(@NotNull EntityType<? extends DarkBloodProjectileEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    /**
     * Copies the location from shooter.
     * Adds a small random to the motion
     */
    public DarkBloodProjectileEntity(@NotNull Level worldIn, @NotNull LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.DARK_BLOOD_PROJECTILE.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    /**
     * Does not add a small random to the motion
     */
    public DarkBloodProjectileEntity(@NotNull Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntities.DARK_BLOOD_PROJECTILE.get(), x, y, z, accelX, accelY, accelZ, worldIn);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("direct_damage", directDamage);
        compound.putFloat("indirect_damage", indirectDamage);
        compound.putBoolean("gothrough", gothrough);
        compound.putInt("max_ticks", maxTicks);
        compound.putFloat("motion_factor", motionFactor);
    }

    /**
     * Deal area of effect damage, spawn particles and remove entity
     *
     * @param distanceSq    the squared distance
     * @param excludeEntity If given this will not receive AOE damage
     */
    public void explode(int distanceSq, @Nullable Entity excludeEntity) {
        @Nullable Entity shootingEntity = getOwner();
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(distanceSq / 2d), EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS));
        for (Entity e : list) {
            if ((excludeShooter && e == shootingEntity) || e == excludeEntity) {
                continue;
            }
            if (e instanceof LivingEntity entity && e.distanceToSqr(this) < distanceSq) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                DamageHandler.hurtVanilla(entity, damageSources -> damageSources.indirectMagic(this, getOwner()), indirectDamage);

            }
        }
        if (!this.level().isClientSide) {
            ModParticles.spawnParticlesServer(this.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "spell_1"), 7, 0xA01010, 0.2F), this.getX(), this.getY(), this.getZ(), 40, 1, 1, 1, 0);
            ModParticles.spawnParticlesServer(this.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "spell_6"), 10, 0x700505), this.getX(), this.getY(), this.getZ(), 15, 1, 1, 1, 0);
        }
        this.discard();
    }

    /**
     * Exclude shooter from area of effect damage
     */
    public void excludeShooter() {
        this.excludeShooter = true;
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public float getPickRadius() {
        return 0.5f;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    /**
     * @param direct   Direct hit damage
     * @param indirect Damage for all other entities close to the impact point
     */
    public void setDamage(float direct, float indirect) {
        directDamage = direct;
        indirectDamage = indirect;
    }

    public void setGothrough(boolean gothrough) {
        this.gothrough = gothrough;
    }

    /**
     * Ignore blocks and minions during the initial 20 ticks
     * Shooter is always ignored for 20 ticks.
     */
    public void setInitialNoClip() {
        initialNoClip = true;
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.directDamage = compound.getFloat("direct_damage");
        this.indirectDamage = compound.getFloat("indirect_damage");
        this.gothrough = compound.getBoolean("gothrough");
        this.maxTicks = compound.getInt("max_ticks");
        this.motionFactor = compound.getFloat("motion_factor");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            Vec3 center = this.position();
            ModParticles.spawnParticlesClient(this.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "spell_4"), 4, 0xA01010, 0f), center.x, center.y, center.z, 5, getPickRadius(), this.random);

            if (this.tickCount % 3 == 0) {
                ModParticles.spawnParticleClient(this.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "effect_4"), 12, 0xC01010, 0.4F), center.x, center.y, center.z);
            }
        }

        if (this.tickCount > this.maxTicks) {
            if (!this.level().isClientSide()) {
                explode(4, null);
            } else {
                this.discard();
            }
        }

    }

    @Override
    protected float getInertia() {
        return motionFactor;
    }

    /**
     * Speed factor
     */
    public void setMotionFactor(float factor) {
        this.motionFactor = factor;
    }

    @NotNull
    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.UNDERWATER;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide) {
            if (initialNoClip && this.tickCount > 20) {
                if (result.getType() == HitResult.Type.BLOCK) {
                    return;
                }
            }

            Entity entity = null;
            if (result.getType() == HitResult.Type.ENTITY) {
                entity = ((EntityHitResult) result).getEntity();
                if (entity instanceof DarkBloodProjectileEntity) {
                    return;
                }
                hitEntity(entity);
            }
            if (!this.gothrough) {
                explode(4, entity);
            }
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    private void hitEntity(@NotNull Entity entity) {
        DamageHandler.hurtVanilla(entity, damageSources -> damageSources.indirectMagic(this, getOwner()), directDamage);
        if (entity instanceof LivingEntity) {
            if (this.random.nextInt(3) == 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                ((LivingEntity) entity).knockback(1f, -this.getDeltaMovement().x, -this.getDeltaMovement().z); //knockback
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));

            }
        }
    }
}
