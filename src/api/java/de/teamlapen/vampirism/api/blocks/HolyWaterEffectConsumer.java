package de.teamlapen.vampirism.api.blocks;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for blocks that can be affected by holy water
 */
public interface HolyWaterEffectConsumer {

    /**
     * Called when a block is affected by holy water splash bottle
     */
    void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.TIER tier);
}
