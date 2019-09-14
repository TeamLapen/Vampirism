package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class VampirismItemStackTESR extends ItemStackTileEntityRenderer {

    private final CoffinTileEntity coffin = new CoffinTileEntity(true);

    @Override
    public void renderByItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CoffinBlock) {
            TileEntityRendererDispatcher.instance.renderAsItem(this.coffin);
        } else {
            super.renderByItem(itemStack);
        }
    }
}
