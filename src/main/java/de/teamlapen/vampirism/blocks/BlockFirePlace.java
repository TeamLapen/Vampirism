package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;


public class BlockFirePlace extends VampirismBlock {
    protected static final AxisAlignedBB BBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D); //TODO 1.13 shape
    private final static String regName = "fire_place";

    public BlockFirePlace() {
        super(regName, Properties.create(Material.WOOD).lightValue(15).hardnessAndResistance(1));

    }


    @Override
    public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return world.getBlockState(pos.down()).isTopSolid();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }



    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public void onNeighborChange(IBlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!isValidPosition(state, world, pos)) {
            if (world instanceof IWorldWriter) {
                ((IWorldWriter) world).destroyBlock(pos, true);
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 2; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

    }
}
