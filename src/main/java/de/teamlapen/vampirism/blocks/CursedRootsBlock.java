package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CursedRootsBlock extends DeadBushBlock {

    public CursedRootsBlock(@NotNull Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return isProperSoil(state);
    }

    public static boolean isProperSoil(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(ModBlockTags.CURSED_EARTH) || state.getBlock() instanceof FarmBlock;
    }
}
