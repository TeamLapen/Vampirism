package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;


public class CursedEarthBlock extends VampirismBlock implements IGrowable {

    private static final String name = "cursed_earth";

    public CursedEarthBlock() {
        super(name, Properties.create(Material.EARTH).hardnessAndResistance(0.5f, 2.0f).sound(SoundType.GROUND));

    }

    @Override
    public boolean canGrow(IBlockReader iBlockReader, BlockPos blockPos, BlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
        return plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source) {
        if (Block.isDirt(getBlock()))
            world.setBlockState(pos, ModBlocks.cursed_earth.getDefaultState(), 2);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.SHOVEL;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isAirBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            VampirismFlowerBlock blockflower = ModBlocks.vampire_orchid;
                            BlockState iblockstate = blockflower.getDefaultState();

                            if (blockflower.isValidPosition(iblockstate, worldIn, blockpos1)) {
                                worldIn.setBlockState(blockpos1, iblockstate, 3);
                            }
                        } else {
                            BlockState iblockstate1 = Blocks.TALL_GRASS.getDefaultState();

                            if (Blocks.TALL_GRASS.isValidPosition(iblockstate1, worldIn, blockpos1)) {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != ModBlocks.cursed_earth || worldIn.getBlockState(blockpos1).isNormalCube(worldIn, blockpos1)) {
                    break;
                }

                ++j;
            }
        }
    }
}
