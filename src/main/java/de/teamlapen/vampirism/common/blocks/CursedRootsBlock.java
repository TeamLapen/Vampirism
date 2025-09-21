package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.common.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CursedRootsBlock extends DeadBushBlock {

    public CursedRootsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isProperSoil(state);
    }

    public static boolean isProperSoil(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(ModBlockTags.CURSED_EARTH) || state.getBlock() instanceof FarmBlock;
    }
}
