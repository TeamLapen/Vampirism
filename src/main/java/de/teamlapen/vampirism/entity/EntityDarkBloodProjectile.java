package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicates;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.core.ModParticles;
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

public class EntityDarkBloodProjectile extends EntityFireball {

    protected float directDamage = 4;
    protected float indirecDamage = 2;
    private boolean initialNoClip = false;
    private float motionFactor = 0.75f;
    private boolean excludeShooter = false;

    public EntityDarkBloodProjectile(World worldIn) {
        super(worldIn);
    }

    public EntityDarkBloodProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
    }

    public EntityDarkBloodProjectile(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
    }

    public void setInitialNoClip() {
        initialNoClip = true;
    }

    public void excludeShooter() {
        this.excludeShooter = true;
    }

    public void setDamage(float direct, float indirect) {
        directDamage = direct;
        indirecDamage = indirect;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.typeOfHit == RayTraceResult.Type.BLOCK && initialNoClip && this.ticksExisted < 20) {
                return;
            }
            if (result.entityHit != null) {
                result.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shootingEntity), directDamage);
                if (result.entityHit instanceof EntityLivingBase) {
                    if (this.rand.nextInt(3) == 0) {
                        ((EntityLivingBase) result.entityHit).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
                        ((EntityLivingBase) result.entityHit).knockBack(this, 1f, this.motionX, this.motionZ);
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

            this.setDead();
        }
    }

    @Override
    protected float getMotionFactor() {
        return motionFactor;
    }

    public void setMotionFactor(float factor) {
        this.motionFactor = factor;
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SUSPENDED;
    }


    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    protected double getRadius() {
        return 0.5;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
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
                this.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("direct_damage", directDamage);
        compound.setFloat("indirect_damage", indirecDamage);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.directDamage = compound.getFloat("direct_damage");
        this.indirecDamage = compound.getFloat("indirect_damage");
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }
}
