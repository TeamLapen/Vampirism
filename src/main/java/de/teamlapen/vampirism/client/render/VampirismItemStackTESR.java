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
    public void render(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CoffinBlock) {
            TileEntityRendererDispatcher.instance.renderNullable(this.coffin, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        } else {
            super.render(itemStack, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        }
    }
}
