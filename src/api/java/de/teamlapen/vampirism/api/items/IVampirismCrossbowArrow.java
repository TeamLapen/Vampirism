package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IVampirismCrossbowArrow<T extends AbstractArrow & IEntityCrossbowArrow> extends IFactionExclusiveItem {

    @Nullable
    @Override
    default IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    /**
     * @return If an arrow of this type can be used in an infinite crossbow
     */
    boolean isCanBeInfinite();

    /**
     * Called when the {@link IVampirismCrossbowArrow} hits a block
     *
     * @param arrow          The itemstack of the shot arrow
     * @param blockPos       The position of the hit block
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity);

    default void onHitBlock(ItemStack arrow, BlockPos blockPos, IEntityCrossbowArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        onHitBlock(arrow, blockPos, arrowEntity, shootingEntity);
    }

    /**
     * Called when the {@link IVampirismCrossbowArrow} hits an entity
     *
     * @param arrow          The itemstack of the shot arrow
     * @param entity         The hit entity
     * @param arrowEntity    The arrow entity
     * @param shootingEntity The shooting entity. Can be the arrow entity itself
     */
    void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity);

    interface ICrossbowArrowBehavior {

        int color();

        default void onHitEntity(ItemStack arrow, LivingEntity entity, AbstractArrow arrowEntity, Entity shootingEntity) {

        }

        default void onHitBlock(ItemStack arrow, @NotNull BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction up) {

        }

        void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag);

        boolean canBeInfinite();

        default AbstractArrow.Pickup pickupBehavior() {
            return AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter);

        default void modifyArrow(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter, @NotNull AbstractArrow arrow) {

        }
    }
}
