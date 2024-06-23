package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.blockentity.MotherTrophyBlockEntity;
import de.teamlapen.vampirism.blocks.MotherTrophyBlock;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.GhostModel;
import de.teamlapen.vampirism.client.renderer.entity.GhostRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.NotNull;

public class MotherTrophyBESR extends VampirismBESR<MotherTrophyBlockEntity> {

    private final GhostModel model;

    public MotherTrophyBESR(BlockEntityRendererProvider.Context context) {
        this.model = new GhostModel(context.bakeLayer(ModEntitiesRender.GHOST));
    }

    @Override
    public void render(@NotNull MotherTrophyBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Integer value = pBlockEntity.getBlockState().getValue(MotherTrophyBlock.ROTATION);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0, 0.5);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        pPoseStack.translate(0.0F, -1.701F, 0.0F);
        float f1 = RotationSegment.convertToDegrees(value);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f1));
        this.model.setupAnim2(pBlockEntity.getLevel() != null ? pBlockEntity.getLevel().getGameTime() : 0);
        this.model.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.itemEntityTranslucentCull(GhostRenderer.TEXTURE)), pPackedLight, pPackedOverlay, -1);
        pPoseStack.popPose();
    }
}
