package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BushBlock extends DeadBushBlock {

    public BushBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos pos) {
        Block block = blockState.getBlock();
        return ModTags.Blocks.CURSEDEARTH.contains(block);
    }
}
