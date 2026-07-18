package de.teamlapen.vampirism.common.world.features.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

public class TallGrassNearTreeDecorator extends TreeDecorator {

    public static final MapCodec<TallGrassNearTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(d -> d.min),
            Codec.INT.fieldOf("max").forGetter(d -> d.max),
            Codec.INT.fieldOf("radius").forGetter(d -> d.radius)
    ).apply(instance, TallGrassNearTreeDecorator::new));

    private static final double RADIUS_DEVIATIONS = 3.0;
    private static final double MAX_SHORT_GRASS_CHANCE = 0.75;

    private final int min;
    private final int max;
    private final int radius;

    public TallGrassNearTreeDecorator() {
        this(4, 9, 3);
    }

    public TallGrassNearTreeDecorator(int min, int max, int radius) {
        this.min = min;
        this.max = max;
        this.radius = radius;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModFeatures.TALL_GRASS_NEAR_TREE.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> trunk = TreeFeature.getLowestTrunkOrRootOfTree(context);
        if (trunk.isEmpty()) return;

        BlockPos basePos = trunk.getFirst();
        RandomSource random = context.random();
        int count = random.nextIntBetweenInclusive(this.min, this.max);
        double spread = this.radius - 1;
        double angleOffset = random.nextDouble() * Mth.TWO_PI;
        for (int i = 0; i < count; i++) {
            double angle = angleOffset + (i + random.nextDouble()) / count * Mth.TWO_PI;
            double distance = 1 + Math.min(Math.abs(random.nextGaussian()) * (spread / RADIUS_DEVIATIONS), spread);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);

            BlockPos groundPos = context.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, basePos.offset(offsetX, 0, offsetZ));
            if (Math.abs(groundPos.getY() - basePos.getY()) <= 1) {
                double shortGrassChance = spread > 0 ? (distance - 1) / spread * MAX_SHORT_GRASS_CHANCE : 0;
                if (random.nextDouble() < shortGrassChance) {
                    placeShortGrass(context, groundPos);
                } else {
                    placeTallGrass(context, groundPos);
                }
            }
        }
    }

    protected void placeShortGrass(Context context, BlockPos pos) {
        BlockState state = Blocks.SHORT_GRASS.defaultBlockState();
        if (!context.level().isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir)) return;
        if (!state.canSurvive(context.level(), pos)) return;

        context.setBlock(pos, state);
    }

    protected void placeTallGrass(Context context, BlockPos pos) {
        BlockState lower = Blocks.TALL_GRASS.defaultBlockState();
        if (!context.level().isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir)) return;
        if (!context.level().isStateAtPosition(pos.above(), BlockBehaviour.BlockStateBase::isAir)) return;
        if (!lower.canSurvive(context.level(), pos)) return;

        context.setBlock(pos, lower);
        context.setBlock(pos.above(), lower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
    }
}