package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.tileentity.PedestalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class PedestalTESR extends VampirismTESR<PedestalTileEntity> {
    public PedestalTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PedestalTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        ItemStack stack = te.getStackForRender();
        if (!stack.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.8, 0.5);
            float rotation = (te.getTickForRender() % 512 + partialTicks) / 512f;
            matrixStack.rotate(Vector3f.YP.rotationDegrees(rotation * 360));
            RenderHelper.enableStandardItemLighting();
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, i, i1, matrixStack, iRenderTypeBuffer);
            matrixStack.pop();
        }

    }
}
