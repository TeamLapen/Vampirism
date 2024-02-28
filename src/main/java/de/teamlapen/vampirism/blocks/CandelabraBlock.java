package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;


public class CandelabraBlock extends AbstractCandelabraBlock {

    private static final Map<Direction, Iterable<Vec3>> PARTICLE_OFFSET = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, ImmutableList.of(new Vec3(0.5, 0.9375, 0.5D),new Vec3(0.1875, 0.8125, 0.5), new Vec3(0.8125, 0.8125, 0.5D)));
        put(Direction.WEST, ImmutableList.of(new Vec3(0.5, 0.9375, 0.5D), new Vec3(0.5, 0.8125, 0.1875), new Vec3(0.5, 0.8125, 0.8125)));
        put(Direction.SOUTH, ImmutableList.of(new Vec3(0.5, 0.9375, 0.5D), new Vec3(0.1875, 0.8125, 0.5), new Vec3(0.8125, 0.8125, 0.5D)));
        put(Direction.EAST, ImmutableList.of(new Vec3(0.5, 0.9375, 0.5D), new Vec3(0.5, 0.8125, 0.1875), new Vec3(0.5D, 0.8125, 0.8125)));
    }};

    public CandelabraBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2f).lightLevel(s -> 14).noOcclusion(), makeCandelabraShape());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader worldIn, @NotNull BlockPos pos) {
        return canSupportCenter(worldIn, pos.below(), Direction.UP);
    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        return facing == Direction.DOWN && !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState pState) {
        return PARTICLE_OFFSET.get(pState.getValue(FACING));
    }

    private static @NotNull VoxelShape makeCandelabraShape() {
        return Stream.of(
                Block.box(6, 0, 6, 10, 1, 10),
                Block.box(6.5, 1, 6.5, 9.5, 2, 9.5),
                Block.box(7, 2, 7, 9, 9, 9),
                Block.box(9, 4, 7, 14, 6, 9),
                Block.box(12, 6, 7, 14, 7, 9),
                Block.box(2, 4, 7, 7, 6, 9),
                Block.box(2, 6, 7, 4, 7, 9),
                Shapes.join(Block.box(7, 10, 7, 9, 14, 9), Block.box(6.5, 9, 6.5, 9.5, 10, 9.5), BooleanOp.OR),
                Shapes.join(Block.box(2, 8, 7, 4, 12, 9), Block.box(1.5, 7, 6.5, 4.5, 8, 9.5), BooleanOp.OR),
                Shapes.join(Block.box(12, 8, 7, 14, 12, 9), Block.box(11.5, 7, 6.5, 14.5, 8, 9.5), BooleanOp.OR)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);
    }
}
