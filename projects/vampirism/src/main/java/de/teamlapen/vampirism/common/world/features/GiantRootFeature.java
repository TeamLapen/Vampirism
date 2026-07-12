package de.teamlapen.vampirism.common.world.features;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.common.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;

public class GiantRootFeature extends Feature<GiantRootConfiguration> {

    public GiantRootFeature(Codec<GiantRootConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GiantRootConfiguration> context) {
        GiantRootConfiguration config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int topRadius = Mth.nextInt(random, config.minTopRadius(), config.maxTopRadius());
        double thickness = (double) (topRadius - config.minTopRadius()) / (config.maxTopRadius() - config.minTopRadius());
        int startY = origin.getY() - config.topOffset();
        int bottomY = Mth.clamp((int) Math.round(Mth.lerp(thickness, config.minLowestReachY(), config.maxLowestReachY())) + random.nextInt(5) - 2, config.maxLowestReachY(), startY - 4);
        if (startY <= bottomY) return false;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double centerX = origin.getX() + 0.5;
        double centerZ = origin.getZ() + 0.5;
        int bodyHeight = startY - bottomY;
        int topY = startY + topRadius;
        boolean placedAny = false;

        for (int y = topY; y >= bottomY; y--) {
            double baseRadius;
            if (y > startY) {
                double capRadiusSq = (double) topRadius * topRadius - (double) (y - startY) * (y - startY);
                baseRadius = capRadiusSq > 0 ? Math.sqrt(capRadiusSq) : 0;
            } else {
                double taperProgress = (double) (y - bottomY) / bodyHeight;
                baseRadius = config.tipRadius() + (topRadius - config.tipRadius()) * Math.pow(taperProgress, config.taperExponent());
            }
            double radius = baseRadius + (random.nextDouble() - 0.5) * 0.6;

            centerX = Mth.clamp(centerX + (random.nextDouble() - 0.5) * 0.4, origin.getX() + 0.5 - config.maxHorizontalOffset(), origin.getX() + 0.5 + config.maxHorizontalOffset());
            centerZ = Mth.clamp(centerZ + (random.nextDouble() - 0.5) * 0.4, origin.getZ() + 0.5 - config.maxHorizontalOffset(), origin.getZ() + 0.5 + config.maxHorizontalOffset());

            int centerBlockX = Mth.floor(centerX);
            int centerBlockZ = Mth.floor(centerZ);
            int radiusCeil = Mth.ceil(radius);
            double innerSq = (radius - 0.7) * (radius - 0.7);
            double radiusSq = radius * radius;

            for (int dx = -radiusCeil; dx <= radiusCeil; dx++) {
                for (int dz = -radiusCeil; dz <= radiusCeil; dz++) {
                    int blockX = centerBlockX + dx;
                    int blockZ = centerBlockZ + dz;
                    double offsetX = blockX + 0.5 - centerX;
                    double offsetZ = blockZ + 0.5 - centerZ;
                    double distSq = offsetX * offsetX + offsetZ * offsetZ;

                    if (distSq > radiusSq) continue;
                    if (distSq > innerSq && random.nextInt(3) == 0) continue;

                    pos.set(blockX, y, blockZ);
                    if (canReplace(level, pos)) {
                        this.setBlock(level, pos, config.block());
                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    private static boolean canReplace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Tags.Blocks.ORES)) {
            return false;
        }
        if (state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA)) {
            return pos.getY() < level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
        }
        if (state.is(BlockTags.DIRT)) {
            return pos.getY() < level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) - 4;
        }
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(ModBlocks.DARK_STONE.get());
    }
}
