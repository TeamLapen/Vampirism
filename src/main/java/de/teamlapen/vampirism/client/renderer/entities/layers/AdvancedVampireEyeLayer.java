package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.renderer.entities.AdvancedVampireRenderer;
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
public class AdvancedVampireEyeLayer extends RenderLayer<AdvancedVampireRenderer.AdvancedVampireRenderState, HumanoidModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {

    private final ResourceLocation @NotNull [] overlays;

    public AdvancedVampireEyeLayer(@NotNull RenderLayerParent<AdvancedVampireRenderer.AdvancedVampireRenderState, HumanoidModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> renderer) {
        super(renderer);
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + (i) + ".png");
        }
    }


    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int packetLightIn, @NotNull AdvancedVampireRenderer.AdvancedVampireRenderState advancedVampireEntity, float v, float v1) {
        int type = advancedVampireEntity.eyeType;
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }
        VertexConsumer builder = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(overlays[type]));
        boolean showModel = this.getParentModel().head.visible;
        this.getParentModel().head.visible = true;
        this.getParentModel().getHead().render(matrixStack, builder, packetLightIn, OverlayTexture.NO_OVERLAY);
        this.getParentModel().head.visible = showModel;

    }
}
