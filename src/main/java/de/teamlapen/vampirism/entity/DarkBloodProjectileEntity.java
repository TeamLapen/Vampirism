package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Projectile entity.
 * <p>
 * Damages directly hit entities but also has a small area of effect damage
 */
public class DarkBloodProjectileEntity extends DamagingProjectileEntity {

    protected float directDamage = 4;
    protected float indirectDamage = 2;
    private boolean initialNoClip = false;
    private float motionFactor = 0.97f;
    private boolean excludeShooter = false;
    private boolean gothrough;
    private int maxTicks = 40;

    public DarkBloodProjectileEntity(EntityType<? extends DarkBloodProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    /**
     * Copies the location from shooter.
     * Adds a small random to the motion
     */
    public DarkBloodProjectileEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.DARK_BLOOD_PROJECTILE.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    /**
     * Does not add a small random to the motion
     */
    public DarkBloodProjectileEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntities.DARK_BLOOD_PROJECTILE.get(), x, y, z, accelX, accelY, accelZ, worldIn);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
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
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(distanceSq / 2d), EntityPredicates.ENTITY_STILL_ALIVE.and(EntityPredicates.NO_SPECTATORS));
        for (Entity e : list) {
            if ((excludeShooter && e == shootingEntity) || e == excludeEntity) {
                continue;
            }
            if (e instanceof LivingEntity && e.distanceToSqr(this) < distanceSq) {
                LivingEntity entity = (LivingEntity) e;
                entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 1));
                entity.hurt(DamageSource.indirectMagic(this, getOwner()), indirectDamage);
            }
        }
        if (!this.level.isClientSide) {
            ModParticles.spawnParticlesServer(this.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_1"), 7, 0xA01010, 0.2F), this.getX(), this.getY(), this.getZ(), 40, 1, 1, 1, 0);
            ModParticles.spawnParticlesServer(this.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_6"), 10, 0x700505), this.getX(), this.getY(), this.getZ(), 15, 1, 1, 1, 0);
        }
        this.remove();
    }

    /**
     * Exclude shooter from area of effect damage
     */
    public void excludeShooter() {
        this.excludeShooter = true;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public float getPickRadius() {
        return 0.5f;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
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
    public void readAdditionalSaveData(CompoundNBT compound) {
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
        if (this.level.isClientSide) {
            Vector3d center = this.position();
            ModParticles.spawnParticlesClient(this.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_4"), 4, 0xA01010, 0f), center.x, center.y, center.z, 5, getPickRadius(), this.random);

            if (this.tickCount % 3 == 0) {
                ModParticles.spawnParticleClient(this.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "effect_4"), 12, 0xC01010, 0.4F), center.x, center.y, center.z);
            }
        }

        if (this.tickCount > this.maxTicks) {
            if (!this.level.isClientSide()) {
                explode(4, null);
            } else {
                this.remove();
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

    @Override
    protected IParticleData getTrailParticle() {
        return ParticleTypes.UNDERWATER;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!this.level.isClientSide) {
            if (initialNoClip && this.tickCount > 20) {
                if (result.getType() == RayTraceResult.Type.BLOCK) {
                    return;
                }
            }

            Entity entity = null;
            if (result.getType() == RayTraceResult.Type.ENTITY) {
                entity = ((EntityRayTraceResult) result).getEntity();
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

    private void hitEntity(Entity entity) {
        entity.hurt(DamageSource.indirectMagic(this, getOwner()), directDamage);
        if (entity instanceof LivingEntity) {
            if (this.random.nextInt(3) == 0) {
                ((LivingEntity) entity).addEffect(new EffectInstance(Effects.BLINDNESS, 100));
                ((LivingEntity) entity).knockback(1f, -this.getDeltaMovement().x, -this.getDeltaMovement().z); //knockback
                ((LivingEntity) entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 1));

            }
        }
    }
}
