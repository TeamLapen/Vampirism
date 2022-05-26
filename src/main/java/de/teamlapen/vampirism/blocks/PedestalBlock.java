package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.PedestalBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PedestalBlock extends VampirismBlockContainer {
    private static final VoxelShape pedestalShape = makeShape();

    private static void takeItemPlayer(Player player, InteractionHand hand, ItemStack stack) {
        player.setItemInHand(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSword) {
            if (((VampirismVampireSword) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSword) stack.getItem()).tryName(stack, player);
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

    public PedestalBlock() {
        super(Properties.of(Material.STONE).strength(3f).noOcclusion());
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return pedestalShape;
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        PedestalBlockEntity tile = getTileEntity(world, pos);
        if (tile == null) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() && !tile.extractItem(0, 1, true).isEmpty()) {
            ItemStack stack2 = tile.extractItem(0, 1, false);
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.blood_pedestal.get(), level.isClientSide() ? PedestalBlockEntity::clientTick : PedestalBlockEntity::serverTick);
    }
}
