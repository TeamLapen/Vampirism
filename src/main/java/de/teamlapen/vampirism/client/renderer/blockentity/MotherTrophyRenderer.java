package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.client.renderer.entities.GhostRenderer;
import de.teamlapen.vampirism.common.blockentity.MotherTrophyBlockEntity;
import de.teamlapen.vampirism.common.blocks.MotherTrophyBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class MotherTrophyRenderer implements BlockEntityRenderer<MotherTrophyBlockEntity> {

    private final GhostModel model;

    public MotherTrophyRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new GhostModel(context.bakeLayer(ModEntitiesRender.GHOST));
    }

    @Override
    public void render(MotherTrophyBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0.0F, -1.701F, 0.0F);
        float rotation = RotationSegment.convertToDegrees(blockEntity.getBlockState().getValue(MotherTrophyBlock.ROTATION));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        this.model.setupAnim2(blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0);
        poseStack.translate(0,0.75,0);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(GhostRenderer.TEXTURE)), packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}
