package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

/**
 * Similar to vanilla ranged bow.
 *
 * @author maxanier
 */
public class EntityAIAttackRangedCrossbow extends Goal {
    private final EntityVampirism entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    /**
     * Probably the same as entity, but is not guaranteed
     */
    private final IAttackWithCrossbow attacker;
    private int attackCooldown;
    private int seeTime;
    private int strafingTime;
    private boolean strafingClockwise, strafingBackwards;
    private int attackTime;

    public EntityAIAttackRangedCrossbow(EntityVampirism entity, IAttackWithCrossbow attacker, double speedAmplifier, int delay, float maxDistance) {
        this.entity = entity;
        this.attacker = attacker;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.seeTime = 0;
        this.attackTime = -1;
        attacker.stopTargeting();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && attacker.isCrossbowInMainhand();
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null && attacker.isCrossbowInMainhand();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        attacker.startTargeting();

    }

    public void updateTask() {
        LivingEntity entitylivingbase = this.entity.getAttackTarget();

        if (entitylivingbase != null) {
            double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getBoundingBox().minY, entitylivingbase.posZ);
            boolean canSee = this.entity.getEntitySenses().canSee(entitylivingbase);
            boolean couldSee = this.seeTime > 0;

            if (canSee != couldSee) {
                this.seeTime = 0;
            }

            if (canSee) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
                this.entity.getNavigator().clearPath();
                ++this.strafingTime;
            } else {
                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double) this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double) (this.maxAttackDistance * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.maxAttackDistance * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
            } else {
                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
            }

            if (--this.attackTime <= 0 && this.seeTime >= -30) {
                attackWithCrossbow(entitylivingbase);
                this.attackTime = this.attackCooldown;
            }

        }
    }

    protected void attackWithCrossbow(LivingEntity target) {
        ItemStack arrows = attacker.getArrowStackForAttack(target);
        EntityCrossbowArrow entityArrow = EntityCrossbowArrow.createWithShooter(entity.getEntityWorld(), entity, 0, 0.3F, !entity.isLeftHanded(), arrows);
        double sx = target.posX - entityArrow.posX;
        double sy = target.getBoundingBox().minY + (double) (target.height / 3.0F) - entityArrow.posY;
        double sz = target.posZ - entityArrow.posZ;
        double dist = MathHelper.sqrt(sx * sx + sz * sz);
        entityArrow.shoot(sx, sy + dist * 0.2, sz, 1.6F, (float) (13 - target.getEntityWorld().getDifficulty().getId() * 4));
        this.entity.playSound(ModSounds.crossbow, 0.5F, 1);
        this.entity.getEntityWorld().spawnEntity(entityArrow);
    }

    public interface IAttackWithCrossbow {
        @Nonnull
        ItemStack getArrowStackForAttack(LivingEntity target);

        boolean isCrossbowInMainhand();

        void startTargeting();

        void stopTargeting();
    }

}
