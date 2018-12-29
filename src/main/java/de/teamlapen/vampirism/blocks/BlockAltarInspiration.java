package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.tileentity.TileAltarInspiration;
import de.teamlapen.vampirism.world.VampirismWorldData;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class BlockAltarInspiration extends VampirismBlockContainer {
    public final static String regName = "altar_inspiration";
    public static final IUnlistedProperty<Integer> FLUID_LEVEL = new Properties.PropertyAdapter<>(PropertyInteger.create("fluidLevel", 0, 10));

    public BlockAltarInspiration() {
        super(regName, Material.IRON);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(2F);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        VampirismWorldData.get(worldIn).onAltarInspirationDestroyed(pos);

    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAltarInspiration();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        TileAltarInspiration tile = (TileAltarInspiration) world.getTileEntity(pos);
        if (tile != null) {
            FluidStack fluid = tile.getTankInfo().fluid;
            if (fluid != null) {
                float i = (fluid.amount / (float) TileAltarInspiration.CAPACITY * 10);
                int l = (i > 0 && i < 1) ? 1 : (int) i;
                return extendedBlockState.withProperty(FLUID_LEVEL, l);
            }
        }
        return extendedBlockState.withProperty(FLUID_LEVEL, 0);
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() && !worldIn.isRemote) {
            if (FluidLib.hasFluidItemCap(stack)) {
                TileAltarInspiration tileEntity = (TileAltarInspiration) worldIn.getTileEntity(pos);
                if (!playerIn.isSneaking()) {
                    FluidActionResult result = FluidUtil.tryEmptyContainer(stack, tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), Integer.MAX_VALUE, playerIn, true);
                    if (result.isSuccess()) {
                        playerIn.setHeldItem(hand, result.getResult());
                    }
                }
                worldIn.notifyBlockUpdate(pos, state, state, 3);
                tileEntity.markDirty();
                return true;
            }
        }
        if (stack.isEmpty()) {
            TileAltarInspiration tileEntity = (TileAltarInspiration) worldIn.getTileEntity(pos);
            tileEntity.startRitual(playerIn);
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{FLUID_LEVEL});
    }
}
