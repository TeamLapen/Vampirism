package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.tileentity.TileAlchemicalCauldron;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nullable;


/**
 * 1.10
 *
 * @author maxanier
 */
public class BlockAlchemicalCauldron extends VampirismBlockContainer {

    public final static String regName = "alchemicalCauldron";
    public static final PropertyBool FILLED = PropertyBool.create("filled");
    public static final IUnlistedProperty<Integer> COLOR = new Properties.PropertyAdapter<>(PropertyInteger.create("color", 0, 7));

    public BlockAlchemicalCauldron() {
        super(regName, Material.IRON);
        this.setHasFacing();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FILLED, false).withProperty(FACING, EnumFacing.NORTH));
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAlchemicalCauldron();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileAlchemicalCauldron t = getTile(worldIn, pos);
        if (t != null && t.isFilled()) state = state.withProperty(FILLED, true);
        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        return extendedState.withProperty(COLOR, 0);//TODO
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
        TileAlchemicalCauldron tile = getTile(worldIn, pos);
        if (tile != null) {
            VampirismMod.log.t("Opening container");
            playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_BASIC, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer c = new ExtendedBlockState(this, new IProperty[]{FACING, FILLED}, new IUnlistedProperty[]{COLOR});
        return c;
    }

    @Nullable
    private TileAlchemicalCauldron getTile(IBlockAccess world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileAlchemicalCauldron) return (TileAlchemicalCauldron) t;
        return null;
    }
}
