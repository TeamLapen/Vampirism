package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;


public class BlockCursedEarth extends VampirismBlock implements IGrowable {

    private static final String name = "cursed_earth";

    public BlockCursedEarth() {
        super(name, Properties.create(Material.GROUND).hardnessAndResistance(0.5f, 2.0f).sound(SoundType.GROUND));

    }

    @Override
    public boolean canGrow(IBlockReader iBlockReader, BlockPos blockPos, IBlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public int getHarvestLevel(IBlockState p_getHarvestLevel_1_) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState p_getHarvestTool_1_) {
        return ToolType.SHOVEL;
    }


    @Override
    public boolean canSustainPlant(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable instanceof BlockBush || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }


    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isAirBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            VampirismFlower blockflower = ModBlocks.vampirism_flower_orchid;
                            IBlockState iblockstate = blockflower.getDefaultState();

                            if (blockflower.isValidPosition(iblockstate, worldIn, blockpos1)) {
                                worldIn.setBlockState(blockpos1, iblockstate, 3);
                            }
                        } else {
                            IBlockState iblockstate1 = Blocks.TALL_GRASS.getDefaultState();

                            if (Blocks.TALL_GRASS.isValidPosition(iblockstate1, worldIn, blockpos1)) {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != ModBlocks.cursed_earth || worldIn.getBlockState(blockpos1).isNormalCube()) {
                    break;
                }

                ++j;
            }
        }
    }
}
