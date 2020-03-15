package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the eyes over the advanced vampire custom face
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireEyeLayer extends LayerRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> {

    private final IEntityRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> renderer;

    private final ResourceLocation[] overlays;

    public AdvancedVampireEyeLayer(IEntityRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> renderer) {
        super(renderer);
        this.renderer = renderer;
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/eyes" + (i) + ".png");
        }
    }


    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int packetLightIn, AdvancedVampireEntity advancedVampireEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        int type = advancedVampireEntity.getEyeType();
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }
        IVertexBuilder builder = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(overlays[type]));
        boolean showModel = this.getEntityModel().bipedHead.showModel;
        this.getEntityModel().bipedHead.showModel = true;
        this.getEntityModel().getModelHead().render(matrixStack, builder, packetLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getEntityModel().bipedHead.showModel = showModel;

    }
}
