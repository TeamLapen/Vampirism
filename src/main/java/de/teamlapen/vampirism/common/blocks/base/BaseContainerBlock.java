package de.teamlapen.vampirism.common.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vampirism default block container with additional helpful methods
 */
public abstract class BaseContainerBlock extends BaseEntityBlock {

    public BaseContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if ((state.hasBlockEntity() && !state.is(newState.getBlock())) || !newState.hasBlockEntity()) {
            this.clearContainer(state, level, pos);
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    protected void clearContainer(BlockState state, Level level, BlockPos pos) {
    }

    /**
     * drop all items from the tileentity's inventory if {@code instanceof} {@link Container}
     */
    protected void dropInventoryTileEntityItems(Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (!(tileEntity instanceof Container inventory)) {
            return;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (!item.isEmpty()) {
                dropItem(level, pos, item);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    protected void dropItem(Level level, BlockPos pos, ItemStack stack) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
