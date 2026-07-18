package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.renderer.entities.ConvertedCreatureRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Render the vampire overlay for converted creatures
 */
public class ConvertedVampireEntityLayer<Z extends LivingEntityRenderState, U extends EntityModel<Z>> extends RenderLayer<Z, U> {

    @Nullable
    public Identifier overlay;

    public ConvertedVampireEntityLayer(RenderLayerParent<Z, U> entityRendererIn) {
        this(entityRendererIn, null);
    }
    public ConvertedVampireEntityLayer(RenderLayerParent<Z, U> entityRendererIn, @Nullable Identifier overlay) {
        super(entityRendererIn);
        this.overlay = overlay;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, Z renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            var overlay = this.overlay;
            if (overlay == null) {
                this.overlay = overlay = renderState.entityType.builtInRegistryHolder().key().identifier().withPrefix("textures/entity/overlay/").withSuffix(".png");
            }
            renderColoredCutoutModel(this.getParentModel(), overlay, poseStack, nodeCollector, packedLight, renderState, -1, 1);
        }
    }
}
