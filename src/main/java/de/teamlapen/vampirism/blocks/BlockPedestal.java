package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.tileentity.TilePedestal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockPedestal extends VampirismBlockContainer {

    public final static String regName = "blood_pedestal";

    public BlockPedestal() {
        super(regName, Material.ROCK);
        this.setHardness(5.0F);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePedestal();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TilePedestal tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.hasStack()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.removeStack());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    private TilePedestal getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePedestal) {
            return (TilePedestal) tile;
        }
        return null;
    }

    private static void takeItemPlayer(EntityPlayer player, EnumHand hand, ItemStack stack) {
        player.setHeldItem(hand, stack);
        if (stack.getItem() instanceof VampirismVampireSword) {
            if (((VampirismVampireSword) stack.getItem()).isFullyCharged(stack)) {
                ((VampirismVampireSword) stack.getItem()).tryName(stack, player);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TilePedestal tile = getTileEntity(worldIn, pos);
        if (tile == null) return false;
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty() && tile.hasStack()) {
            ItemStack stack2 = tile.removeStack();
            takeItemPlayer(playerIn, hand, stack2);
            tile.markDirty();
        } else if (!stack.isEmpty()) {
            ItemStack stack2 = ItemStack.EMPTY;
            if (tile.hasStack()) {
                stack2 = tile.removeStack();
                tile.markDirty();
            }
            if (tile.setStack(stack)) {
                takeItemPlayer(playerIn, hand, stack2);
                tile.markDirty();
            } else {
                tile.setStack(stack2);
            }
        }
        return true;
    }


}
