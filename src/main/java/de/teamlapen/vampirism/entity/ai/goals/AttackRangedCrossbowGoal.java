package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

/**
 * Similar to vanilla ranged bow.
 *
 * @author maxanier
 */
public class AttackRangedCrossbowGoal<T extends PathfinderMob & RangedAttackMob & IVampirismCrossbowUser> extends Goal {
    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T mob;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public AttackRangedCrossbowGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
        this.mob = p_i50322_1_;
        this.speedModifier = p_i50322_2_;
        this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow() && this.canUseCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.mob.isHolding(stack -> stack.getItem() instanceof ICrossbow);
    }

    private boolean canUseCrossbow() {
        ItemStack stack = this.mob.getMainHandItem();
        if (stack.getItem() instanceof ICrossbow) {
            return this.mob.canUseCrossbow(stack);
        }
        stack = this.mob.getOffhandItem();
        if (stack.getItem() instanceof ICrossbow) {
            return this.mob.canUseCrossbow(stack);
        }
        return false;
    }

    private boolean isValidTarget() {
        return this.mob.getTarget() != null && this.mob.getTarget().isAlive() && this.mob.distanceToSqr(this.mob.getTarget()) >= 3;
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setTarget((LivingEntity)null);
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            this.mob.setChargingCrossbow(false);
//            CrossbowItem.setCharged(this.mob.getUseItem(), false);
        }
    }

    @SuppressWarnings("UnreachableCode")
    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            double d0 = this.mob.distanceToSqr(livingentity);
            boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
            if (flag2) {
                --this.updatePathDelay;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
                    this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            if (this.crossbowState == CrossbowState.UNCHARGED) {
                if (!flag2) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, ICrossbow.class::isInstance));
                    this.crossbowState = CrossbowState.CHARGING;
                    this.mob.setChargingCrossbow(true);
                }
            } else if (this.crossbowState == CrossbowState.CHARGING) {
                if (!this.mob.isUsingItem()) {
                    this.crossbowState = CrossbowState.UNCHARGED;
                    return;
                }

                int i = this.mob.getTicksUsingItem();
                ItemStack itemstack = this.mob.getUseItem();
                if (i >= ((ICrossbow) itemstack.getItem()).getChargeDuration(itemstack)) {
                    this.mob.releaseUsingItem();
                    this.crossbowState = CrossbowState.CHARGED;
                    var delay = getAttackDelay(itemstack);
                    this.attackDelay = delay + this.mob.getRandom().nextInt(delay);
                    this.mob.setChargingCrossbow(false);
                }
            } else if (this.crossbowState == CrossbowState.CHARGED) {
                --this.attackDelay;
                if (this.attackDelay == 0) {
                    this.crossbowState = CrossbowState.READY_TO_ATTACK;
                }
            } else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && flag) {
                this.mob.performRangedAttack(livingentity, 1.0F);
                ItemStack itemstack1 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, ICrossbow.class::isInstance));
                if (itemstack1.getItem() instanceof ICrossbow crossbow && crossbow.getChargedProjectiles(itemstack1).isEmpty()) {
                    crossbow.setCharged(itemstack1, false);
                    this.crossbowState = CrossbowState.UNCHARGED;
                } else {
                    var delay = getAttackDelay(itemstack1);
                    this.attackDelay = delay + this.mob.getRandom().nextInt(delay);
                    this.crossbowState = CrossbowState.CHARGED;
                }
            }

        }
    }

    private int getAttackDelay(ItemStack stack) {
        return stack.getItem() instanceof TechCrossbowItem ? 10 : 20;
    }

    private boolean canRun() {
        return this.crossbowState == CrossbowState.UNCHARGED;
    }

    enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK
    }

}
