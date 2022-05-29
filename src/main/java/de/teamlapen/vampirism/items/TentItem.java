package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item used to place a tent
 */
public class TentItem extends Item {
    private static final String name = "item_tent";
    private static final String name_spawner = "item_tent_spawner";

    public static boolean placeAt(IWorld world, BlockPos pos, Direction dir, boolean force, boolean spawner) {
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
                    TileEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof TentTileEntity) {
                        ((TentTileEntity) tile).setSpawn(true);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAt(BlockState state, Block block, IWorld world, int x, int y, int z) {
        return block.canSurvive(state, world, new BlockPos(x, y, z));
    }

    private final boolean spawner;

    public TentItem(boolean spawner) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.spawner = spawner;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (spawner) {
            tooltip.add(new TranslationTextComponent("tile.vampirism.tent.spawner").withStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        if (ctx.getClickedFace() != Direction.UP)
            return ActionResultType.PASS;
        if (ctx.getLevel().isClientSide) return ActionResultType.PASS;

        ItemStack stack = ctx.getItemInHand();
        PlayerEntity player = ctx.getPlayer();

        Direction dir = player == null ? Direction.NORTH : Direction.fromYRot(ctx.getPlayer().yRot);
        boolean flag = placeAt(ctx.getLevel(), ctx.getClickedPos().above(), dir, false, false);
        if (flag) {
            TileEntity tile = ctx.getLevel().getBlockEntity(ctx.getClickedPos().above());
            if (tile instanceof TentTileEntity) {
                if (spawner) {
                    ((TentTileEntity) tile).setSpawn(true);
                }
            }

            if (player == null || !player.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        return flag ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }
}
