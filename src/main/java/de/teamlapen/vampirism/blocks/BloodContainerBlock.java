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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tileentity container that can store liquids.
 */
public class BloodContainerBlock extends VampirismBlockContainer implements ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final MapCodec<BloodContainerBlock> CODEC = simpleCodec(BloodContainerBlock::new);

    protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public BloodContainerBlock(Properties properties) {
        super(properties);
    }

    public static FluidStack getFluidFromItemStack(ItemStack stack) {
        return ContainedFluid.get(stack);
    }

    public static void writeFluidToItemStack(ItemStack stack, FluidStack fluid) {
        if (fluid.isEmpty()) {
            stack.remove(ModDataComponents.BLOOD_CONTAINER);
        } else {
            stack.set(ModDataComponents.BLOOD_CONTAINER, new ContainedFluid(fluid));
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodContainerBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack heldStack) {
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
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
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
        return InteractionResult.SUCCESS;
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = new ItemStack(this, 1);
        output.accept(stack);
        stack = stack.copy();
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), BloodContainerBlockEntity.CAPACITY);
        stack.set(ModDataComponents.BLOOD_CONTAINER, new ContainedFluid(fluid));
        output.accept(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        FluidStack fluidStack = ContainedFluid.get(stack);
        if (!fluidStack.isEmpty()) {
            tooltipComponents.add(Component.translatable(fluidStack.getFluidType().getDescriptionId(fluidStack)).append(Component.literal(": " + fluidStack.getAmount() + "mB")).withStyle(ChatFormatting.DARK_RED));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    /**
     * @return 0-14
     */
    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return FluidUtil.getFluidHandler(pLevel, pPos, null).map(handler -> (int) ((handler.getFluidInTank(0).getAmount() * 14f) / (float) handler.getTankCapacity(0))).orElse(0);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
