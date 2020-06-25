package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Similar to vanilla ranged bow.
 *
 * @author maxanier
 */
public class AttackRangedCrossbowGoal<T extends VampirismEntity & AttackRangedCrossbowGoal.IAttackWithCrossbow> extends Goal {
    private final T entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    /**
     * Probably the same as entity, but is not guaranteed
     */
    private final int attackCooldown;
    private int seeTime;
    private int strafingTime;
    private boolean strafingClockwise, strafingBackwards;
    private int attackTime;

    public AttackRangedCrossbowGoal(T entity, double speedAmplifier, int delay, float maxDistance) {
        this.entity = entity;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.seeTime = 0;
        this.attackTime = -1;
        entity.stopTargeting();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && entity.isCrossbowInMainhand();
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null && entity.isCrossbowInMainhand();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        entity.startTargeting();

    }

    @Override
    public void tick() {
        LivingEntity entitylivingbase = this.entity.getAttackTarget();

        if (entitylivingbase != null) {
            double d0 = this.entity.getDistanceSq(entitylivingbase.getPosX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getPosZ());
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
                this.entity.lookAt(EntityAnchorArgument.Type.EYES, entitylivingbase.getEyePosition(1.0F));
            }

            if (--this.attackTime <= 0 && this.seeTime >= -30) {
                attackWithCrossbow(entitylivingbase);
                this.attackTime = this.attackCooldown;
            }

        }
    }

    protected void attackWithCrossbow(LivingEntity target) {
        ItemStack arrows = entity.getArrowStackForAttack(target);
        CrossbowArrowEntity entityArrow = CrossbowArrowEntity.createWithShooter(entity.getEntityWorld(), entity, 0, 0.3F, !entity.isLeftHanded(), arrows);
        double sx = target.getPosX() - entityArrow.getPosX();
        double sy = target.getBoundingBox().minY + (double) (target.getHeight() / 3.0F) - entityArrow.getPosY();
        double sz = target.getPosZ() - entityArrow.getPosZ();
        double dist = MathHelper.sqrt(sx * sx + sz * sz);
        entityArrow.shoot(sx, sy + dist * 0.2, sz, 1.6F, (float) (13 - target.getEntityWorld().getDifficulty().getId() * 4));
        this.entity.playSound(ModSounds.crossbow, 0.5F, 1);
        this.entity.getEntityWorld().addEntity(entityArrow);
    }

    public interface IAttackWithCrossbow {
        @Nonnull
        ItemStack getArrowStackForAttack(LivingEntity target);

        boolean isCrossbowInMainhand();

        void startTargeting();

        void stopTargeting();
    }

}
