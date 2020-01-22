package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.tileentity.PedestalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class PedestalTESR extends VampirismTESR<PedestalTileEntity> {
    public PedestalTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PedestalTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack stack = te.getStackForRender();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translated(x + 0.5, y + 0.7, z + 0.5);
            float rotation = (te.getTickForRender() % 512 + partialTicks) / 512f;
            GlStateManager.rotatef(rotation * 360f, 0, 1, 0);
            GlStateManager.pushLightingAttributes();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttributes();
            GlStateManager.popMatrix();
        }
    }
}
