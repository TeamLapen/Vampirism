package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.block.PropertyStringUnlisted;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.List;

/**
 * Tileentity container that can store liquids.
 */
public class BlockBloodContainer extends VampirismBlockContainer {
    public static final IUnlistedProperty<Integer> FLUID_LEVEL = new Properties.PropertyAdapter<>(PropertyInteger.create("fluidLevel", 0, 14));
    public static final IUnlistedProperty<String> FLUID_NAME = new PropertyStringUnlisted("fluidName");
    public final static String regName = "bloodContainer";

    public BlockBloodContainer() {
        super(regName, Material.GLASS);
        this.setHardness(1F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBloodContainer();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        TileBloodContainer tile = (TileBloodContainer) world.getTileEntity(pos);
        if (tile != null) {
            FluidStack fluid = tile.getTankInfo().fluid;
            float amount = fluid == null ? 0 : fluid.amount / (float) TileBloodContainer.LEVEL_AMOUNT;
            return extendedState.withProperty(FLUID_LEVEL, (amount > 0 && amount < 1) ? 1 : (int) amount).withProperty(FLUID_NAME, fluid == null ? "" : fluid.getFluid().getName());

        }
        return extendedState.withProperty(FLUID_LEVEL, 0).withProperty(FLUID_NAME, "");
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        super.getSubBlocks(itemIn, tab, list);
        ItemStack stack = new ItemStack(itemIn, 1);
        FluidStack fluid = new FluidStack(ModFluids.blood, TileBloodContainer.CAPACITY);
        stack.setTagInfo("fluid", fluid.writeToNBT(new NBTTagCompound()));
        list.add(stack);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.bloodContainer, 1);
        FluidStack fluid = ((TileBloodContainer) te).getTankInfo().fluid;
        if (fluid != null && fluid.amount > 0) {
            stack.setTagInfo("fluid", fluid.writeToNBT(new NBTTagCompound()));
        }
        spawnAsEntity(worldIn, pos, stack);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = heldItem;
            if (stack != null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                    TileBloodContainer bloodContainer = (TileBloodContainer) worldIn.getTileEntity(pos);
                if (playerIn.isSneaking()) {
                        fillContainerFromTank(worldIn, pos, playerIn, stack, bloodContainer);
                    } else {
                        drainContainerIntoTank(worldIn, pos, playerIn, stack, bloodContainer);
                    }
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                    bloodContainer.markDirty();
                    return true;
                }
            }

        return true;

    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fluid")) {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("fluid");
            if (nbt != null) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                if (fluid == null) {
                    VampirismMod.log.w("BloodContainer", "Failed to load fluid from item nbt %s", nbt);
                } else {
                    ((TileBloodContainer) worldIn.getTileEntity(pos)).setFluidStack(fluid);
                }
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{FLUID_LEVEL, FLUID_NAME});
    }

    /**
     * Does NOT check if capabilties exist
     */
    private void drainContainerIntoTank(World worldIn, BlockPos pos, EntityPlayer playerIn, ItemStack stack, TileBloodContainer bloodContainer) {

        FluidLib.drainContainerIntoTank(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), bloodContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
    }

    /**
     * Does NOT check if capabilties exist
     */
    private void fillContainerFromTank(World worldIn, BlockPos pos, EntityPlayer playerIn, ItemStack stack, TileBloodContainer bloodContainer) {
        FluidLib.drainContainerIntoTank(bloodContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
    }

}
