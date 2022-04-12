package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.items.CoffinBlockItem;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CoffinItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {

    private final CoffinTileEntity coffin;

    public CoffinItemStackTileEntityRenderer(DyeColor color) {
        coffin = new CoffinTileEntity(true, color);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        Item item = itemStack.getItem();
        if (item instanceof CoffinBlockItem) {
            TileEntityRendererDispatcher.instance.renderItem(this.coffin, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        } else {
            super.renderByItem(itemStack, transformType, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        }
    }
}
