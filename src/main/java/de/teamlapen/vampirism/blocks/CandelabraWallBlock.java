package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class CandelabraWallBlock extends AbstractCandelabraBlock {

    private static final Map<Direction, Iterable<Vec3>> PARTICLE_OFFSET = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, ImmutableList.of(new Vec3(0.5, 0.9375, 0.8125), new Vec3(0.1875, 0.8125, 0.8125), new Vec3(0.8125, 0.8125, 0.8125)));
        put(Direction.WEST, ImmutableList.of(new Vec3(0.8125, 0.9375,0.5 ), new Vec3(0.8125, 0.8125,0.1875), new Vec3(0.8125, 0.8125, 0.8125)));
        put(Direction.SOUTH, ImmutableList.of(new Vec3(0.5, 0.9375, 0.1875), new Vec3(0.1875, 0.8125, 0.1875), new Vec3(0.8125, 0.8125, 0.1875)));
        put(Direction.EAST, ImmutableList.of(new Vec3(0.1875, 0.9375, 0.5D), new Vec3(0.1875, 0.8125, 0.1875), new Vec3(0.1875, 0.8125, 1-0.1875)));
    }};

    public CandelabraWallBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).lightLevel(s -> 14).noOcclusion(), makeWallCandelabraShape());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader worldIn, @NotNull BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(worldIn, blockpos, direction);
    }

    @Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader iworldreader = context.getLevel();
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

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState pState) {
        return PARTICLE_OFFSET.get(pState.getValue(FACING));
    }

    private static @NotNull VoxelShape makeWallCandelabraShape() {
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
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);
    }
}
