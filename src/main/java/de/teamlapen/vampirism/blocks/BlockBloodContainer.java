package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tileentity container that can store liquids.
 */
public class BlockBloodContainer extends VampirismBlockContainer {

    public final static String regName = "blood_container";
    private final static Logger LOGGER = LogManager.getLogger();

    public BlockBloodContainer() {
        super(regName, Properties.create(Material.GLASS).hardnessAndResistance(1f));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileBloodContainer();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }


    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
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
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, side);//TODO 1.13 check
            /*
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

             */
        }

        return true;

    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            NBTTagCompound nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid == null) {
                LOGGER.warn("Failed to load fluid from item nbt {}", nbt);
            } else {
                TileEntity tile = (worldIn.getTileEntity(pos));
                if (tile instanceof TileBloodContainer) {
                    ((TileBloodContainer) tile).setFluidStack(fluid);
                }
            }

        }
    }



}
