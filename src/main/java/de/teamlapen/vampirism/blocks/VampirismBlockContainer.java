package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends BaseEntityBlock {


    public VampirismBlockContainer(String regName, Block.Properties properties) {
        super(properties);
        setRegistryName(REFERENCE.MODID, regName);

    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            this.clearContainer(state, worldIn, pos);
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    protected void clearContainer(BlockState state, Level worldIn, BlockPos pos) {

    }

    /**
     * drop all items from the tileentity's inventory if {@code instanceof} {@link IInventory}
     */
    protected void dropInventoryTileEntityItems(Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof Container)) {
            return;
        }
        Container inventory = (Container) tileEntity;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (!item.isEmpty()) {
                dropItem(world, pos, item);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    protected void dropItem(Level world, BlockPos pos, ItemStack stack) {
        net.minecraft.world.Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }


}
