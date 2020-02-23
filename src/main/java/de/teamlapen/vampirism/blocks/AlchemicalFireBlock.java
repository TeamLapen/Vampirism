package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * Alchemist's fire which does not spread
 */
public class AlchemicalFireBlock extends VampirismBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

    private static final String name = "alchemical_fire";

    public AlchemicalFireBlock() {
        super(name, Properties.create(Material.FIRE, MaterialColor.TNT).hardnessAndResistance(0.0f).lightValue(15).sound(SoundType.CLOTH).doesNotBlockMovement().tickRandomly().notSolid().noDrops());
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 3; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            IParticleData type = i == 0 ? ParticleTypes.LARGE_SMOKE : i == 1 ? ParticleTypes.WITCH : rand.nextInt(10) == 0 ? ParticleTypes.FIREWORK : RedstoneParticleData.REDSTONE_DUST;
            worldIn.addParticle(type, d0, d1, d2, 0.0D, i == 2 ? 0.1D : 0.0D, 0.0D);
        }
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isBurning(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }


    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return Block.hasSolidSide(worldIn.getBlockState(pos.down()), worldIn, pos.down(), Direction.UP);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!isValidPosition(state, worldIn, pos)) {
            worldIn.removeBlock(pos, isMoving);
        }
    }

    /**
     * Marks the block to burn for an infinite time
     *
     * @param worldIn
     * @param pos
     * @param state
     */
    public void setBurningInfinite(World worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, state.with(AGE, 15), 4);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!this.isValidPosition(state, worldIn, pos)) {
            worldIn.removeBlock(pos, this.blocksMovement);
        }


        int age = (state.get(AGE));


        if (age < 14) {
            state = state.with(AGE, age + 1);
            worldIn.setBlockState(pos, state, 4);
        } else if (age == 14) {
            worldIn.removeBlock(pos, this.blocksMovement);
        }
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn) + random.nextInt(10));
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return 30;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }


}
