package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.world.blockentity.PedestalBlockEntity;
import de.teamlapen.faction.common.world.blocks.base.BaseContainerBlock;
import de.teamlapen.vampirism.common.world.items.VampireSwordItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class PedestalBlock extends BaseContainerBlock {

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
        try (var transaction = Transaction.openRoot()) {
            var resource = Optional.ofNullable(level.getCapability(Capabilities.Item.BLOCK, pos, hitResult.getDirection())).map(handler -> {
                var item = ResourceHandlerUtil.extractFirst(handler, x -> true, 1, transaction);
                if (item == null || item.isEmpty()) {
                    return null;
                }
                return item.resource().toStack(item.amount());
            }).orElse(ItemStack.EMPTY);
            if (resource.getItem() instanceof VampireSwordItem vampireSwordItem) {
                if (vampireSwordItem.isFullyCharged(resource)) {
                    vampireSwordItem.tryName(resource, player);
                    player.awardStat(ModStats.ITEMS_FILLED_ON_BLOOD_PEDESTAL.get());
                }
            }

            if (resource.isEmpty()) {
                return InteractionResult.PASS;
            }

            ItemResource itemResource = ItemResource.of(resource);
            PlayerInventoryWrapper playerInventoryWrapper = PlayerInventoryWrapper.of(player);
            int insert = playerInventoryWrapper.getHandSlot(InteractionHand.MAIN_HAND).insert(itemResource, 1, transaction);
            if (insert == 0) {
                playerInventoryWrapper.drop(itemResource, 1, true, false, transaction);
            }
            transaction.commit();
            player.awardStat(ModStats.INTERACT_WITH_BLOOD_PEDESTAL.get());
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        try (var transaction = Transaction.openRoot()) {
            var handler = level.getCapability(Capabilities.Item.BLOCK, pos, null);
            if (handler == null) return InteractionResult.PASS;

            if (!ResourceHandlerUtil.isEmpty(handler)) return InteractionResult.TRY_WITH_EMPTY_HAND;

            var count = ResourceHandlerUtil.move(PlayerInventoryWrapper.of(player).getHandSlot(InteractionHand.MAIN_HAND), handler, x -> x.getItem() instanceof IBloodChargeable, 1, transaction);

            if (count > 0) {
                transaction.commit();
                player.awardStat(ModStats.INTERACT_WITH_BLOOD_PEDESTAL.get());
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
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
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        PedestalBlockEntity tile = getTileEntity(level, pos);
        if (tile != null) {
            return tile.getChargedProgress();
        }
        return 0;
    }
}
