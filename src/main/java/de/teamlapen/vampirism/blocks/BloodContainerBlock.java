package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.component.ContainedFluid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer implements ModDisplayItemGenerator.CreativeTabItemProvider {
    protected static final VoxelShape containerShape = Block.box(2, 0, 2, 14, 16, 14);
    public static final MapCodec<BloodContainerBlock> CODEC = simpleCodec(BloodContainerBlock::new);
    private final static Logger LOGGER = LogManager.getLogger();

    public static FluidStack getFluidFromItemStack(@NotNull ItemStack stack) {
        return ContainedFluid.get(stack);
    }

    public static void writeFluidToItemStack(@NotNull ItemStack stack, @NotNull FluidStack fluid) {
        if (fluid.isEmpty()) {
            stack.remove(ModDataComponents.BLOOD_CONTAINER);
        } else {
            stack.set(ModDataComponents.BLOOD_CONTAINER, new ContainedFluid(fluid));
        }
    }

    public BloodContainerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        FluidStack fluidStack = ContainedFluid.get(stack);
        if (!fluidStack.isEmpty()) {
            tooltip.add(Component.translatable(fluidStack.getFluidType().getDescriptionId(fluidStack)).append(Component.literal(": " + fluidStack.getAmount() + "mB")).withStyle(ChatFormatting.DARK_RED));
        }
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = new ItemStack(this, 1);
        output.accept(stack);
        stack = stack.copy();
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), BloodContainerBlockEntity.CAPACITY);
        stack.set(ModDataComponents.BLOOD_CONTAINER, new ContainedFluid(fluid));
        output.accept(stack);
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BloodContainerBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return containerShape;
    }

    @Override
    public void playerDestroy(@NotNull Level worldIn, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack heldStack) {
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
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
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

    @NotNull
    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player playerIn, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getDirection()) && stack.getItem().equals(Items.GLASS_BOTTLE) && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
            FluidUtil.getFluidHandler(worldIn, pos, hit.getDirection()).ifPresent((fluidHandler -> {
                if (fluidHandler.getFluidInTank(0).getFluid().equals(ModFluids.BLOOD.get())) {
                    ItemStack glass = playerIn.getItemInHand(hand);
                    ItemStack bloodBottle = FluidUtil.tryFillContainer(new ItemStack(ModItems.BLOOD_BOTTLE.get(), 1), fluidHandler, Integer.MAX_VALUE, playerIn, true).getResult();
                    if (bloodBottle.isEmpty()) {
                        playerIn.displayClientMessage(Component.translatable("text.vampirism.container.not_enough_blood"), true);
                    } else {
                        if (glass.getCount() > 1) {
                            glass.shrink(1);
                            playerIn.setItemInHand(hand, glass);
                            playerIn.addItem(bloodBottle);
                        } else {
                            playerIn.setItemInHand(hand, bloodBottle);
                        }
                    }
                }
            }));
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState pState) {
        return true;
    }

    /**
     * @return 0-14
     */
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        return FluidUtil.getFluidHandler(pLevel, pPos, null).map(handler -> (int) ((handler.getFluidInTank(0).getAmount() * 14f) / (float) handler.getTankCapacity(0))).orElse(0);
    }
}
