package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Alchemist's fire which does not spread
 */
public class BlockAlchemicalFire extends VampirismBlock {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    private static final String name = "alchemical_fire";

    public BlockAlchemicalFire() {
        super(name, Material.FIRE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
        this.setTickRandomly(true);
        this.setCreativeTab(null);
        setHardness(0.0F);
        setLightLevel(1.0F);
        setSoundType(SoundType.CLOTH);
        disableStats();
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public MapColor getMapColor(IBlockState state) {
        return MapColor.TNT;
    }

    public int getMetaFromState(IBlockState state) {
        return (state.getValue(AGE));
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return true;
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
        if (!canPlaceBlockAt(worldIn, pos)) {
            worldIn.removeBlock(pos);
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }


        for (int i = 0; i < 3; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            EnumParticleTypes type = i == 0 ? EnumParticleTypes.SMOKE_LARGE : i == 1 ? EnumParticleTypes.SPELL_WITCH : rand.nextInt(10) == 0 ? EnumParticleTypes.FIREWORKS_SPARK : EnumParticleTypes.REDSTONE;
            worldIn.spawnParticle(type, d0, d1, d2, 0.0D, i == 2 ? 0.1D : 0.0D, 0.0D);
        }
    }

    public boolean requiresUpdates() {
        return false;
    }

    /**
     * Marks the block to burn for an infinite time
     *
     * @param worldIn
     * @param pos
     * @param state
     */
    public void setBurningInfinite(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.withProperty(AGE, 15), 4);
    }

    public int tickRate(World worldIn) {
        return 30;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {

        if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.removeBlock(pos);
        }


        int age = (state.getValue(AGE));


        if (age < 14) {
            state = state.withProperty(AGE, age + 1);
            worldIn.setBlockState(pos, state, 4);
        } else if (age == 14) {
            worldIn.removeBlock(pos);
        }

        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));


    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }
}
