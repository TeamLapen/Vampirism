package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.entity.DarkBloodProjectileEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class AttackRangedDarkBloodGoal extends Goal {

    protected final VampireBaronEntity entity;
    private final int attackCooldown;
    private final int maxAttackDistance;
    private final float directDamage;
    private final float indirectDamage;
    private int attackTime;
    private int seeTime;

    public AttackRangedDarkBloodGoal(VampireBaronEntity entity, int cooldown, int maxDistance, float damage, float indirectDamage) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.attackCooldown = cooldown;
        this.maxAttackDistance = maxDistance;
        this.directDamage = damage;
        this.indirectDamage = indirectDamage;
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null;
    }

    @Override
    public void stop() {
        seeTime = 0;
        attackTime = 0;
    }

    @Override
    public void tick() {
        if (attackTime > 0) {
            attackTime--;
        } else {
            LivingEntity target = entity.getTarget();
            if (target != null) {
                double d0 = this.entity.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
                boolean canSee = this.entity.getSensing().hasLineOfSight(target);
                boolean couldSee = this.seeTime > 0;

                if (canSee != couldSee) {
                    this.seeTime = 0;
                }

                if (canSee) {
                    ++this.seeTime;
                    this.entity.lookAt(target, 19.0F, 10.0F);


                    this.entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition(1.0F));
                } else {
                    --this.seeTime;
                }

                if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
                    attack(target);
                    this.attackTime = attackCooldown;
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20));


                } else {
                    this.entity.getNavigation().moveTo(target, 1.0);

                }
            }
        }
    }

    /**
     * Spawns the dark blood entity heading towards the target entity
     */
    protected void attack(@NotNull LivingEntity target) {
        Vec3 vec3d = target.position().add(0, target.getBbHeight() * 0.6f, 0).subtract(entity.getEyePosition(1f)).normalize();

        DarkBloodProjectileEntity projectile = new DarkBloodProjectileEntity(entity.getCommandSenderWorld(), entity.getX() + vec3d.x * 0.3f, entity.getY() + entity.getEyeHeight() * 0.9f, entity.getZ() + vec3d.z * 0.3f, vec3d.x, vec3d.y, vec3d.z);
        projectile.setOwner(entity);
        projectile.setDamage(directDamage, indirectDamage);
        if (entity.distanceToSqr(target) > 64) {
            projectile.setMotionFactor(0.95f);
        } else {
            projectile.setMotionFactor(0.75f);
        }
        projectile.setInitialNoClip();
        projectile.excludeShooter();

        entity.getCommandSenderWorld().addFreshEntity(projectile);
    }
}
