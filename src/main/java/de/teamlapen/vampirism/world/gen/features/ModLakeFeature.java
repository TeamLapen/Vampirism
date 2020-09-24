package de.teamlapen.vampirism.world.gen.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Random;

public class ModLakeFeature extends LakesFeature {
    private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

    public ModLakeFeature(Codec<BlockStateFeatureConfig> p_i231968_1_) {
        super(p_i231968_1_);
    }

    /**
     * copied from {@link LakesFeature}
     */
    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockStateFeatureConfig p_241855_5_) {
        while (p_241855_4_.getY() > 5 && p_241855_1_.isAirBlock(p_241855_4_)) {
            p_241855_4_ = p_241855_4_.down();
        }

        if (p_241855_4_.getY() <= 4) {
            return false;
        } else {
            p_241855_4_ = p_241855_4_.down(4);
            if (p_241855_1_.func_241827_a(SectionPos.from(p_241855_4_), Structure.field_236381_q_).findAny().isPresent()) {
                return false;
            } else {
                boolean[] aboolean = new boolean[2048];
                int i = p_241855_3_.nextInt(4) + 4;

                for (int j = 0; j < i; ++j) {
                    double d0 = p_241855_3_.nextDouble() * 6.0D + 3.0D;
                    double d1 = p_241855_3_.nextDouble() * 4.0D + 2.0D;
                    double d2 = p_241855_3_.nextDouble() * 6.0D + 3.0D;
                    double d3 = p_241855_3_.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                    double d4 = p_241855_3_.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                    double d5 = p_241855_3_.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

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
                    for (int l2 = 0; l2 < 16; ++l2) {
                        for (int k = 0; k < 8; ++k) {
                            boolean flag = !aboolean[(k1 * 16 + l2) * 8 + k] && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + l2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + l2) * 8 + (k - 1)]);
                            if (flag) {
                                Material material = p_241855_1_.getBlockState(p_241855_4_.add(k1, k, l2)).getMaterial();
                                if (k >= 4 && material.isLiquid()) {
                                    return false;
                                }

                                if (k < 4 && !material.isSolid() && p_241855_1_.getBlockState(p_241855_4_.add(k1, k, l2)) != p_241855_5_.state) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                for (int l1 = 0; l1 < 16; ++l1) {
                    for (int i3 = 0; i3 < 16; ++i3) {
                        for (int i4 = 0; i4 < 8; ++i4) {
                            if (aboolean[(l1 * 16 + i3) * 8 + i4]) {
                                p_241855_1_.setBlockState(p_241855_4_.add(l1, i4, i3), i4 >= 4 ? AIR : p_241855_5_.state, 2);
                            }
                        }
                    }
                }

                for (int i2 = 0; i2 < 16; ++i2) {
                    for (int j3 = 0; j3 < 16; ++j3) {
                        for (int j4 = 4; j4 < 8; ++j4) {
                            if (aboolean[(i2 * 16 + j3) * 8 + j4]) {
                                BlockPos blockpos = p_241855_4_.add(i2, j4 - 1, j3);
                                if (isDirt(p_241855_1_.getBlockState(blockpos).getBlock()) && p_241855_1_.getLightFor(LightType.SKY, p_241855_4_.add(i2, j4, j3)) > 0) {
                                    Biome biome = p_241855_1_.getBiome(blockpos);
                                    p_241855_1_.setBlockState(blockpos, biome.func_242440_e().func_242502_e().getTop(), 2);
                                }
                            }
                        }
                    }
                }

                if (p_241855_5_.state.getMaterial() == Material.LAVA) {
                    for (int j2 = 0; j2 < 16; ++j2) {
                        for (int k3 = 0; k3 < 16; ++k3) {
                            for (int k4 = 0; k4 < 8; ++k4) {
                                boolean flag1 = !aboolean[(j2 * 16 + k3) * 8 + k4] && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)]);
                                if (flag1 && (k4 < 4 || p_241855_3_.nextInt(2) != 0) && p_241855_1_.getBlockState(p_241855_4_.add(j2, k4, k3)).getMaterial().isSolid()) {
                                    p_241855_1_.setBlockState(p_241855_4_.add(j2, k4, k3), Blocks.STONE.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }

                if (p_241855_5_.state.getMaterial() == Material.WATER) {
                    for (int k2 = 0; k2 < 16; ++k2) {
                        for (int l3 = 0; l3 < 16; ++l3) {
                            int l4 = 4;
                            BlockPos blockpos1 = p_241855_4_.add(k2, 4, l3);
                            if (p_241855_1_.getBiome(blockpos1).doesWaterFreeze(p_241855_1_, blockpos1, false)) {
                                p_241855_1_.setBlockState(blockpos1, Blocks.ICE.getDefaultState(), 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
    }
}
