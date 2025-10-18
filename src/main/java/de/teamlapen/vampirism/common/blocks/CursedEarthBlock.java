package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.items.HolyWaterBottleItem;
import de.teamlapen.vampirism.common.items.HolyWaterSplashBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CursedEarthBlock extends Block implements HolyWaterEffectConsumer {

    public CursedEarthBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return HolyWaterBottleItem.onHolyWaterUsedOnBlock(stack, player, () -> level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState()));
    }

    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.Tier tier) {
        level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
    }
}
