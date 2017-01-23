package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.tileentity.TileAlchemicalCauldron;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;


/**
 * 1.10
 *
 * @author maxanier
 */
public class BlockAlchemicalCauldron extends VampirismBlockContainer {

    public final static String regName = "alchemicalCauldron";
    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final PropertyInteger LIQUID = PropertyInteger.create("liquid", 0, 2);
    public static final PropertyBool BURNING = PropertyBool.create("burning");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.87, 0.93);

    public BlockAlchemicalCauldron() {
        super(regName, Material.IRON);
        this.setHasFacing();
        this.setDefaultState(this.blockState.getBaseState().withProperty(LIQUID, 0).withProperty(FACING, EnumFacing.NORTH).withProperty(BURNING, false));
        this.setHardness(4);
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAlchemicalCauldron();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileAlchemicalCauldron t = getTile(worldIn, pos);
        if (t != null) {
            state = state.withProperty(LIQUID, !t.isFilled() ? 0 : t.isCooking() ? 2 : 1).withProperty(BURNING, t.isBurning());
        }
        return state;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileAlchemicalCauldron tile = getTile(worldIn, pos);
            if (tile != null) {
                if (tile.canUse(playerIn)) {
                    playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_ALCHEMICAL_CAULDRON, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileAlchemicalCauldron tile = getTile(worldIn, pos);
        if (tile != null && placer instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer) placer);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIQUID, BURNING);
    }

    @Nullable
    private TileAlchemicalCauldron getTile(IBlockAccess world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileAlchemicalCauldron) return (TileAlchemicalCauldron) t;
        return null;
    }
}
