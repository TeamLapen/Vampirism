package de.teamlapen.vampirism.api.world.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IVampirismQuarrel<T extends AbstractArrow & IEntityQuarrel> extends ItemLike {

    /**
     * Called when the {@link IVampirismQuarrel} hits a block
     *
     * @param arrow          The itemstack of the shot arrow
     * @param blockPos       The position of the hit block
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityQuarrel arrowEntity, @Nullable Entity shootingEntity);

    default void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityQuarrel arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        onHitBlock(arrow, blockPos, arrowEntity, shootingEntity);
    }

    /**
     * Called when the {@link IVampirismQuarrel} hits an entity
     *
     * @param arrow          The itemstack of the shot arrow
     * @param entity         The hit entity
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityQuarrel arrowEntity, Entity shootingEntity);

    interface IQuarrelBehavior {

        int color();

        default void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        }

        default void onHitBlock(ItemStack arrow, @NotNull BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        }

        default Component getEffectDescription() {
            return Component.empty();
        }

        default AbstractArrow.Pickup pickupBehavior() {
            return AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter);

        default float damageMultiplier() {
            return 1f;
        }

        default void modifyArrow(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter, @NotNull AbstractArrow arrow) {
        }

        /**
         * Multiplier applied to the launch speed. Values below 1 make the quarrel slower (and, since arrow damage scales
         * with speed, softer at range). 1 = vanilla speed.
         */
        default float velocityFactor() {
            return 1f;
        }

        /**
         * Multiplier applied to the quarrel's gravity, composed on top of the crossbow's precision factor. Values above
         * 1 make it drop faster (heavier). 1 = unchanged.
         */
        default float gravityFactor() {
            return 1f;
        }

        /**
         * Multiplier applied to the shot's inaccuracy. Values below 1 tighten the grouping. 1 = unchanged.
         */
        default float inaccuracyFactor() {
            return 1f;
        }

        /**
         * Pierce levels granted innately, added on top of the weapon's Piercing enchantment. 0 = none.
         */
        default int extraPierceLevel() {
            return 0;
        }

        /**
         * Multiplier applied to the crossbow's charge time when this quarrel is the selected/loaded ammo. Above 1 takes
         * longer to charge. 1 = unchanged.
         */
        default float chargeMultiplier() {
            return 1f;
        }
    }
}
