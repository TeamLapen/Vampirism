package de.teamlapen.vampirism.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.blocks.CursedVineBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TrunkCursedVineTreeDecorator extends TreeDecorator {
    public static final TrunkCursedVineTreeDecorator INSTANCE = new TrunkCursedVineTreeDecorator();
    public static final Codec<TrunkCursedVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Nonnull
    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.trunk_cursed_vine;
    }

    /**
     * TODO 1.18 recheck
     * initially copied from {@link net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator#place(ISeedReader, Random, List, List, Set, MutableBoundingBox)}
     * - for all direction instead of only east, west, north, south
     * - placeCursedVine instead of placeVine
     */
    @Override
    public void place(@Nonnull ISeedReader seedReader, @Nonnull Random random, List<BlockPos> posList, @Nonnull List<BlockPos> posList1, @Nonnull Set<BlockPos> posSet, @Nonnull MutableBoundingBox boundingBox) {
        posList.forEach((p_236880_5_) -> {
            for (Direction direction : Direction.values()) {
                BlockPos blockpos = p_236880_5_.relative(direction);
                if (Feature.isAir(seedReader, blockpos)) {
                    this.placeCursedVine(seedReader, blockpos, direction.getOpposite(), posSet, boundingBox);
                }
            }
        });
    }

    protected void placeCursedVine(IWorldWriter levelWriter, BlockPos pos, Direction direction, Set<BlockPos> blockPositions, MutableBoundingBox boundingBox) {
        this.setBlock(levelWriter, pos, ModBlocks.cursed_vine.defaultBlockState().setValue(CursedVineBlock.FACING, direction), blockPositions, boundingBox);
    }

}
