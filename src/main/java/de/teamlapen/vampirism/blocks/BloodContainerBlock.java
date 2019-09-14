package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer {

    public final static String regName = "blood_container";
    private final static Logger LOGGER = LogManager.getLogger();
    protected static final VoxelShape containerShape = Block.makeCuboidShape(2, 0, 2, 14, 16, 14);

    public BloodContainerBlock() {
        super(regName, Properties.create(Material.GLASS).hardnessAndResistance(1f));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BloodContainerTileEntity();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }


    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
        ItemStack stack = new ItemStack(this, 1);
        FluidStack fluid = new FluidStack(ModFluids.blood, BloodContainerTileEntity.CAPACITY);
        stack.setTagInfo("fluid", fluid.writeToNBT(new CompoundNBT()));
        items.add(stack);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.blood_container, 1);
        FluidStack fluid = ((BloodContainerTileEntity) te).getFluid();
        if (!fluid.isEmpty() && fluid.getAmount() > 0) {
            stack.setTagInfo("fluid", fluid.writeToNBT(new CompoundNBT()));
        }
        spawnAsEntity(worldIn, pos, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return containerShape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getFace());
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() && FluidLib.getFluidItemCap(stack).isPresent()) {
            BloodContainerTileEntity bloodContainer = (BloodContainerTileEntity) worldIn.getTileEntity(pos);
            IFluidHandler source = bloodContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElseThrow(() -> new IllegalStateException("Cannot get IFluidHandler from bloodContainer"));
            if (playerIn.isSneaking()) {
                FluidActionResult result = FluidUtil.tryFillContainer(stack, source, Integer.MAX_VALUE, playerIn, true);
                if (result.isSuccess()) {
                    playerIn.setHeldItem(hand, result.getResult());

                }
            } else {
                FluidActionResult result = tryEmptyContainer(stack, source, Integer.MAX_VALUE, playerIn, true);
                if (result.isSuccess()) {
                    playerIn.setHeldItem(hand, result.getResult());
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void onBlockClicked(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
        super.onBlockClicked(p_196270_1_, p_196270_2_, p_196270_3_, p_196270_4_);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            CompoundNBT nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid == null) {
                LOGGER.warn("Failed to load fluid from item nbt {}", nbt);
            } else {
                TileEntity tile = (worldIn.getTileEntity(pos));
                if (tile instanceof BloodContainerTileEntity) {
                    ((BloodContainerTileEntity) tile).setFluidStack(fluid);
                }
            }

        }
    }

    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    if (doDrain) {
                        FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                        if (!transfer.isEmpty()) {
                            if (player != null) {
                                SoundEvent soundevent = transfer.getFluid().getAttributes().getEmptySound(transfer);
                                player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new FluidActionResult(resultContainer);
                        }
                    } else {
                        FluidStack simulatedTransfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
                        if (!simulatedTransfer.isEmpty()) {
                            containerFluidHandler.drain(simulatedTransfer, IFluidHandler.FluidAction.SIMULATE);
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new FluidActionResult(resultContainer);
                        }
                    }
                    return FluidActionResult.FAILURE;
                })
                .orElse(FluidActionResult.FAILURE);
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@Nonnull ItemStack itemStack) {
        return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
    }

    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty()) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer) {
        int fillableAmount = fluidDestination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount > 0) {
            if (doTransfer) {
                FluidStack drained = fluidSource.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty()) {
                    drained.setAmount(fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
                    return drained;
                }
            } else {
                drainable.setAmount(fillableAmount);
                return drainable;
            }
        }
        return FluidStack.EMPTY;
    }
}
