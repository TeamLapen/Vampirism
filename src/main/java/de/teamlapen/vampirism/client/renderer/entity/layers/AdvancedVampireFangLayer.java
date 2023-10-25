package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Render the eyes over the advanced vampire custom face
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireFangLayer extends RenderLayer<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> {

    private final ResourceLocation @NotNull [] overlays;

    public AdvancedVampireFangLayer(@NotNull RenderLayerParent<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> renderer) {
        super(renderer);
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/fangs" + (i) + ".png");
        }
    }


    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int packetLightIn, @NotNull AdvancedVampireEntity advancedVampireEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        int type = advancedVampireEntity.getFangType();
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }
        VertexConsumer builder = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(overlays[type]));
        boolean showModel = this.getParentModel().head.visible;
        this.getParentModel().head.visible = true;
        this.getParentModel().getHead().render(matrixStack, builder, packetLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getParentModel().head.visible = showModel;

    }
}
