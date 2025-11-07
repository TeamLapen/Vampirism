package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render the vampire overlay
 */
public class VampireEntityLayer<T extends LivingEntityRenderState, U extends EntityModel<? super T>> extends RenderLayer<T, U> {

    private final ResourceLocation texture;

    public VampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, ResourceLocation texture) {
        super(entityRendererIn);
        this.texture = texture;
    }

    @Deprecated
    public VampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, ResourceLocation texture, boolean checkIfRender) {
        this(entityRendererIn, texture);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, T renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            renderColoredCutoutModel(this.getParentModel(), this.texture, poseStack, nodeCollector, packedLight, renderState, -1, -1);
        }
    }
}
