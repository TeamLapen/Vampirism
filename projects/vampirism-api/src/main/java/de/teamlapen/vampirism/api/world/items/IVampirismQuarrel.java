package de.teamlapen.vampirism.api.world.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

        /**
         * Constants describing the quarrel behavior where overrides would be obsolete.
         */
        QuarrelProperties properties();

        /**
         * Called when the quarrel hits an entity.
         */
        default void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {
        }

        /**
         * Called when the quarrel hits a block.
         */
        default void onHitBlock(ItemStack arrow, @NotNull BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        }

        /**
         * Called every tick on the server while a crossbow loaded with this quarrel as its selected ammunition is being
         * charged, until it is fully charged.
         */
        default void onChargeTick(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull ItemStack crossbow, float chargeProgress) {
        }

        /**
         * Called during a quarrel entity creation. Can be used to apply special properties or stats on it if needed.
         */
        default void modifyArrow(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter, @NotNull AbstractArrow arrow) {
        }
    }
}
