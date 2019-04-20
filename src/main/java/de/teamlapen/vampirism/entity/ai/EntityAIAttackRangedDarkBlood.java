package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityDarkBloodProjectile;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;


public class EntityAIAttackRangedDarkBlood extends EntityAIBase {

    protected final EntityVampireBaron entity;
    private int attackTime;
    private int attackCooldown;
    private int seeTime;
    private int maxAttackDistance;
    private float directDamage;
    private float indirectDamage;

    public EntityAIAttackRangedDarkBlood(EntityVampireBaron entity, int cooldown, int maxDistance, float damage, float indirectDamage) {
        this.entity = entity;
        this.setMutexBits(3);
        this.attackCooldown = cooldown;
        this.maxAttackDistance = maxDistance;
        this.directDamage = damage;
        this.indirectDamage = indirectDamage;
    }

    @Override
    public void resetTask() {
        seeTime = 0;
        attackTime = 0;
    }

    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() != null;
    }

    @Override
    public void updateTask() {
        if (attackTime > 0) {
            attackTime--;
        } else {
            EntityLivingBase target = entity.getAttackTarget();
            if (target != null) {
                double d0 = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
                boolean canSee = this.entity.getEntitySenses().canSee(target);
                boolean couldSee = this.seeTime > 0;

                if (canSee != couldSee) {
                    this.seeTime = 0;
                }

                if (canSee) {
                    ++this.seeTime;
                    this.entity.faceEntity(target, 19.0F, 10.0F);


                    this.entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    --this.seeTime;
                }

                if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
                    attack(target);
                    this.attackTime = attackCooldown;
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20));


                } else {
                    this.entity.getNavigator().tryMoveToEntityLiving(target, 1.0);

                }
            }
        }
    }

    /**
     * Spawns the dark blood entity heading towards the target entity
     */
    protected void attack(EntityLivingBase target) {
        Vec3d vec3d = target.getPositionVector().add(0, target.height * 0.6f, 0).subtract(entity.getPositionEyes(1f)).normalize();

        EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(entity.getEntityWorld(), entity.posX + vec3d.x * 0.3f, entity.posY + entity.getEyeHeight() * 0.9f, entity.posZ + vec3d.z * 0.3f, vec3d.x, vec3d.y, vec3d.z);
        projectile.shootingEntity = entity;
        projectile.setDamage(directDamage, indirectDamage);
        if (entity.getDistanceSq(target) > 64) {
            projectile.setMotionFactor(0.95f);
        } else {
            projectile.setMotionFactor(0.75f);
        }
        projectile.setInitialNoClip();
        projectile.excludeShooter();

        entity.getEntityWorld().spawnEntity(projectile);
    }
}
