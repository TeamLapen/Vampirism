package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.tileentity.PedestalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;

public class PedestalTESR extends VampirismTESR<PedestalTileEntity> {
    public PedestalTESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(PedestalTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        ItemStack stack = te.getStackForRender();
        if (!stack.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.8, 0.5);
            float rotation = (te.getTickForRender() % 512 + partialTicks) / 512f;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation * 360));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, i, i1, matrixStack, iRenderTypeBuffer, 0);
            matrixStack.popPose();
        }

    }
}
