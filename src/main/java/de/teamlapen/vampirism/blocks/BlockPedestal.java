package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.tileentity.TilePedestal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockPedestal extends VampirismBlockContainer {

    public final static String regName = "blood_pedestal";

    private static void takeItemPlayer(EntityPlayer player, EnumHand hand, ItemStack stack) {
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
    public int getHarvestLevel(IBlockState p_getHarvestLevel_1_) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving) {
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
