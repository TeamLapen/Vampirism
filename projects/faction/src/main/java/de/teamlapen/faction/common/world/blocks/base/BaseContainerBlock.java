package de.teamlapen.factions.common.world.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Vampirism default block container with additional helpful methods
 */
public abstract class BaseContainerBlock extends BaseEntityBlock {

    public BaseContainerBlock(Properties properties) {
        super(properties);
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
