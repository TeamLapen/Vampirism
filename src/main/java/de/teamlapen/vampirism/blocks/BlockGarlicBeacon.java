package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TileGarlicBeacon;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 1.10
 *
 * @author maxanier
 */
public class BlockGarlicBeacon extends VampirismBlockContainer {

    private final static String regName = "garlicBeacon";

    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    private final static AxisAlignedBB COLLISION_BOX_1 = new AxisAlignedBB(0.19, 0, 0.19, 0.81, 0.75, 0.81);
    private final static AxisAlignedBB COLLISION_BOX_2 = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.19, 0.93);

    public BlockGarlicBeacon() {
        super(regName, Material.WOOD);
        this.setHasFacing();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_1);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_2);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGarlicBeacon();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

        return BOUNDING_BOX;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing face = EnumFacing.getHorizontal(meta);
        return this.getDefaultState().withProperty(FACING, face);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
