package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer {

    public final static String regName = "blood_container";
    @ObjectHolder("vampirism:blood_container")
    public static final Item item = UtilLib.getNull();
    protected static final VoxelShape containerShape = Block.box(2, 0, 2, 14, 16, 14);
    private final static Logger LOGGER = LogManager.getLogger();

    public static FluidStack getFluidFromItemStack(ItemStack stack) {
        if (stack.getItem() == item) {
            if (stack.hasTag() && stack.getTag().contains("fluid", 10)) {
                CompoundTag fluidTag = stack.getTag().getCompound("fluid");
                return FluidStack.loadFluidStackFromNBT(fluidTag);
            } else {
                return new FluidStack(ModFluids.blood, 0);
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
            stack.addTagElement("fluid", fluid.writeToNBT(new CompoundTag()));
        }
    }

    public BloodContainerBlock() {
        super(regName, Properties.of(Material.GLASS).strength(1f).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            CompoundTag nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                tooltip.add(new TranslatableComponent(fluid.getTranslationKey()).append(new TextComponent(": " + fluid.getAmount() + "mB")).withStyle(ChatFormatting.DARK_RED));
            }

        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        ItemStack stack = new ItemStack(this, 1);
        FluidStack fluid = new FluidStack(ModFluids.blood, BloodContainerTileEntity.CAPACITY);
        stack.addTagElement("fluid", fluid.writeToNBT(new CompoundTag()));
        items.add(stack);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new BloodContainerTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return containerShape;
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.blood_container, 1);
        if (te != null) {
            FluidStack fluid = ((BloodContainerTileEntity) te).getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() >= VReference.FOOD_TO_FLUID_BLOOD) {
                writeFluidToItemStack(stack, fluid);
            }
        }
        popResource(worldIn, pos, stack);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        FluidStack fluid = getFluidFromItemStack(stack);
        if (!stack.isEmpty()) {
            BlockEntity tile = (worldIn.getBlockEntity(pos));
            if (tile instanceof BloodContainerTileEntity) {
                ((BloodContainerTileEntity) tile).setFluidStack(fluid);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
        if (!FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getDirection()) && playerIn.getItemInHand(hand).getItem().equals(Items.GLASS_BOTTLE) && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
            FluidUtil.getFluidHandler(worldIn, pos, hit.getDirection()).ifPresent((fluidHandler -> {
                if (fluidHandler.getFluidInTank(0).getFluid().equals(ModFluids.blood)) {
                    ItemStack glass = playerIn.getItemInHand(hand);
                    ItemStack bloodBottle = new ItemStack(ModItems.blood_bottle, 1);
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
        return InteractionResult.SUCCESS;
    }
}
