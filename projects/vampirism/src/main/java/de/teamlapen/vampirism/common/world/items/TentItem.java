package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.TentBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * Item used to place a tent
 */
public class TentItem extends Item {

    private final boolean spawner;

    public TentItem(boolean spawner, Properties properties) {
        super(properties);
        this.spawner = spawner;
    }

    @SuppressWarnings("DuplicateExpressions")
    public static boolean placeAt(LevelAccessor world, BlockPos pos, Direction dir, boolean force, boolean spawner) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int x1 = x + (dir == Direction.SOUTH ? 1 : (dir == Direction.NORTH ? -1 : 0));
        int z1 = z + (dir == Direction.WEST ? 1 : (dir == Direction.EAST ? -1 : 0));
        int x2 = x + (dir == Direction.WEST ? -1 : (dir == Direction.NORTH ? -1 : 1));
        int z2 = z + (dir == Direction.SOUTH || dir == Direction.WEST ? 1 : -1);
        int x3 = x + (dir == Direction.WEST ? -1 : (dir == Direction.EAST ? 1 : 0));
        int z3 = z + (dir == Direction.SOUTH ? 1 : (dir == Direction.NORTH ? -1 : 0));

        Block tent = ModBlocks.TENT.get();
        Block main = ModBlocks.TENT_MAIN.get();
        BlockState mainState = main.defaultBlockState();
        if (force || canPlaceAt(mainState, tent, world, x, y, z) && canPlaceAt(mainState, tent, world, x1, y, z1) && canPlaceAt(mainState, tent, world, x2, y, z2) && canPlaceAt(mainState, tent, world, x3, y, z3)) {
            boolean flag = world.setBlock(pos, main.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()), 3);
            if (flag) {
                world.setBlock(new BlockPos(x1, y, z1), tent.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, 1), 3);
                world.setBlock(new BlockPos(x2, y, z2), tent.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, 2), 3);
                world.setBlock(new BlockPos(x3, y, z3), tent.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, 3), 3);
                if (spawner) {
                    BlockEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof TentBlockEntity) {
                        ((TentBlockEntity) tile).setSpawn(true);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAt(BlockState state, Block block, LevelAccessor world, int x, int y, int z) {
        return state.canSurvive(world, new BlockPos(x, y, z));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, flagIn);
        if (spawner) {
            tooltipComponents.accept(Component.translatable("tile.vampirism.tent.spawner").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide()) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        Direction dir = player == null ? Direction.NORTH : Direction.fromYRot(context.getPlayer().getYRot());
        boolean flag = placeAt(context.getLevel(), context.getClickedPos().above(), dir, false, false);
        if (flag) {
            BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos().above());
            if (tile instanceof TentBlockEntity) {
                if (spawner) {
                    ((TentBlockEntity) tile).setSpawn(true);
                }
            }

            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return flag ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
}
