package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SpitfireBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 0xFFFF2211;
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockpos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
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
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_spitfire");
    }

    @Override
    public boolean canBeInfinite() {
        return ModConfig.balance().allowInfiniteSpecialArrows.get();
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }

    @Override
    public void modifyArrow(Level level, ItemStack stack, @Nullable LivingEntity shooter, AbstractArrow arrow) {
        arrow.setRemainingFireTicks(100);
    }
}
