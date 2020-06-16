package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer {

    public final static String regName = "blood_container";
    protected static final VoxelShape containerShape = Block.makeCuboidShape(2, 0, 2, 14, 16, 14);
    private final static Logger LOGGER = LogManager.getLogger();

    public BloodContainerBlock() {
        super(regName, Properties.create(Material.GLASS).hardnessAndResistance(1f));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BloodContainerTileEntity();
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return containerShape;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.blood_container, 1);
        if (te != null) {
            FluidStack fluid = ((BloodContainerTileEntity) te).getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() >= VReference.FOOD_TO_FLUID_BLOOD) {
                stack.setTagInfo("fluid", fluid.writeToNBT(new CompoundNBT()));
            }
        }
        spawnAsEntity(worldIn, pos, stack);
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        if (!FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getFace()) && playerIn.getHeldItem(hand).getItem().equals(Items.GLASS_BOTTLE) && VampirismConfig.SERVER.autoConvertGlassBottles.get()) {
            FluidUtil.getFluidHandler(worldIn, pos, hit.getFace()).ifPresent((fluidHandler -> {
                if (fluidHandler.getFluidInTank(0).getFluid().equals(ModFluids.blood)) {
                    ItemStack glass = playerIn.getHeldItem(hand);
                    ItemStack bloodBottle = new ItemStack(ModItems.blood_bottle, 1);
                    playerIn.setHeldItem(hand, bloodBottle);
                    bloodBottle = FluidUtil.tryFillContainer(bloodBottle, fluidHandler, Integer.MAX_VALUE, playerIn, true).getResult();
                    if (glass.getCount() > 1) {
                        glass.shrink(1);
                        playerIn.setHeldItem(hand, glass);
                        playerIn.addItemStackToInventory(bloodBottle);
                    } else {
                        playerIn.setHeldItem(hand, bloodBottle);
                    }
                }
            }));
        }
        return ActionResultType.SUCCESS;
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

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            CompoundNBT nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                tooltip.add(new TranslationTextComponent(fluid.getTranslationKey()).appendText(": ").appendText(fluid.getAmount() + "mB").applyTextStyle(TextFormatting.DARK_RED));
            }

        }
    }
}
