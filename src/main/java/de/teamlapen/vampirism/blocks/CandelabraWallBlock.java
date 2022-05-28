package de.teamlapen.vampirism.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CandelabraWallBlock extends VampirismHorizontalBlock {

    private static VoxelShape makeWallCandelabraShape() {
        return Stream.of(
                Block.box(6, 1, 15, 10, 5, 16),
                Block.box(6.5, 1.5, 14, 9.5, 4.5, 15),
                Block.box(7, 2, 12, 9, 9, 14),
                Block.box(9, 4, 12, 14, 6, 14),
                Block.box(12, 6, 12, 14, 7, 14),
                Block.box(2, 4, 12, 7, 6, 14),
                Block.box(2, 6, 12, 4, 7, 14),
                Block.box(6.5, 9, 11.5, 9.5, 10, 14.5),
                Block.box(7, 10, 12, 9, 14, 14),
                Block.box(2, 8, 12, 4, 12, 14),
                Block.box(1.5, 7, 11.5, 4.5, 8, 14.5),
                Block.box(12, 8, 12, 14, 12, 14),
                Block.box(11.5, 7, 11.5, 14.5, 8, 14.5)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);
    }

    public CandelabraWallBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).lightLevel(s -> 14).noOcclusion(), makeWallCandelabraShape());
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(worldIn, blockpos, direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.defaultBlockState();
        IWorldReader iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] adirection = context.getNearestLookingDirections();

        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(iworldreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
    }
}
