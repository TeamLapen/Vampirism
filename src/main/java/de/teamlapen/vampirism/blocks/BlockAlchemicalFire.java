package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * Alchemist's fire which does not spread
 */
public class BlockAlchemicalFire extends VampirismBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

    private static final String name = "alchemical_fire";

    public BlockAlchemicalFire() {
        super(name, Properties.create(Material.FIRE, MaterialColor.TNT).hardnessAndResistance(0.0f).lightValue(15).sound(SoundType.CLOTH).doesNotBlockMovement().tickRandomly());
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 3; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            EnumParticleTypes type = i == 0 ? BasicParticleType.SMOKE_LARGE : i == 1 ? EnumParticleTypes.SPELL_WITCH : rand.nextInt(10) == 0 ? EnumParticleTypes.FIREWORKS_SPARK : EnumParticleTypes.REDSTONE;
            worldIn.spawnParticle(type, d0, d1, d2, 0.0D, i == 2 ? 0.1D : 0.0D, 0.0D);
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }


    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isBurning(IBlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }


    @Override
    public boolean isCollidable() {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!isValidPosition(state, worldIn, pos)) {
            worldIn.removeBlock(pos);
        }
    }

    @Override
    public int quantityDropped(IBlockState state, Random random) {
        return 0;
    }

    /**
     * Marks the block to burn for an infinite time
     *
     * @param worldIn
     * @param pos
     * @param state
     */
    public void setBurningInfinite(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.with(AGE, 15), 4);
    }

    @Override
    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
        if (!this.isValidPosition(state, worldIn, pos)) {
            worldIn.removeBlock(pos);
        }


        int age = (state.get(AGE));


        if (age < 14) {
            state = state.with(AGE, age + 1);
            worldIn.setBlockState(pos, state, 4);
        } else if (age == 14) {
            worldIn.removeBlock(pos);
        }
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + random.nextInt(10));
    }

    @Override
    public int tickRate(IWorldReaderBase worldIn) {
        return 30;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(AGE);
    }


}
