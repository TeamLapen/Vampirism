package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.common.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FogDiffuserRenderer implements BlockEntityRenderer<FogDiffuserBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack motherCore;

    public FogDiffuserRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.motherCore = ModItems.MOTHER_CORE.get().getDefaultInstance();
    }

    @Override
    public void render(FogDiffuserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3, 0.5);
        if (blockEntity.getLevel() != null) poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getLevel().getGameTime() + partialTick));
        this.itemRenderer.renderStatic(motherCore, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}
