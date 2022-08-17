package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Item used to place a tent
 */
public class TentItem extends Item {

    @SuppressWarnings("DuplicateExpressions")
    public static boolean placeAt(@NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull Direction dir, boolean force, boolean spawner) {
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

    private static boolean canPlaceAt(@NotNull BlockState state, @NotNull Block block, @NotNull LevelAccessor world, int x, int y, int z) {
        return block.canSurvive(state, world, new BlockPos(x, y, z));
    }

    private final boolean spawner;

    public TentItem(boolean spawner) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.spawner = spawner;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (spawner) {
            tooltip.add(Component.translatable("tile.vampirism.tent.spawner").withStyle(ChatFormatting.GRAY));
        }
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext ctx) {
        if (ctx.getClickedFace() != Direction.UP)
            return InteractionResult.PASS;
        if (ctx.getLevel().isClientSide) return InteractionResult.PASS;

        ItemStack stack = ctx.getItemInHand();
        Player player = ctx.getPlayer();

        Direction dir = player == null ? Direction.NORTH : Direction.fromYRot(ctx.getPlayer().getYRot());
        boolean flag = placeAt(ctx.getLevel(), ctx.getClickedPos().above(), dir, false, false);
        if (flag) {
            BlockEntity tile = ctx.getLevel().getBlockEntity(ctx.getClickedPos().above());
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
