package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicates;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Projectile entity.
 * <p>
 * Damages directly hit entities but also has a small area of effect damage
 */
public class EntityDarkBloodProjectile extends EntityFireball {

    protected float directDamage = 4;
    protected float indirecDamage = 2;
    private boolean initialNoClip = false;
    private float motionFactor = 0.9f;
    private boolean excludeShooter = false;

    public EntityDarkBloodProjectile(World worldIn) {
        super(worldIn);
    }

    /**
     * Copies the location from shooter.
     * Adds a small random to the motion
     */
    public EntityDarkBloodProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
    }

    /**
     * Does not add a small random to the motion
     */
    public EntityDarkBloodProjectile(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
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
                Vec3d motion = new Vec3d(motionX, motionY, motionZ);
                border = border.add(motion.scale(-0.1));
                VampLib.proxy.getParticleHandler().spawnParticle(this.world, ModParticles.GENERIC_PARTICLE, border.x, border.y, border.z, 132, 12, 0xC01010, 0.4);
            }

        } else {
            if (this.ticksExisted > 300) {
                this.remove();
            }
        }
    }

    @Override
    public void readAdditional(NBTTagCompound compound) {
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
    public void writeAdditional(NBTTagCompound compound) {
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
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SUSPENDED;
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
                if (result.type == RayTraceResult.Type.BLOCK) {
                    return;
                }
                if (result.type == RayTraceResult.Type.ENTITY && result.entity instanceof EntityVampireMinionBase && (this.shootingEntity != null && this.shootingEntity.equals(((EntityVampireMinionBase) result.entity).getLord()))) {
                    return;
                }
            }

            if (result.entity != null) {
                result.entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), directDamage);
                if (result.entity instanceof EntityLivingBase) {
                    if (this.rand.nextInt(3) == 0) {
                        ((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
                        ((EntityLivingBase) result.entity).knockBack(this, 1f, -this.motionX, -this.motionZ);
                        ((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));

                    }
                }


            }

            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(2), Predicates.and(EntitySelectors.IS_ALIVE, EntitySelectors.NOT_SPECTATING));
            for (Entity e : list) {
                if (excludeShooter && e == shootingEntity) {
                    continue;
                }
                if (e instanceof EntityLivingBase && e.getDistanceSq(this) < 4) {
                    EntityLivingBase entity = (EntityLivingBase) e;
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));
                    if (entity != result.entityHit)
                        entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), indirecDamage);

                }
            }
            Vec3d center = result.hitVec;
            VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 40, 2, this.rand, 145, 7, 0xA01010, 0.2);
            VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, center.x, center.y, center.z, 15, 2, this.rand, 150, 10, 0x700505, 0.0);


            this.remove();
        }
    }
}
