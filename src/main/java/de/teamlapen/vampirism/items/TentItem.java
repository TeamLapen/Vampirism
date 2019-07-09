package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileTent;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Item used to place a tent
 */
public class TentItem extends VampirismItem {
    private static final String name = "item_tent";
    private static final String name_spawner = "item_tent_spawner";

    private final boolean spawner;

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

        Block tent = ModBlocks.tent;
        Block main = ModBlocks.tent_main;
        BlockState mainState = main.getDefaultState();
        if (force || canPlaceAt(mainState, tent, world, x, y, z) && canPlaceAt(mainState, tent, world, x1, y, z1) && canPlaceAt(mainState, tent, world, x2, y, z2) && canPlaceAt(mainState, tent, world, x3, y, z3)) {
            boolean flag = world.setBlockState(pos, main.getDefaultState().with(TentBlock.FACING, dir.getOpposite()), 3);
            if (flag) {
                world.setBlockState(new BlockPos(x1, y, z1), tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, 1), 3);
                world.setBlockState(new BlockPos(x2, y, z2), tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, 2), 3);
                world.setBlockState(new BlockPos(x3, y, z3), tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, 3), 3);
                if (spawner) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileTent) {
                        ((TileTent) tile).setSpawn(true);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAt(BlockState state, Block block, IWorld world, int x, int y, int z) {
        return block.isValidPosition(state, world, new BlockPos(x, y, z));
    }

    public TentItem(boolean spawner) {
        super(spawner ? name : name_spawner, new Properties().group(VampirismMod.creativeTab));
        this.spawner = spawner;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (spawner) {
            tooltip.add(UtilLib.translated("tile.vampirism.tent.spawner"));
        }
    }



    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if (ctx.getFace() != Direction.UP)
            return ActionResultType.PASS;
        if (ctx.getWorld().isRemote) return ActionResultType.PASS;

        ItemStack stack = ctx.getItem();
        PlayerEntity player = ctx.getPlayer();

        Direction dir = player == null ? Direction.NORTH : Direction.fromAngle(ctx.getPlayer().rotationYaw);
        boolean flag = placeAt(ctx.getWorld(), ctx.getPos().up(), dir, false, false);
        if (flag) {
            TileEntity tile = ctx.getWorld().getTileEntity(ctx.getPos().up());
            if (tile instanceof TileTent) {
                if (spawner) {
                    ((TileTent) tile).setSpawn(true);
                }
            }

            if (player != null || !player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return flag ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }
}
