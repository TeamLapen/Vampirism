package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class FirePlaceBlock extends VampirismBlock {
    private static final VoxelShape shape = makeShape();

    private static VoxelShape makeShape() {
        return Block.box(0, 0.01, 0, 16, 4, 16);
    }


    public FirePlaceBlock() {
        super(Properties.of(Material.WOOD).lightLevel(s -> 15).strength(1).noOcclusion());

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 2; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!canSurvive(state, world, pos)) {
            if (world instanceof IWorldWriter) {
                ((IWorldWriter) world).destroyBlock(pos, true);
            }
        }
    }
}
