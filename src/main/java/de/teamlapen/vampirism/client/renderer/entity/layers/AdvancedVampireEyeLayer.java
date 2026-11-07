package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render the eyes over the advanced vampire custom face
 */
public class AdvancedVampireEyeLayer extends RenderLayer<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> {

    private static final int[] LEGACY_EYE_VERTICAL_OFFSETS = {
            0, 0, 0, 0, 1, 0, 1, -1, 1, -1, 2, 0, 1, 0, 0, 0, 0
    };

    private final ResourceLocation @NotNull [] overlays;

    public AdvancedVampireEyeLayer(@NotNull RenderLayerParent<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> renderer) {
        super(renderer);
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + REFERENCE.eyeTextureId(i) + ".png");
        }
    }


    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int packetLightIn, @NotNull AdvancedVampireEntity advancedVampireEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        int type = advancedVampireEntity.getEyeType();
        int eyeType = REFERENCE.mapLegacyEyeType(type);
        if (eyeType < 0) return;
        VertexConsumer builder = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(overlays[eyeType]));
        boolean showModel = this.getParentModel().head.visible;
        this.getParentModel().head.visible = true;
        matrixStack.pushPose();
        matrixStack.translate(0.0, LEGACY_EYE_VERTICAL_OFFSETS[type] / 16.0, 0.0);
        this.getParentModel().getHead().render(matrixStack, builder, packetLightIn, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        this.getParentModel().head.visible = showModel;

    }
}
