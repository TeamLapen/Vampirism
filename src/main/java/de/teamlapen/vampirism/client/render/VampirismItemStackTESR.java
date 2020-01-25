package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class VampirismItemStackTESR extends ItemStackTileEntityRenderer {

    private final CoffinTileEntity coffin = new CoffinTileEntity(true);

    @Override
    public void render(ItemStack itemStack, MatrixStack p_228364_2_, IRenderTypeBuffer p_228364_3_, int p_228364_4_, int p_228364_5_) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CoffinBlock) {
            TileEntityRendererDispatcher.instance.renderItem(this.coffin, p_228364_2_);
        } else {
            super.render(itemStack, p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_);
        }
    }
}
