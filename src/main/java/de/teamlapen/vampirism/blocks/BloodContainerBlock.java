package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer {
    protected static final VoxelShape containerShape = Block.box(2, 0, 2, 14, 16, 14);
    private final static Logger LOGGER = LogManager.getLogger();

    public static FluidStack getFluidFromItemStack(ItemStack stack) {
        if (ModBlocks.BLOOD_CONTAINER.get().asItem().equals(stack.getItem())) {
            if (stack.hasTag() && stack.getTag().contains("fluid", 10)) {
                CompoundTag fluidTag = stack.getTag().getCompound("fluid");
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
            stack.addTagElement("fluid", fluid.writeToNBT(new CompoundTag()));
        }
    }

    public BloodContainerBlock() {
        super(Properties.of(Material.GLASS).strength(1f).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("fluid")) {
            CompoundTag nbt = stack.getTag().getCompound("fluid");
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                tooltip.add(Component.translatable(fluid.getTranslationKey()).append(Component.literal(": " + fluid.getAmount() + "mB")).withStyle(ChatFormatting.DARK_RED));
            }

        }
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        ItemStack stack = new ItemStack(this, 1);
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), BloodContainerBlockEntity.CAPACITY);
        stack.addTagElement("fluid", fluid.writeToNBT(new CompoundTag()));
        items.add(stack);
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new BloodContainerBlockEntity(pos, state);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return containerShape;
    }

    @Override
    public void playerDestroy(@Nonnull Level worldIn, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity te, @Nonnull ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModBlocks.BLOOD_CONTAINER.get(), 1);
        if (te != null) {
            FluidStack fluid = ((BloodContainerBlockEntity) te).getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() >= VReference.FOOD_TO_FLUID_BLOOD) {
                writeFluidToItemStack(stack, fluid);
            }
        }
        popResource(worldIn, pos, stack);
    }

    @Override
    public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        FluidStack fluid = getFluidFromItemStack(stack);
        if (!stack.isEmpty()) {
            BlockEntity tile = (worldIn.getBlockEntity(pos));
            if (tile instanceof BloodContainerBlockEntity) {
                ((BloodContainerBlockEntity) tile).setFluidStack(fluid);
                tile.setChanged();

            }
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Player playerIn, @Nonnull InteractionHand hand, BlockHitResult hit) {
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
        return InteractionResult.SUCCESS;
    }
}
