package de.teamlapen.vampirism.world.gen.features;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModLootTables;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.DungeonsFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class VampireDungeonFeature extends DungeonsFeature {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();


    public VampireDungeonFeature(Codec<NoFeatureConfig> featureConfig) {
        super(featureConfig);
    }


    /**
     * Last update 1.16.5
     * copied from {@link net.minecraft.world.gen.feature.DungeonsFeature#generate}
     * <p>
     * - changed {@link Blocks#MOSSY_COBBLESTONE} to {@link ModBlocks#castle_block_dark_brick} and {@link ModBlocks#castle_block_dark_brick_bloody}
     * - changed {@link Blocks#COBBLESTONE} to {@link Blocks#SPRUCE_PLANKS}
     * - changed {@link LootTables#CHESTS_SIMPLE_DUNGEON} to {@link ModLootTables#chest_vampire_dungeon}
     * - changed {@link DungeonsFeature#getRandomDungeonMob(Random)} to {@link ModEntities#vampire}
     */
    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        int j = rand.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int k1 = rand.nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i2 = k1 + 1;
        int j2 = 0;

        for (int k2 = k; k2 <= l; ++k2) {
            for (int l2 = -1; l2 <= 4; ++l2) {
                for (int i3 = l1; i3 <= i2; ++i3) {
                    BlockPos blockpos = pos.offset(k2, l2, i3);
                    Material material = reader.getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();
                    if (l2 == -1 && !flag) {
                        return false;
                    }

                    if (l2 == 4 && !flag) {
                        return false;
                    }

                    if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && reader.isEmptyBlock(blockpos) && reader.isEmptyBlock(blockpos.above())) {
                        ++j2;
                    }
                }
            }
        }

        if (j2 >= 1 && j2 <= 5) {
            for (int k3 = k; k3 <= l; ++k3) {
                for (int i4 = 3; i4 >= -1; --i4) {
                    for (int k4 = l1; k4 <= i2; ++k4) {
                        BlockPos blockpos1 = pos.offset(k3, i4, k4);
                        BlockState blockstate = reader.getBlockState(blockpos1);
                        if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                            if (!blockstate.is(Blocks.CHEST) && !blockstate.is(Blocks.SPAWNER)) {
                                reader.setBlock(blockpos1, CAVE_AIR, 2);
                            }
                        } else if (blockpos1.getY() >= 0 && !reader.getBlockState(blockpos1.below()).getMaterial().isSolid()) {
                            reader.setBlock(blockpos1, CAVE_AIR, 2);
                        } else if (blockstate.getMaterial().isSolid() && !blockstate.is(Blocks.CHEST)) {
                            if (i4 == -1 && rand.nextInt(4) != 0) {
                                if (rand.nextInt(20) == 0) // changed to castle bricks
                                    reader.setBlock(blockpos1, ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get().defaultBlockState(), 2);
                                else
                                    reader.setBlock(blockpos1, ModBlocks.CASTLE_BLOCK_DARK_BRICK.get().defaultBlockState(), 2);
                            } else {
                                reader.setBlock(blockpos1, Blocks.SPRUCE_PLANKS.defaultBlockState(), 2);
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
                    if (reader.isEmptyBlock(blockpos2)) {
                        int j3 = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (reader.getBlockState(blockpos2.relative(direction)).getMaterial().isSolid()) {
                                ++j3;
                            }
                        }

                        if (j3 == 1) {
                            reader.setBlock(blockpos2, StructurePiece.reorient(reader, blockpos2, Blocks.CHEST.defaultBlockState()), 2);
                            LockableLootTileEntity.setLootTable(reader, rand, blockpos2, ModLootTables.chest_vampire_dungeon);
                            break;
                        }
                    }
                }
            }

            reader.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
            TileEntity tileentity = reader.getBlockEntity(pos);
            if (tileentity instanceof MobSpawnerTileEntity) {
                ((MobSpawnerTileEntity) tileentity).getSpawner().setEntityId(ModEntities.VAMPIRE.get());
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
            }
            if (VampirismWorldGen.debug) {
                LOGGER.info("Generated dungeon at {}", pos);
            }
            return true;
        } else {
            return false;
        }

    }


}
