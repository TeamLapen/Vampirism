package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.PedestalBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.VampirismVampireSwordItem;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends VampirismBlockContainer {
    public static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);
    private static final VoxelShape pedestalShape = makeShape();

    private static void takeItemPlayer(@NotNull Player player, @NotNull InteractionHand hand, @NotNull ItemStack stack) {
        player.setItemInHand(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSwordItem) {
            if (((VampirismVampireSwordItem) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSwordItem) stack.getItem()).tryName(stack, player);
            }
        }
    }

    private static @NotNull VoxelShape makeShape() {
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

    public PedestalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return pedestalShape;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        PedestalBlockEntity tile = getTileEntity(world, pos);
        if (tile == null) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() && !tile.extractItem(0, 1, true).isEmpty()) {
            ItemStack stack2 = tile.extractItem(0, 1, false);
            player.awardStat(ModStats.items_filled_on_blood_pedestal);
            takeItemPlayer(player, hand, stack2);
            return InteractionResult.SUCCESS;

        } else if (!stack.isEmpty()) {
            ItemStack stack2 = ItemStack.EMPTY;
            if (!tile.extractItem(0, 1, true).isEmpty()) {
                stack2 = tile.extractItem(0, 1, false);
            }
            if (tile.insertItem(0, stack, false).isEmpty()) {
                if (!stack.isEmpty()) takeItemPlayer(player, hand, stack2);
            } else {
                tile.insertItem(0, stack2, false);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    protected void clearContainer(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos) {
        PedestalBlockEntity tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.hasStack()) {
            dropItem(worldIn, pos, tile.removeStack());
        }
    }

    @Nullable
    private PedestalBlockEntity getTileEntity(@NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PedestalBlockEntity) {
            return (PedestalBlockEntity) tile;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.BLOOD_PEDESTAL.get(), level.isClientSide() ? PedestalBlockEntity::clientTick : PedestalBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        var tile = getTileEntity(pLevel, pPos);
        if (tile != null) {
            return tile.getChargedProgress();
        }
        return 0;
    }
}
