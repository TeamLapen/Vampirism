package de.teamlapen.vampirism.api.blocks;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface HolyWaterEffectConsumer {

    void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.TIER tier);
}
