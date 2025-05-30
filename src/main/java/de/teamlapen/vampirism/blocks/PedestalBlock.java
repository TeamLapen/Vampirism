package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.PedestalBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.items.VampireSwordItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class PedestalBlock extends VampirismBlockContainer {

    public static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(6, 2, 6, 10, 8, 10),
            Block.box(3, 8, 3, 13, 9, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public PedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static void takeItemPlayer(Player player, InteractionHand hand, ItemStack stack) {
        player.setItemInHand(hand, stack);
        if (stack.getItem() instanceof VampireSwordItem vampireSwordItem) {
            if (vampireSwordItem.isFullyCharged(stack)) {
                vampireSwordItem.tryName(stack, player);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return getTile(level, pos).filter(s -> player.getMainHandItem().isEmpty()).map(pedestal -> {
            ItemStack stack2 = pedestal.extractItem(0, 1, false);
            player.awardStat(ModStats.ITEMS_FILLED_ON_BLOOD_PEDESTAL.get());
            takeItemPlayer(player, InteractionHand.MAIN_HAND, stack2);
            return (InteractionResult) InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return getTile(level, pos).filter(s -> !s.hasStack()).map(pedestal -> {
            ItemStack stack2 = ItemStack.EMPTY;
            if (!pedestal.extractItem(0, 1, true).isEmpty()) {
                stack2 = pedestal.extractItem(0, 1, false);
            }
            if (pedestal.insertItem(0, stack, false).isEmpty()) {
                if (!stack.isEmpty()) takeItemPlayer(player, hand, stack2);
            } else {
                pedestal.insertItem(0, stack2, false);
            }
            return (InteractionResult)InteractionResult.SUCCESS;
        }).orElse(InteractionResult.TRY_WITH_EMPTY_HAND);
    }

    @Override
    protected void clearContainer(BlockState state, Level level, BlockPos pos) {
        PedestalBlockEntity tile = getTileEntity(level, pos);
        if (tile != null && tile.hasStack()) {
            dropItem(level, pos, tile.removeStack());
        }
    }

    @Nullable
    private PedestalBlockEntity getTileEntity(BlockGetter level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof PedestalBlockEntity pedestalBlockEntity) {
            return pedestalBlockEntity;
        }
        return null;
    }

    private Optional<PedestalBlockEntity> getTile(BlockGetter level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof PedestalBlockEntity pedestalBlockEntity) {
            return Optional.of(pedestalBlockEntity);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.BLOOD_PEDESTAL.get(), level.isClientSide() ? PedestalBlockEntity::clientTick : PedestalBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        PedestalBlockEntity tile = getTileEntity(level, pos);
        if (tile != null) {
            return tile.getChargedProgress();
        }
        return 0;
    }
}
