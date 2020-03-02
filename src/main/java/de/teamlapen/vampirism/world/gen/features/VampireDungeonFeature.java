package de.teamlapen.vampirism.world.gen.features;

import com.mojang.datafixers.Dynamic;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.tileentity.AltarInspirationTileEntity;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

public class VampireDungeonFeature extends Feature<NoFeatureConfig> {
    private static final Logger LOGGER = LogManager.getLogger();

    public VampireDungeonFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> featureConfig) {
        super(featureConfig);
    }

    /**
     * almost copied from {@link net.minecraft.world.gen.feature.DungeonsFeature#place(net.minecraft.world.IWorld, net.minecraft.world.gen.ChunkGenerator, java.util.Random, net.minecraft.util.math.BlockPos, net.minecraft.world.gen.feature.NoFeatureConfig)}
     */
    @Override
    public boolean place(@Nonnull IWorld worldIn, @Nonnull ChunkGenerator<? extends GenerationSettings> generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        int i = 3;
        int j = rand.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int i1 = -1;
        int j1 = 4;
        int k1 = rand.nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i2 = k1 + 1;
        int j2 = 0;

        for (int k2 = k; k2 <= l; ++k2) {
            for (int l2 = -1; l2 <= 4; ++l2) {
                for (int i3 = l1; i3 <= i2; ++i3) {
                    BlockPos blockpos = pos.add(k2, l2, i3);
                    Material material = worldIn.getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();
                    if (l2 == -1 && !flag) {
                        return false;
                    }

                    if (l2 == 4 && !flag) {
                        return false;
                    }

                    if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up())) {
                        ++j2;
                    }
                }
            }
        }

        if (j2 >= 1 && j2 <= 5) {
            for (int k3 = k; k3 <= l; ++k3) {
                for (int i4 = 3; i4 >= -1; --i4) {
                    for (int k4 = l1; k4 <= i2; ++k4) {
                        BlockPos blockpos1 = pos.add(k3, i4, k4);
                        if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                            if (worldIn.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                                worldIn.setBlockState(blockpos1, Blocks.CAVE_AIR.getDefaultState(), 2);
                            }
                        } else if (blockpos1.getY() >= 0 && !worldIn.getBlockState(blockpos1.down()).getMaterial().isSolid()) {
                            worldIn.setBlockState(blockpos1, Blocks.CAVE_AIR.getDefaultState(), 2);
                        } else if (worldIn.getBlockState(blockpos1).getMaterial().isSolid() && worldIn.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                            if (i4 == -1 && rand.nextInt(4) != 0) {
                                BlockState stone;
                                if (rand.nextInt(40) == 0)
                                    stone = ModBlocks.castle_block_dark_brick_bloody.getDefaultState();
                                else
                                    stone = ModBlocks.castle_block_dark_brick.getDefaultState();
                                worldIn.setBlockState(blockpos1, stone, 2);
                            } else {
                                worldIn.setBlockState(blockpos1, Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            for (int l3 = 0; l3 < 2; ++l3) {
                for (int j4 = 0; j4 < 3; ++j4) {
                    int l4 = pos.getX() + rand.nextInt(j * 2 + 1) - j;
                    int i5 = pos.getY();
                    int j5 = pos.getZ() + rand.nextInt(k1 * 2 + 1) - k1;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);
                    if (worldIn.isAirBlock(blockpos2)) {
                        int j3 = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (worldIn.getBlockState(blockpos2.offset(direction)).getMaterial().isSolid()) {
                                ++j3;
                            }
                        }

                        if (j3 == 1) {
                            worldIn.setBlockState(blockpos2, StructurePiece.func_197528_a(worldIn, blockpos2, Blocks.CHEST.getDefaultState()), 2);
                            LockableLootTileEntity.setLootTable(worldIn, rand, blockpos2, ModLootTables.chest_vampire_dungeon);
                            break;
                        }
                    }
                }
                for (int j4 = 0; j4 < 3; ++j4) {
                    int l4 = pos.getX() + rand.nextInt(j * 2 + 1) - j;
                    int i5 = pos.getY();
                    int j5 = pos.getZ() + rand.nextInt(k1 * 2 + 1) - k1;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);
                    if (worldIn.isAirBlock(blockpos2)) {
                        int j3 = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (worldIn.getBlockState(blockpos2.offset(direction)).getMaterial().isSolid()) {
                                ++j3;
                            }
                        }

                        if (j3 == 1) {
                            worldIn.setBlockState(blockpos2, ModBlocks.blood_container.getDefaultState(), 2);
                            BloodContainerTileEntity.setBloodValue(worldIn, rand, blockpos2);
                            break;
                        }
                    }
                }
            }
            for (int j4 = 0; j4 < 20; ++j4) {
                int l4 = pos.getX() + rand.nextInt(j * 2 + 1) - j;
                int i5 = pos.getY();
                int j5 = pos.getZ() + rand.nextInt(k1 * 2 + 1) - k1;
                BlockPos blockpos2 = new BlockPos(l4, i5, j5);
                if (worldIn.isAirBlock(blockpos2)) {
                    int j3 = 0;

                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        if (worldIn.getBlockState(blockpos2.offset(direction)).getMaterial().isSolid()) {
                            ++j3;
                        }
                    }

                    if (j3 == 1) {
                        worldIn.setBlockState(blockpos2, ModBlocks.altar_inspiration.getDefaultState(), 2);
                        AltarInspirationTileEntity.setBloodValue(worldIn, rand, blockpos2);
                        break;
                    }
                }
            }
            worldIn.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof MobSpawnerTileEntity) {
                ((MobSpawnerTileEntity) tileentity).getSpawnerBaseLogic().setEntityType(this.getRandomDungeonMob(rand));
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        } else {
            return false;
        }
    }

    private EntityType<?> getRandomDungeonMob(Random random) {
        return ModEntities.vampire;
    }

}
