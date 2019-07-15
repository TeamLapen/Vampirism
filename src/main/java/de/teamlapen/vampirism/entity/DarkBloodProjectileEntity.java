package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.minions.vampire.VampireMinionBaseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
    private float motionFactor = 0.9f;
    private boolean excludeShooter = false;
    public LivingEntity shootingEntity; //Temporary until mapping is fixed. Is not updated if someone changes projectile direction TODO

    public DarkBloodProjectileEntity(EntityType<? extends DarkBloodProjectileEntity> type, World worldIn) {
        super(type, worldIn);
        this.shootingEntity = field_70235_a;
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
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            Vec3d center = this.getPositionVector();
            //VampLib.proxy.getParticleHandler().spawnParticle(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z,  148, 4, 0xA01010,0.0);
            VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 2, getRadius(), this.rand, 148, 4, 0xA01010, 0.0);
            //Vec3d border=center.addVector(this.getRadius() * (this.rand.nextDouble()-0.5)*2,this.getRadius() * (this.rand.nextDouble()-0.5)*2,this.getRadius() * (this.rand.nextDouble()-0.5)*2);

            if (this.ticksExisted % 3 == 0) {
                Vec3d border = this.getPositionVector();
                border = border.add(this.getMotion().scale(-0.1));
                VampLib.proxy.getParticleHandler().spawnParticle(this.world, ModParticles.GENERIC_PARTICLE, border.x, border.y, border.z, 132, 12, 0xC01010, 0.4);
            }

        } else {
            if (this.ticksExisted > 300) {
                this.remove();
            }
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.directDamage = compound.getFloat("direct_damage");
        this.indirecDamage = compound.getFloat("indirect_damage");
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
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putFloat("direct_damage", directDamage);
        compound.putFloat("indirect_damage", indirecDamage);
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

    protected double getRadius() {
        return 0.5;
    }

    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (initialNoClip && this.ticksExisted > 20) {
                if (result.getType() == RayTraceResult.Type.BLOCK) {
                    return;
                }
                if (result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) result).getEntity() instanceof VampireMinionBaseEntity && (this.shootingEntity != null && this.shootingEntity.equals(((VampireMinionBaseEntity) ((EntityRayTraceResult) result).getEntity()).getLord()))) {
                    return;
                }
            }

            if (result.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), directDamage);
                if (entity instanceof LivingEntity) {
                    if (this.rand.nextInt(3) == 0) {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100));
                        ((LivingEntity) entity).knockBack(this, 1f, -this.getMotion().x, -this.getMotion().z);
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));

                    }
                }


            }

            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(2), EntityPredicates.IS_ALIVE.and(EntityPredicates.NOT_SPECTATING));
            for (Entity e : list) {
                if (excludeShooter && e == shootingEntity) {
                    continue;
                }
                if (e instanceof LivingEntity && e.getDistanceSq(this) < 4) {
                    LivingEntity entity = (LivingEntity) e;
                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));
                    if (result.getType() == RayTraceResult.Type.ENTITY) {
                        if (entity != ((EntityRayTraceResult) result).getEntity())
                            entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), indirecDamage);
                    } else {
                        entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), indirecDamage);
                    }


                }
            }
            Vec3d center = result.getHitVec();
            VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 40, 2, this.rand, 145, 7, 0xA01010, 0.2);
            VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 15, 2, this.rand, 150, 10, 0x700505, 0.0);


            this.remove();
        }
    }
}
