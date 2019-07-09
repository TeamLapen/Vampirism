package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.tileentity.TilePedestal;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockPedestal extends VampirismBlockContainer {

    public final static String regName = "blood_pedestal";

    private static void takeItemPlayer(PlayerEntity player, Hand hand, ItemStack stack) {
        player.setHeldItem(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSword) {
            if (((VampirismVampireSword) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSword) stack.getItem()).tryName(stack, player);
            }
        }
    }

    public BlockPedestal() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(3f));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TilePedestal();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        return face == Direction.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TilePedestal tile = getTileEntity(world, pos);
        if (tile == null) return false;
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty() && !tile.extractItem(0, 1, true).isEmpty()) {
            ItemStack stack2 = tile.extractItem(0, 1, false);
            takeItemPlayer(player, hand, stack2);
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
        }
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isRemote && state.getBlock() != newState.getBlock()) {
            TilePedestal tile = getTileEntity(world, pos);
            if (tile != null && tile.hasStack()) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tile.removeStack());
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Nullable
    private TilePedestal getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePedestal) {
            return (TilePedestal) tile;
        }
        return null;
    }
}
