package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.items.IHunterCrossbow;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;

public class RangedHunterCrossbowAttackGoal<T extends PathfinderMob & RangedAttackMob & IVampirismCrossbowUser> extends RangedCrossbowAttackGoal<T> {


    public RangedHunterCrossbowAttackGoal(T mob, double speed, float radius) {
        super(mob, speed, radius);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.canUseCrossbow();
    }

    private boolean canUseCrossbow() {
        ItemStack stack = this.mob.getMainHandItem();
        if (stack.getItem() instanceof IHunterCrossbow) {
            return this.mob.canUseCrossbow(stack);
        }
        stack = this.mob.getOffhandItem();
        if (stack.getItem() instanceof IHunterCrossbow) {
            return this.mob.canUseCrossbow(stack);
        }
        return false;
    }

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
                this.seeTime++;
            } else {
                this.seeTime--;
            }

            double d0 = this.mob.distanceToSqr(livingentity);
            boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
            if (flag2) {
                this.updatePathDelay--;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                    this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
                if (!flag2) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, IHunterCrossbow.class::isInstance)); // IVampirismCrossbow
                    this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
                    this.mob.setChargingCrossbow(true);
                }
            } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
                if (!this.mob.isUsingItem()) {
                    this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
                }

                int i = this.mob.getTicksUsingItem();
                ItemStack itemstack = this.mob.getUseItem();
                if (i >= CrossbowItem.getChargeDuration(itemstack, this.mob)) {
                    this.mob.releaseUsingItem();
                    this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
                    this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                    this.mob.setChargingCrossbow(false);
                }
            } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
                this.attackDelay--;
                if (this.attackDelay == 0) {
                    this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
                }
            } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag) {
                this.mob.performRangedAttack(livingentity, 1.0F);
                ItemStack itemInHand = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, IHunterCrossbow.class::isInstance)); // set state depending on still loaded projectiles
                ChargedProjectiles projectiles = itemInHand.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
                if (!projectiles.isEmpty()) {
                    var delay = getAttackDelay(itemInHand);
                    this.attackDelay = delay + this.mob.getRandom().nextInt(delay);
                    this.crossbowState = CrossbowState.CHARGED;

                } else {
                    itemInHand.remove(DataComponents.CHARGED_PROJECTILES);
                    this.crossbowState = CrossbowState.UNCHARGED;
                }
            }
        }

    }

    private int getAttackDelay(ItemStack stack) {
        return stack.getItem() instanceof TechCrossbowItem ? 10 : 20;
    }

}
