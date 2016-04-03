package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.block.PropertyStringUnlisted;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 * Tileentity container that can store liquids.
 * Supports {@link IFluidContainerItem} as well as containers registered in {@link FluidContainerRegistry}
 */
public class BlockBloodContainer extends VampirismBlockContainer {
    public static final IUnlistedProperty<Integer> FLUID_LEVEL = new Properties.PropertyAdapter<>(PropertyInteger.create("fluidLevel", 0, 14));
    public static final IUnlistedProperty<String> FLUID_NAME = new PropertyStringUnlisted("fluidName");
    public final static String regName = "bloodContainer";

    public BlockBloodContainer() {
        super(regName, Material.glass);
        this.setHardness(1F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBloodContainer();
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        TileBloodContainer tile = (TileBloodContainer) world.getTileEntity(pos);
        if (tile != null) {
            FluidStack fluid = tile.getTankInfo().fluid;
            return extendedState.withProperty(FLUID_LEVEL, fluid == null ? 0 : fluid.amount / TileBloodContainer.LEVEL_AMOUNT).withProperty(FLUID_NAME, fluid == null ? "" : fluid.getFluid().getName());

        }
        return extendedState.withProperty(FLUID_LEVEL, 0).withProperty(FLUID_NAME, "");
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        ItemStack stack = new ItemStack(ModBlocks.bloodContainer, 1);
        FluidStack fluid = ((TileBloodContainer) te).getTankInfo().fluid;
        if (fluid != null && fluid.amount > 0) {
            stack.setTagInfo("fluid", fluid.writeToNBT(new NBTTagCompound()));
        }
        spawnAsEntity(worldIn, pos, stack);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof IFluidContainerItem || FluidContainerRegistry.isContainer(stack)) {
                    TileBloodContainer bloodContainer = (TileBloodContainer) worldIn.getTileEntity(pos);
                    if (FluidContainerRegistry.isEmptyContainer(stack) || (stack.getItem() instanceof IFluidContainerItem && playerIn.isSneaking())) {
                        fillContainerFromTank(worldIn, pos, playerIn, stack, bloodContainer);
                    } else {
                        drainContainerIntoTank(worldIn, pos, playerIn, stack, bloodContainer);
                    }
                    worldIn.markBlockForUpdate(pos);
                    bloodContainer.markDirty();
                    return true;
                }
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
    protected BlockState createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{FLUID_LEVEL, FLUID_NAME});
    }

    private void drainContainerIntoTank(World worldIn, BlockPos pos, EntityPlayer playerIn, ItemStack stack, TileBloodContainer bloodContainer) {
        FluidTankInfo tankInfo = bloodContainer.getTankInfo();
        if (bloodContainer.isFull()) return;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem containerItem = (IFluidContainerItem) stack.getItem();
            FluidStack containerFluid = containerItem.getFluid(stack);
            FluidStack tankFluid = tankInfo.fluid;
            if (tankFluid == null || tankFluid.isFluidEqual(containerFluid)) {
                int drainAmount = Math.min(tankInfo.capacity - (tankFluid == null ? 0 : tankFluid.amount), containerFluid.amount);
                FluidStack drained = containerItem.drain(stack, drainAmount, true);
                bloodContainer.fill(null, drained, true);
            }
        } else {
            FluidStack containerFluid = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (bloodContainer.fill(null, containerFluid, true) > 0 && !playerIn.capabilities.isCreativeMode) {
                ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(stack);

                if (--stack.stackSize <= 0) {
                    playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
                }


                if (!playerIn.inventory.addItemStackToInventory(emptyContainer)) {
                    worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D, emptyContainer));
                } else if (playerIn instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                }
            }
        }
    }

    private void fillContainerFromTank(World worldIn, BlockPos pos, EntityPlayer playerIn, ItemStack stack, TileBloodContainer bloodContainer) {
        FluidTankInfo tankInfo = bloodContainer.getTankInfo();
        if (tankInfo.fluid == null) return;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem containerItem = (IFluidContainerItem) stack.getItem();
            int filled = containerItem.fill(stack, tankInfo.fluid, true);
            bloodContainer.drain(null, filled, true);
        } else {
            ItemStack filledStack = FluidContainerRegistry.fillFluidContainer(tankInfo.fluid, stack);
            if (filledStack != null) {
                int capacity = FluidContainerRegistry.getContainerCapacity(tankInfo.fluid, stack);
                if (capacity > 0) {
                    FluidStack drained = bloodContainer.drain(null, capacity, true);
                    if (drained != null && drained.amount == capacity) {
                        if (--stack.stackSize <= 0) {
                            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
                        }


                        if (!playerIn.inventory.addItemStackToInventory(filledStack)) {
                            worldIn.spawnEntityInWorld(new EntityItem(worldIn, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D, filledStack));
                        } else if (playerIn instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                        }
                    }
                }
            }
        }
    }

}
