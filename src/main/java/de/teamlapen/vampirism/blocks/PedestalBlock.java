package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.PedestalBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PedestalBlock extends VampirismBlockContainer {
    public static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);
    private static final VoxelShape pedestalShape = makeShape();

    private static void takeItemPlayer(Player player, InteractionHand hand, ItemStack stack) {
        player.setItemInHand(hand, stack);
        if (stack.getItem() instanceof VampireSwordItem) {
            if (((VampireSwordItem) stack.getItem()).isFullyCharged(stack)) {
                ((VampireSwordItem) stack.getItem()).tryName(stack, player);
            }
        }
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.box(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.box(5, 2, 5, 11, 3, 11);
        VoxelShape d = Block.box(6, 3, 6, 10, 7, 10);
        VoxelShape e = Block.box(5, 7, 5, 11, 8, 11);
        VoxelShape f = Block.box(3, 8, 3, 13, 9, 13);
        VoxelShape g1 = Block.box(4, 9, 4, 5, 11, 5);
        VoxelShape g2 = Block.box(11, 9, 4, 12, 11, 5);
        VoxelShape g3 = Block.box(4, 9, 11, 5, 11, 12);
        VoxelShape g4 = Block.box(11, 9, 11, 12, 11, 12);

        return Shapes.or(a, b, c, d, e, f, g1, g2, g3, g4);
    }

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
        return pedestalShape;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return getTile(world, pos).filter(s -> player.getMainHandItem().isEmpty()).map(pedestal -> {
            ItemStack stack2 = pedestal.extractItem(0, 1, false);
            player.awardStat(ModStats.ITEMS_FILLED_ON_BLOOD_PEDESTAL.get());
            takeItemPlayer(player, InteractionHand.MAIN_HAND, stack2);
            return (InteractionResult) InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return getTile(world, pos).filter(s -> !s.hasStack()).map(pedestal -> {
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
    protected void clearContainer(BlockState state, Level worldIn, BlockPos pos) {
        PedestalBlockEntity tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.hasStack()) {
            dropItem(worldIn, pos, tile.removeStack());
        }
    }

    @Nullable
    private PedestalBlockEntity getTileEntity(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PedestalBlockEntity) {
            return (PedestalBlockEntity) tile;
        }
        return null;
    }

    private Optional<PedestalBlockEntity> getTile(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PedestalBlockEntity) {
            return Optional.of((PedestalBlockEntity) tile);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.BLOOD_PEDESTAL.get(), level.isClientSide() ? PedestalBlockEntity::clientTick : PedestalBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        var tile = getTileEntity(pLevel, pPos);
        if (tile != null) {
            return tile.getChargedProgress();
        }
        return 0;
    }
}
