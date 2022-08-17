package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.teamlapen.vampirism.blockentity.PedestalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class PedestalBESR extends VampirismBESR<PedestalBlockEntity> {
    public PedestalBESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@NotNull PedestalBlockEntity te, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, int i1) {
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
