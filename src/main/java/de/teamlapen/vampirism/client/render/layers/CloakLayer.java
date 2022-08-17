package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.client.model.BipedCloakedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import org.jetbrains.annotations.NotNull;
import java.util.function.Predicate;


public class CloakLayer<T extends Mob, Q extends BipedCloakedModel<T>> extends RenderLayer<T, Q> {

    private final ResourceLocation textureCloak;
    private final Predicate<T> renderPredicate;

    public CloakLayer(RenderLayerParent<T, Q> entityRendererIn, ResourceLocation texture, Predicate<T> predicate) {
        super(entityRendererIn);
        this.textureCloak = texture;
        this.renderPredicate = predicate;
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isInvisible() && renderPredicate.test(entitylivingbaseIn)) {
            matrixStackIn.pushPose();
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entitySolid(textureCloak));
            this.getParentModel().renderCustomCloak(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.popPose();
        }
    }
}
