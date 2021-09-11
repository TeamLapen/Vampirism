package de.teamlapen.vampirism.world.gen.features;

import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import java.util.Random;

public class VampirismLakeFeature extends LakeFeature {
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public VampirismLakeFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    /**
     * copied from {@link LakeFeature} and swapped hardcoded grass block with top layer block
     */
    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> p_159958_) {
        BlockPos blockpos = p_159958_.origin();
        WorldGenLevel worldgenlevel = p_159958_.level();
        Random random = p_159958_.random();

        BlockStateConfiguration blockstateconfiguration;
        for (blockstateconfiguration = p_159958_.config(); blockpos.getY() > worldgenlevel.getMinBuildHeight() + 5 && worldgenlevel.isEmptyBlock(blockpos); blockpos = blockpos.below()) {
        }

        if (blockpos.getY() <= worldgenlevel.getMinBuildHeight() + 4) {
            return false;
        } else {
            blockpos = blockpos.below(4);
            if (worldgenlevel.startsForFeature(SectionPos.of(blockpos), StructureFeature.VILLAGE).findAny().isPresent()) {
                return false;
            } else {
                boolean[] aboolean = new boolean[2048];
                int i = random.nextInt(4) + 4;

                for (int j = 0; j < i; ++j) {
                    double d0 = random.nextDouble() * 6.0D + 3.0D;
                    double d1 = random.nextDouble() * 4.0D + 2.0D;
                    double d2 = random.nextDouble() * 6.0D + 3.0D;
                    double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                    double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                    double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                    for (int l = 1; l < 15; ++l) {
                        for (int i1 = 1; i1 < 15; ++i1) {
                            for (int j1 = 1; j1 < 7; ++j1) {
                                double d6 = ((double) l - d3) / (d0 / 2.0D);
                                double d7 = ((double) j1 - d4) / (d1 / 2.0D);
                                double d8 = ((double) i1 - d5) / (d2 / 2.0D);
                                double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                                if (d9 < 1.0D) {
                                    aboolean[(l * 16 + i1) * 8 + j1] = true;
                                }
                            }
                        }
                    }
                }

                for (int k1 = 0; k1 < 16; ++k1) {
                    for (int k2 = 0; k2 < 16; ++k2) {
                        for (int k = 0; k < 8; ++k) {
                            boolean flag = !aboolean[(k1 * 16 + k2) * 8 + k] && (k1 < 15 && aboolean[((k1 + 1) * 16 + k2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + k2) * 8 + k] || k2 < 15 && aboolean[(k1 * 16 + k2 + 1) * 8 + k] || k2 > 0 && aboolean[(k1 * 16 + (k2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + k2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + k2) * 8 + (k - 1)]);
                            if (flag) {
                                Material material = worldgenlevel.getBlockState(blockpos.offset(k1, k, k2)).getMaterial();
                                if (k >= 4 && material.isLiquid()) {
                                    return false;
                                }

                                if (k < 4 && !material.isSolid() && worldgenlevel.getBlockState(blockpos.offset(k1, k, k2)) != blockstateconfiguration.state) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                for (int l1 = 0; l1 < 16; ++l1) {
                    for (int l2 = 0; l2 < 16; ++l2) {
                        for (int l3 = 0; l3 < 8; ++l3) {
                            if (aboolean[(l1 * 16 + l2) * 8 + l3]) {
                                BlockPos blockpos2 = blockpos.offset(l1, l3, l2);
                                boolean flag1 = l3 >= 4;
                                worldgenlevel.setBlock(blockpos2, flag1 ? AIR : blockstateconfiguration.state, 2);
                                if (flag1) {
                                    worldgenlevel.getBlockTicks().scheduleTick(blockpos2, AIR.getBlock(), 0);
                                    this.markAboveForPostProcessing(worldgenlevel, blockpos2);
                                }
                            }
                        }
                    }
                }

                for (int i2 = 0; i2 < 16; ++i2) {
                    for (int i3 = 0; i3 < 16; ++i3) {
                        for (int i4 = 4; i4 < 8; ++i4) {
                            if (aboolean[(i2 * 16 + i3) * 8 + i4]) {
                                BlockPos blockpos3 = blockpos.offset(i2, i4 - 1, i3);
                                if (isDirt(worldgenlevel.getBlockState(blockpos3)) && worldgenlevel.getBrightness(LightLayer.SKY, blockpos.offset(i2, i4, i3)) > 0) {
                                    Biome biome = worldgenlevel.getBiome(blockpos3);
// ---------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------- Replace Grass block with top material ---------------------
                                    worldgenlevel.setBlock(blockpos, biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), 2);
// ---------------------------------------------------------------------------------------------------------------------
                                }
                            }
                        }
                    }
                }

                if (blockstateconfiguration.state.getMaterial() == Material.LAVA) {
                    BaseStoneSource basestonesource = p_159958_.chunkGenerator().getBaseStoneSource();

                    for (int j3 = 0; j3 < 16; ++j3) {
                        for (int j4 = 0; j4 < 16; ++j4) {
                            for (int l4 = 0; l4 < 8; ++l4) {
                                boolean flag2 = !aboolean[(j3 * 16 + j4) * 8 + l4] && (j3 < 15 && aboolean[((j3 + 1) * 16 + j4) * 8 + l4] || j3 > 0 && aboolean[((j3 - 1) * 16 + j4) * 8 + l4] || j4 < 15 && aboolean[(j3 * 16 + j4 + 1) * 8 + l4] || j4 > 0 && aboolean[(j3 * 16 + (j4 - 1)) * 8 + l4] || l4 < 7 && aboolean[(j3 * 16 + j4) * 8 + l4 + 1] || l4 > 0 && aboolean[(j3 * 16 + j4) * 8 + (l4 - 1)]);
                                if (flag2 && (l4 < 4 || random.nextInt(2) != 0)) {
                                    BlockState blockstate = worldgenlevel.getBlockState(blockpos.offset(j3, l4, j4));
                                    if (blockstate.getMaterial().isSolid() && !blockstate.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                                        BlockPos blockpos1 = blockpos.offset(j3, l4, j4);
                                        worldgenlevel.setBlock(blockpos1, basestonesource.getBaseBlock(blockpos1), 2);
                                        this.markAboveForPostProcessing(worldgenlevel, blockpos1);
                                    }
                                }
                            }
                        }
                    }
                }

                if (blockstateconfiguration.state.getMaterial() == Material.WATER) {
                    for (int j2 = 0; j2 < 16; ++j2) {
                        for (int k3 = 0; k3 < 16; ++k3) {
                            int k4 = 4;
                            BlockPos blockpos4 = blockpos.offset(j2, 4, k3);
                            if (worldgenlevel.getBiome(blockpos4).shouldFreeze(worldgenlevel, blockpos4, false)) {
                                worldgenlevel.setBlock(blockpos4, Blocks.ICE.defaultBlockState(), 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
    }
}
