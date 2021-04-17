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
    protected float indirecDamage = 2;
    private boolean initialNoClip = false;
    private float motionFactor = 0.97f;
    private boolean excludeShooter = false;
    private boolean gothrough;
    private int maxTicks = 100;

    public DarkBloodProjectileEntity(EntityType<? extends DarkBloodProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    /**
     * Copies the location from shooter.
     * Adds a small random to the motion
     */
    public DarkBloodProjectileEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.dark_blood_projectile, shooter, accelX, accelY, accelZ, worldIn);
    }

    /**
     * Does not add a small random to the motion
     */
    public DarkBloodProjectileEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntities.dark_blood_projectile, x, y, z, accelX, accelY, accelZ, worldIn);
    }

    public void setGothrough(boolean gothrough) {
        this.gothrough = gothrough;
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    /**
     * Exclude shooter from area of effect damage
     */
    public void excludeShooter() {
        this.excludeShooter = true;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.directDamage = compound.getFloat("direct_damage");
        this.indirecDamage = compound.getFloat("indirect_damage");
        this.gothrough = compound.getBoolean("gothrough");
        this.maxTicks = compound.getInt("max_ticks");
    }

    /**
     * @param direct   Direct hit damage
     * @param indirect Damage for all other entities close to the impact point
     */
    public void setDamage(float direct, float indirect) {
        directDamage = direct;
        indirecDamage = indirect;
    }

    /**
     * Ignore blocks and minions during the initial 20 ticks
     * Shooter is always ignored for 20 ticks.
     */
    public void setInitialNoClip() {
        initialNoClip = true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putFloat("direct_damage", directDamage);
        compound.putFloat("indirect_damage", indirecDamage);
        compound.putBoolean("gothrough", gothrough);
        compound.putInt("max_ticks", maxTicks);
    }

    @Override
    protected float getMotionFactor() {
        return motionFactor;
    }

    /**
     * Speed factor
     */
    public void setMotionFactor(float factor) {
        this.motionFactor = factor;
    }

    @Override
    protected IParticleData getParticle() {
        return ParticleTypes.UNDERWATER;
    }

    @Override
    public float getCollisionBorderSize() {
        return 0.5f;
    }

    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            Vector3d center = this.getPositionVec();
            ModParticles.spawnParticlesClient(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "spell_4"), 4, 0xA01010, 0f), center.x, center.y, center.z, 5, getCollisionBorderSize(), this.rand);

            if (this.ticksExisted % 3 == 0) {
                ModParticles.spawnParticleClient(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "effect_4"), 12, 0xC01010, 0.4F), center.x, center.y, center.z);
            }

        } else {
            if (this.ticksExisted > this.maxTicks) {
                this.remove();
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (initialNoClip && this.ticksExisted > 20) {
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

            explode(entity,4);

            Vector3d center = result.getHitVec();
            ModParticles.spawnParticlesServer(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "spell_1"), 7, 0xA01010, 0.2F), center.x, center.y, center.z, 40, 1, 1, 1, 0);
            ModParticles.spawnParticlesServer(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "spell_6"), 10, 0x700505), center.x, center.y, center.z, 15, 1, 1, 1, 0);


            if (!this.gothrough) {
                this.remove();
            }
        }
    }

    private void hitEntity(Entity entity) {
        entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, func_234616_v_()), directDamage);
        if (entity instanceof LivingEntity) {
            if (this.rand.nextInt(3) == 0) {
                ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100));
                ((LivingEntity) entity).applyKnockback(1f, -this.getMotion().x, -this.getMotion().z); //knockback
                ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));

            }
        }
    }

    public void explode(@Nullable Entity hitEntity, int distanceSq){
        @Nullable Entity shootingEntity = func_234616_v_();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(2), EntityPredicates.IS_ALIVE.and(EntityPredicates.NOT_SPECTATING));
        for (Entity e : list) {
            if ((excludeShooter && e == shootingEntity) || e == hitEntity) {
                continue;
            }
            if (e instanceof LivingEntity && e.getDistanceSq(this) < distanceSq) {
                LivingEntity entity = (LivingEntity) e;
                entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));
                entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, func_234616_v_()), indirecDamage);
            }
        }
    }
}
