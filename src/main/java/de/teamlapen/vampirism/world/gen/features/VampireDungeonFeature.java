package de.teamlapen.vampirism.world.gen.features;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class VampireDungeonFeature extends MonsterRoomFeature {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();


    public VampireDungeonFeature(Codec<NoneFeatureConfiguration> featureConfig) {
        super(featureConfig);
    }


    /**
     * Last update 1.16.5
     * copied from {@link MonsterRoomFeature#place(FeaturePlaceContext)}
     * <p>
     * - changed {@link Blocks#MOSSY_COBBLESTONE} to {@link ModBlocks#castle_block_dark_brick} and {@link ModBlocks#castle_block_dark_brick_bloody}
     * - changed {@link Blocks#COBBLESTONE} to {@link Blocks#SPRUCE_PLANKS}
     * - changed {@link net.minecraft.world.level.storage.loot.BuiltInLootTables#SIMPLE_DUNGEON} to {@link ModLootTables#chest_vampire_dungeon}
     * - changed {@link MonsterRoomFeature#randomEntityId(Random)} to {@link ModEntities#vampire}
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        int j = context.random().nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int k1 = context.random().nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i2 = k1 + 1;
        int j2 = 0;

        for (int k2 = k; k2 <= l; ++k2) {
            for (int l2 = -1; l2 <= 4; ++l2) {
                for (int i3 = l1; i3 <= i2; ++i3) {
                    BlockPos blockpos = context.origin().offset(k2, l2, i3);
                    Material material = context.level().getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();
                    if (l2 == -1 && !flag) {
                        return false;
                    }

                    if (l2 == 4 && !flag) {
                        return false;
                    }

                    if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && context.level().isEmptyBlock(blockpos) && context.level().isEmptyBlock(blockpos.above())) {
                        ++j2;
                    }
                }
            }
        }

        if (j2 >= 1 && j2 <= 5) {
            for (int k3 = k; k3 <= l; ++k3) {
                for (int i4 = 3; i4 >= -1; --i4) {
                    for (int k4 = l1; k4 <= i2; ++k4) {
                        BlockPos blockpos1 = context.origin().offset(k3, i4, k4);
                        BlockState blockstate = context.level().getBlockState(blockpos1);
                        if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                            if (!blockstate.is(Blocks.CHEST) && !blockstate.is(Blocks.SPAWNER)) {
                                context.level().setBlock(blockpos1, CAVE_AIR, 2);
                            }
                        } else if (blockpos1.getY() >= 0 && !context.level().getBlockState(blockpos1.below()).getMaterial().isSolid()) {
                            context.level().setBlock(blockpos1, CAVE_AIR, 2);
                        } else if (blockstate.getMaterial().isSolid() && !blockstate.is(Blocks.CHEST)) {
                            if (i4 == -1 && context.random().nextInt(4) != 0) {
                                if (context.random().nextInt(20) == 0) // changed to castle bricks
                                    context.level().setBlock(blockpos1, ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get().defaultBlockState(), 2);
                                else
                                    context.level().setBlock(blockpos1, ModBlocks.CASTLE_BLOCK_DARK_BRICK.get().defaultBlockState(), 2);
                            } else {
                                context.level().setBlock(blockpos1, Blocks.SPRUCE_PLANKS.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }

            for (int l3 = 0; l3 < 2; ++l3) {
                for (int j4 = 0; j4 < 3; ++j4) {
                    int l4 = context.origin().getX() + context.random().nextInt(j * 2 + 1) - j;
                    int i5 = context.origin().getY();
                    int j5 = context.origin().getZ() + context.random().nextInt(k1 * 2 + 1) - k1;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);
                    if (context.level().isEmptyBlock(blockpos2)) {
                        int j3 = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (context.level().getBlockState(blockpos2.relative(direction)).getMaterial().isSolid()) {
                                ++j3;
                            }
                        }

                        if (j3 == 1) {
                            context.level().setBlock(blockpos2, StructurePiece.reorient(context.level(), blockpos2, Blocks.CHEST.defaultBlockState()), 2);
                            RandomizableContainerBlockEntity.setLootTable(context.level(), context.random(), blockpos2, ModLootTables.chest_vampire_dungeon);
                            break;
                        }
                    }
                }
            }

            context.level().setBlock(context.origin(), Blocks.SPAWNER.defaultBlockState(), 2);
            BlockEntity tileentity = context.level().getBlockEntity(context.origin());
            if (tileentity instanceof SpawnerBlockEntity) {
                ((SpawnerBlockEntity) tileentity).getSpawner().setEntityId(ModEntities.VAMPIRE.get());
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", context.origin().getX(), context.origin().getY(), context.origin().getZ());
            }
//            if (VampirismWorldGen.debug) {
//                LOGGER.info("Generated dungeon at {}", context.origin());
//            }
            return true;
        } else {
            return false;
        }

    }


}
