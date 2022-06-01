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
import net.minecraft.util.text.StringTextComponent;
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
    protected static final VoxelShape containerShape = Block.box(2, 0, 2, 14, 16, 14);
    private final static Logger LOGGER = LogManager.getLogger();

    public static FluidStack getFluidFromItemStack(ItemStack stack) {
        if (stack.getItem() == ModBlocks.BLOOD_CONTAINER.get().asItem()) {
            if (stack.hasTag() && stack.getTag().contains("fluid", 10)) {
                CompoundNBT fluidTag = stack.getTag().getCompound("fluid");
                return FluidStack.loadFluidStackFromNBT(fluidTag);
            } else {
                return new FluidStack(ModFluids.BLOOD.get(), 0);
            }
        }
        return FluidStack.EMPTY;
    }

    public static void writeFluidToItemStack(ItemStack stack, FluidStack fluid) {
        if (fluid.isEmpty()) {
            if (stack.hasTag() && stack.getTag().contains("fluid")) {
                stack.getTag().remove("fluid");
            }
        } else {
            stack.addTagElement("fluid", fluid.writeToNBT(new CompoundNBT()));
        }
    }

    public BloodContainerBlock() {
        super(Properties.of(Material.GLASS).strength(1f).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            CompoundNBT nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                tooltip.add(new TranslationTextComponent(fluid.getTranslationKey()).append(new StringTextComponent(": " + fluid.getAmount() + "mB")).withStyle(TextFormatting.DARK_RED));
            }

        }
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        ItemStack stack = new ItemStack(this, 1);
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), BloodContainerTileEntity.CAPACITY);
        stack.addTagElement("fluid", fluid.writeToNBT(new CompoundNBT()));
        items.add(stack);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new BloodContainerTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return containerShape;
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.BLOOD_CONTAINER.get(), 1);
        if (te != null) {
            FluidStack fluid = ((BloodContainerTileEntity) te).getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() >= VReference.FOOD_TO_FLUID_BLOOD) {
                writeFluidToItemStack(stack, fluid);
            }
        }
        popResource(worldIn, pos, stack);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        FluidStack fluid = getFluidFromItemStack(stack);
        if (!stack.isEmpty()) {
            TileEntity tile = (worldIn.getBlockEntity(pos));
            if (tile instanceof BloodContainerTileEntity) {
                ((BloodContainerTileEntity) tile).setFluidStack(fluid);
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        if (!FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getDirection()) && playerIn.getItemInHand(hand).getItem().equals(Items.GLASS_BOTTLE) && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
            FluidUtil.getFluidHandler(worldIn, pos, hit.getDirection()).ifPresent((fluidHandler -> {
                if (fluidHandler.getFluidInTank(0).getFluid().equals(ModFluids.BLOOD.get())) {
                    ItemStack glass = playerIn.getItemInHand(hand);
                    ItemStack bloodBottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), 1);
                    playerIn.setItemInHand(hand, bloodBottle);
                    bloodBottle = FluidUtil.tryFillContainer(bloodBottle, fluidHandler, Integer.MAX_VALUE, playerIn, true).getResult();
                    if (glass.getCount() > 1) {
                        glass.shrink(1);
                        playerIn.setItemInHand(hand, glass);
                        playerIn.addItem(bloodBottle);
                    } else {
                        playerIn.setItemInHand(hand, bloodBottle);
                    }
                }
            }));
        }
        return ActionResultType.SUCCESS;
    }
}
