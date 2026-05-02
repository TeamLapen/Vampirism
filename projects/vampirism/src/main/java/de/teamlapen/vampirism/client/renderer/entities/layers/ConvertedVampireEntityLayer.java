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

/**
 * Render the vampire overlay for converted creatures
 */
public class ConvertedVampireEntityLayer<Z extends LivingEntityRenderState, U extends EntityModel<Z>> extends RenderLayer<Z, U> {

    public final boolean checkIfRender;

    /**
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public ConvertedVampireEntityLayer(RenderLayerParent<Z, U> entityRendererIn, boolean checkIfRender) {
        super(entityRendererIn);
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, Z renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            Identifier texture = renderState.getRenderData(ModEntityRenderStates.CONVERTED_OVERLAY);
            if (texture == null) {
                texture = renderState.getRenderData(ModEntityRenderStates.OVERLAY);
            }
            if (texture != null) {
                renderColoredCutoutModel(this.getParentModel(), texture, poseStack, nodeCollector, packedLight, renderState, -1, 1);
            }
        }
    }
}
