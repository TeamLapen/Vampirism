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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Tileentity container that can store liquids.
 */
public class BlockBloodContainer extends VampirismBlockContainer {
    public static final IUnlistedProperty<Integer> FLUID_LEVEL = new Properties.PropertyAdapter<>(PropertyInteger.create("fluidLevel", 0, 14));
    public static final IUnlistedProperty<String> FLUID_NAME = new PropertyStringUnlisted("fluidName");
    public final static String regName = "blood_container";

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
        TileBloodContainer tile = (TileBloodContainer) (world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos));
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
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        super.getSubBlocks(itemIn, items);
        ItemStack stack = new ItemStack(this, 1);
        FluidStack fluid = new FluidStack(ModFluids.blood, TileBloodContainer.CAPACITY);
        stack.setTagInfo("fluid", fluid.writeToNBT(new NBTTagCompound()));
        items.add(stack);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.blood_container, 1);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty() && FluidLib.hasFluidItemCap(stack)) {
                TileBloodContainer bloodContainer = (TileBloodContainer) worldIn.getTileEntity(pos);
                IFluidHandler source = bloodContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (playerIn.isSneaking()) {
                    FluidActionResult result = FluidUtil.tryFillContainer(stack, source, Integer.MAX_VALUE, playerIn, true);
                    if (result.isSuccess()) {
                        playerIn.setHeldItem(hand, result.getResult());
                    }
                    //fillContainerFromTank(worldIn, pos, playerIn, stack, bloodContainer);
                } else {
                    FluidActionResult result = FluidUtil.tryEmptyContainer(stack, source, Integer.MAX_VALUE, playerIn, true);
                    //drainContainerIntoTank(worldIn, pos, playerIn, stack, bloodContainer);
                    if (result.isSuccess()) {
                        playerIn.setHeldItem(hand, result.getResult());
                    }
                }
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
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid == null) {
                VampirismMod.log.w("BloodContainer", "Failed to load fluid from item nbt %s", nbt);
            } else {
                TileEntity tile = (worldIn.getTileEntity(pos));
                if (tile instanceof TileBloodContainer) {
                    ((TileBloodContainer) tile).setFluidStack(fluid);
                }
            }

        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{FLUID_LEVEL, FLUID_NAME});
    }


}
