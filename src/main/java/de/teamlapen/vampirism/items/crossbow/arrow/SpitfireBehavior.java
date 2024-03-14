package de.teamlapen.vampirism.items.crossbow.arrow;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.mixin.accessor.EntityAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpitfireBehavior implements IVampirismCrossbowArrow.ICrossbowArrowBehavior {
    @Override
    public int color() {
        return 0xFF2211;
    }

    @Override
    public void onHitBlock(ItemStack arrow, @NotNull BlockPos blockpos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        Level level = arrowEntity.level();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
            BlockPos blockpos1 = blockpos.relative(direction);
            if (BaseFireBlock.canBePlacedAt(level, blockpos1, direction)) {
                BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
                if (blockstate1.getBlock() instanceof BaseFireBlock) {
                    blockstate1 = ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState();
                }
                level.setBlock(blockpos1, blockstate1, 11);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
        textComponents.add(Component.translatable("item.vampirism.crossbow_arrow_spitfire.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeInfinite() {
        return VampirismConfig.BALANCE.allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }

    @Override
    public void modifyArrow(@NotNull Level level, @NotNull ItemStack stack, @Nullable LivingEntity shooter, @NotNull AbstractArrow arrow) {
        arrow.setSecondsOnFire(100);
    }
}
