package de.teamlapen.vampirism.world.gen.feature.treedecorators;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.blocks.DiagonalCursedBarkBlock;
import de.teamlapen.vampirism.blocks.DirectCursedBarkBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class TrunkCursedVineDecorator extends TreeDecorator {
    public static final TrunkCursedVineDecorator INSTANCE = new TrunkCursedVineDecorator();
    public static final Codec<TrunkCursedVineDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @NotNull
    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.TRUNK_CURSED_VINE.get();
    }

    @Override
    public void place(@NotNull Context context) {
        context.logs().forEach((trunkPos) -> {
            if (context.level().isStateAtPosition(trunkPos, state -> state.hasProperty(BlockStateProperties.AXIS))) {
                placeCursedVine(context, trunkPos);
            }
        });
    }

    protected void placeCursedVine(@NotNull Context context, @NotNull BlockPos pos) {
        if (context.level().isStateAtPosition(pos, state -> state.getValue(BlockStateProperties.AXIS) == Direction.Axis.X)) {
            placeCursedVineX(context, pos);
        } else if (context.level().isStateAtPosition(pos, state -> state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y)) {
            placeCursedVineY(context, pos);
        } else {
            placeCursedVineZ(context, pos);
        }
    }

    protected void placeCursedVineX(@NotNull Context context, @NotNull BlockPos pos) {
        place(context, pos.north(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));
        place(context, pos.south(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));
        place(context, pos.above(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));
        place(context, pos.below(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));

        place(context, pos.above().north(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.DOWN_SOUTH, true));
        place(context, pos.north().below(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.UP_SOUTH, true));
        place(context, pos.below().south(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.UP_NORTH, true));
        place(context, pos.south().above(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.DOWN_NORTH, true));
    }

    protected void placeCursedVineY(@NotNull Context context, @NotNull BlockPos pos) {
        place(context, pos.north(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL));
        place(context, pos.south(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL));
        place(context, pos.east(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.VERTICAL));
        place(context, pos.west(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.VERTICAL));

        place(context, pos.east().north(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.WEST_SOUTH, true));
        place(context, pos.north().west(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.SOUTH_EAST, true));
        place(context, pos.west().south(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.EAST_NORTH, true));
        place(context, pos.south().east(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.NORTH_WEST, true));
    }

    protected void placeCursedVineZ(@NotNull Context context, @NotNull BlockPos pos) {
        place(context, pos.above(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.VERTICAL));
        place(context, pos.below(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.VERTICAL));
        place(context, pos.east(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));
        place(context, pos.west(), ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL));

        place(context, pos.east().above(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.DOWN_WEST, true));
        place(context, pos.above().west(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.DOWN_EAST, true));
        place(context, pos.west().below(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.UP_EAST, true));
        place(context, pos.below().east(), ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState().setValue(DiagonalCursedBarkBlock.UP_WEST, true));
    }

    protected void place(@NotNull Context context, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (context.level().isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir)) {
            context.setBlock(pos, state);
        }
    }

}
