package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Random;


public class BlockCursedEarth extends VampirismBlock implements IGrowable {

    private static final String name = "cursedEarth";

    public BlockCursedEarth() {
        super(name, Material.GROUND);
        this.setHardness(0.5F).setResistance(2.0F).setHarvestLevel("shovel", 0);
        setSoundType(SoundType.GROUND);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable instanceof BlockBush || plantable.getPlantType(world, pos).equals(VReference.vampirePlantType);
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
                            VampirismFlower blockflower = ModBlocks.vampirismFlower;
                            IBlockState iblockstate = blockflower.getDefaultState().withProperty(VampirismFlower.TYPE, VampirismFlower.EnumFlowerType.ORCHID);

                            if (blockflower.canBlockStay(worldIn, blockpos1, iblockstate)) {
                                worldIn.setBlockState(blockpos1, iblockstate, 3);
                            }
                        } else {
                            IBlockState iblockstate1 = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                            if (Blocks.TALLGRASS.canBlockStay(worldIn, blockpos1, iblockstate1)) {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != ModBlocks.cursedEarth || worldIn.getBlockState(blockpos1).isNormalCube()) {
                    break;
                }

                ++j;
            }
        }
    }
}
