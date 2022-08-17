package de.teamlapen.vampirism.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.blocks.CursedBarkBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrunkCursedVineDecorator extends TreeDecorator {
    public static final TrunkCursedVineDecorator INSTANCE = new TrunkCursedVineDecorator();
    public static final Codec<TrunkCursedVineDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @NotNull
    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.trunk_cursed_vine.get();
    }

    @Override
    public void place(@NotNull Context context) {
        RandomSource randomsource = context.random();
        context.logs().forEach((trunkPos) -> {
                    List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
                    Direction.Axis axis = Direction.Axis.Y;
                    if (context.level().isStateAtPosition(trunkPos, (state) -> state.hasProperty(BlockStateProperties.AXIS))) {
                        if (context.level().isStateAtPosition(trunkPos, (state) -> state.getValue(BlockStateProperties.AXIS) == Direction.Axis.X)) {
                            directions.remove(Direction.WEST);
                            directions.remove(Direction.EAST);
                            axis = Direction.Axis.X;
                        } else if (context.level().isStateAtPosition(trunkPos, (state) -> state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y)) {
                            directions.remove(Direction.UP);
                            directions.remove(Direction.DOWN);
                        } else {
                            directions.remove(Direction.NORTH);
                            directions.remove(Direction.SOUTH);
                        }
                    }

                    for (Direction direction : directions) {
                        BlockPos blockpos = trunkPos.relative(direction);
                        if (context.level().isStateAtPosition(blockpos, BlockBehaviour.BlockStateBase::isAir)) {
                            this.placeCursedVine(context, blockpos, direction.getOpposite(), direction.getOpposite(), axis);
                        }
                        for (Direction direction1 : directions) {
                            if (direction == direction1.getOpposite()) continue;
                            if (direction == direction1) continue;
                            BlockPos blockpos1 = blockpos.relative(direction1);
                            if (context.level().isStateAtPosition(blockpos1, BlockBehaviour.BlockStateBase::isAir)) {
                                this.placeCursedVine(context, blockpos1, direction.getOpposite(), direction1.getOpposite(), axis);
                            }
                        }
                    }
                }
        );
    }

    protected void placeCursedVine(@NotNull Context context, @NotNull BlockPos pos, @NotNull Direction mainDirection, @NotNull Direction secondaryDirection, Direction.@NotNull Axis axis) {
        context.setBlock(pos, ModBlocks.CURSED_BARK.get().defaultBlockState().setValue(CursedBarkBlock.FACING, mainDirection).setValue(CursedBarkBlock.FACING2, secondaryDirection).setValue(CursedBarkBlock.AXIS, axis));
    }

}
