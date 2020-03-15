package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.client.model.BipedCloakedModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;


public class CloakLayer<T extends MobEntity, Q extends BipedCloakedModel<T>> extends LayerRenderer<T, Q> {

    private final ResourceLocation textureCloak;
    private final Predicate<T> renderPredicate;

    public CloakLayer(IEntityRenderer<T, Q> entityRendererIn, ResourceLocation texture, Predicate<T> predicate) {
        super(entityRendererIn);
        this.textureCloak = texture;
        this.renderPredicate = predicate;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isInvisible() && renderPredicate.test(entitylivingbaseIn)) {
            matrixStackIn.push();
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entitySolid(textureCloak));
            this.getEntityModel().renderCloak(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }
    }
}
