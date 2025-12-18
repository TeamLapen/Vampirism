package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.world.items.components.IBottleBlood;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.util.BloodHelper;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.world.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.world.items.BaseDisplayItemGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BloodContainerBlock extends BaseEntityBlock implements BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final MapCodec<BloodContainerBlock> CODEC = simpleCodec(BloodContainerBlock::new);

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public BloodContainerBlock(Properties properties) {
        super(properties);
    }

    public static FluidStack getFluidFromItemStack(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOOD_CONTAINER.get(), SimpleFluidContent.EMPTY).copy();
    }

    public static void writeFluidToItemStack(ItemStack stack, FluidStack fluid) {
        if (fluid.isEmpty()) {
            stack.remove(ModDataComponents.BLOOD_CONTAINER);
        } else {
            stack.set(ModDataComponents.BLOOD_CONTAINER, SimpleFluidContent.copyOf(fluid));
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodContainerBlockEntity(pos, state);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        ItemStack stack = new ItemStack(ModBlocks.BLOOD_CONTAINER.get(), 1);
        if (blockEntity instanceof BloodContainerBlockEntity bloodContainerEntity) {
            FluidStack fluid = bloodContainerEntity.getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() >= IBottleBlood.MULTIPLIER) {
                stack.set(ModDataComponents.BLOOD_CONTAINER, SimpleFluidContent.copyOf(fluid));
            }
        }
        popResource(level, pos, stack);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof BloodContainerBlockEntity blockEntity) {
                blockEntity.setFluid(getFluidFromItemStack(stack));
                blockEntity.setChanged();
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return BloodHelper.handleFluidBlockInteraction(stack, level, pos, player, hand, hitResult.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return ItemDataUtils.createBloodContainer(level.getBlockEntity(pos) instanceof BloodContainerBlockEntity blockEntity ? blockEntity.getFluid().getAmount() : 0);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = new ItemStack(this, 1);
        output.accept(stack);
        stack = stack.copy();
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), BloodContainerBlockEntity.CAPACITY);
        stack.set(ModDataComponents.BLOOD_CONTAINER, SimpleFluidContent.copyOf(fluid));
        output.accept(stack);
    }

//    @Override FIXME readd
//    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
//        FluidStack fluidStack = getFluidFromItemStack(stack);
//        if (!fluidStack.isEmpty()) {
//            tooltipComponents.add(Component.translatable(fluidStack.getFluidType().getDescriptionId(fluidStack.copy())).append(Component.literal(": " + fluidStack.getAmount() + "mB")).withStyle(ChatFormatting.DARK_RED));
//        }
//    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    /**
     * @return 0-14
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return Optional.ofNullable(level.getCapability(Capabilities.Fluid.BLOCK, pos, null)).map(ResourceHandlerUtil::getRedstoneSignalFromResourceHandler).orElse(0);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
