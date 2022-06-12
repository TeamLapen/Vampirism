package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.stream.Stream;


public class CrossBlock extends VampirismHorizontalBlock {

    private static VoxelShape makeCross() {
        return Stream.of(
                Block.box(1, 0, 1, 15, 2, 15),
                Block.box(3, 2, 3, 13, 3, 13),
                Block.box(6, 3, 6, 10, 30, 10),
                Block.box(10, 19, 6, 16, 23, 10),
                Block.box(0, 19, 6, 6, 23, 10)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);
    }

    public CrossBlock() {
        super(Block.Properties.of(Material.WOOD).strength(2).noOcclusion(), makeCross());
        markDecorativeBlock();
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.above()).isAir();
    }

    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        return facing == Direction.UP && !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
