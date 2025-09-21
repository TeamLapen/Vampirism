package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.client.models.entities.BipedCloakedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


public class CloakLayer<T extends PlayerRenderState, Q extends BipedCloakedModel<T>> extends RenderLayer<T, Q> {

    private final ResourceLocation textureCloak;
    private final Predicate<T> renderPredicate;

    public CloakLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, ResourceLocation texture, Predicate<T> predicate) {
        super(entityRendererIn);
        this.textureCloak = texture;
        this.renderPredicate = predicate;
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLightIn, T state, float p_117353_, float p_117354_) {
        if (!state.isInvisible && renderPredicate.test(state)) {
            stack.pushPose();
            VertexConsumer ivertexbuilder = bufferSource.getBuffer(RenderType.entitySolid(textureCloak));
            this.getParentModel().renderCustomCloak(stack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }
}
