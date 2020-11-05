package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends ContainerBlock {


    public VampirismBlockContainer(String regName, Block.Properties properties) {
        super(properties);
        setRegistryName(REFERENCE.MODID, regName);

    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity())) {
            this.clearContainer(state, worldIn, pos);
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    protected void clearContainer(BlockState state, World worldIn, BlockPos pos) {

    }

    /**
     * drop all items from the tileentity's inventory if {@code instanceof} {@link IInventory}
     */
    protected void dropInventoryTileEntityItems(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (!item.isEmpty()) {
                dropItem(world, pos, item);
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }

    protected void dropItem(World world, BlockPos pos, ItemStack stack) {
        net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }


}
