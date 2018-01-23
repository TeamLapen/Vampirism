package de.teamlapen.vampirism.client.render.tiles;

import de.teamlapen.vampirism.tileentity.TilePedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public class PedestalTESR extends VampirismTESR<TilePedestal> {

    @Override
    public void render(TilePedestal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = te.getStackForRender();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.7, z + 0.5);
            float rotation = (te.getTickForRender() % 256 + partialTicks) / 256f;
            GlStateManager.rotate(rotation * 360f, 0, 1, 0);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }
}
