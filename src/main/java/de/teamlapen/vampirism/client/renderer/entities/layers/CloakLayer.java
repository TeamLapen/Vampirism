package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.models.entities.BipedCloakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


public class CloakLayer<T extends AvatarRenderState, Q extends BipedCloakedModel<T>> extends RenderLayer<T, Q> {

    private final ResourceLocation textureCloak;
    private final Predicate<T> renderPredicate;

    public CloakLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, ResourceLocation texture, Predicate<T> predicate) {
        super(entityRendererIn);
        this.textureCloak = texture;
        this.renderPredicate = predicate;
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, int packedLight, @NotNull T renderState, float yRot, float xRot) {
        if (!renderState.isInvisible && renderPredicate.test(renderState)) {
            nodeCollector.submitModel(getParentModel(), renderState, poseStack, RenderType.entitySolid(textureCloak), packedLight, OverlayTexture.NO_OVERLAY, -1, null);
        }
    }
}
