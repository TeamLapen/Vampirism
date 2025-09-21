package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blockentity.BatCageBlockEntity;
import de.teamlapen.vampirism.common.blocks.BatCageBlock;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BatCageRenderer implements BlockEntityRenderer<BatCageBlockEntity> {

    public static final ResourceLocation BAT_LOCATION = VResourceLocation.mc("textures/entity/bat.png");

    private final BatModel model;

    public BatCageRenderer(BlockEntityRendererProvider.Context context) {
        model = new BatModel(context.bakeLayer(ModelLayers.BAT));
    }

    @Override
    public void render(BatCageBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();

        if (blockState.getValue(BatCageBlock.CONTAINS_BAT)) {
            renderBat(poseStack, bufferSource, packedLight, packedOverlay, blockState.getValue(BatCageBlock.FACING));
        }
    }

    private void renderBat(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Direction direction) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1F, 0.5F);
        //pPoseStack.mulPose(Axis.YN.rotationDegrees(90 * direction.get2DDataValue()));
        poseStack.scale(0.65F, 0.65F, 0.65F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        BatRenderState batRenderState = new BatRenderState();
        batRenderState.isResting = true;
        batRenderState.restAnimationState.animateWhen(true, 0);
        this.model.setupAnim(batRenderState);
        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(this.model.renderType(BAT_LOCATION)), packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}
