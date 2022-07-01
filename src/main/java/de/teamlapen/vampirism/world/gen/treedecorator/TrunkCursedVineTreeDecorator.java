package de.teamlapen.vampirism.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.blocks.CursedBarkBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class TrunkCursedVineTreeDecorator extends TreeDecorator {
    public static final TrunkCursedVineTreeDecorator INSTANCE = new TrunkCursedVineTreeDecorator();
    public static final Codec<TrunkCursedVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Nonnull
    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.TRUNK_CURSED_VINE.get();
    }

    /**
     * TODO 1.18 recheck
     * initially copied from {@link net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator#place(ISeedReader, Random, List, List, Set, MutableBoundingBox)}
     * - for all direction instead of only east, west, north, south
     * - placeCursedVine instead of placeVine
     */
    @Override
    public void place(@Nonnull ISeedReader seedReader, @Nonnull Random random, List<BlockPos> posList, @Nonnull List<BlockPos> posList1, @Nonnull Set<BlockPos> posSet, @Nonnull MutableBoundingBox boundingBox) {
        posList.forEach((trunkPos) -> {
            BlockState trunkState = seedReader.getBlockState(trunkPos);
            List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
            Direction.Axis axis = Direction.Axis.Y;
            if (trunkState.hasProperty(BlockStateProperties.AXIS)) {
                switch (trunkState.getValue(BlockStateProperties.AXIS)){
                    case X:
                        directions.remove(Direction.WEST);
                        directions.remove(Direction.EAST);
                        axis = Direction.Axis.X;
                        break;
                    case Y:
                        directions.remove(Direction.UP);
                        directions.remove(Direction.DOWN);
                        break;
                    case Z:
                        directions.remove(Direction.NORTH);
                        directions.remove(Direction.SOUTH);
                        axis = Direction.Axis.Z;
                        break;
                }
            }
            for (Direction direction : directions) {
                BlockPos blockpos = trunkPos.relative(direction);
                if (Feature.isAir(seedReader, blockpos)) {
                    this.placeCursedVine(seedReader, blockpos, direction.getOpposite(), direction.getOpposite(), axis, posSet, boundingBox);
                }
                for (Direction direction1 : directions) {
                    if (direction == direction1.getOpposite()) continue;
                    if (direction == direction1) continue;
                    BlockPos blockpos1 = blockpos.relative(direction1);
                    if (Feature.isAir(seedReader, blockpos1)) {
                        this.placeCursedVine(seedReader, blockpos1, direction.getOpposite(), direction1.getOpposite(), axis, posSet, boundingBox);
                    }
                }
            }
        });
    }

    protected void placeCursedVine(IWorldWriter levelWriter, BlockPos pos, Direction mainDirection, Direction secondaryDirection, Direction.Axis axis,  Set<BlockPos> blockPositions, MutableBoundingBox boundingBox) {
        this.setBlock(levelWriter, pos, ModBlocks.CURSED_BARK.get().defaultBlockState().setValue(CursedBarkBlock.FACING, mainDirection).setValue(CursedBarkBlock.FACING2, secondaryDirection).setValue(CursedBarkBlock.AXIS, axis), blockPositions, boundingBox);
    }

}
