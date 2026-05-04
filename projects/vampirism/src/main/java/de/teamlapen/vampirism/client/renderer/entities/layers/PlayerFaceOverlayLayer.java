package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.faction.common.world.entities.IPlayerOverlay;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Renders an overlay over the entities face
 */
public class PlayerFaceOverlayLayer<T extends Mob & IPlayerOverlay, S extends HumanoidRenderState & IOverlayRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {


    private final M model;

    public PlayerFaceOverlayLayer(HumanoidMobRenderer<T, S, M> renderBiped, M model) {
        super(renderBiped);
        this.model = model;
    }


    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        Identifier overlay = renderState.overlay();
        if (overlay != null) {
            copyModelPartProperties(getParentModel().head, model.head);
            copyModelPartProperties(getParentModel().hat, model.hat);
            var renderType = RenderTypes.entityTranslucent(overlay);
            nodeCollector.submitModelPart(model.head, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, null);
            getParentModel().head.visible = false;
        } else {
            getParentModel().head.visible = true;
        }
    }

    protected static void copyModelPartProperties(ModelPart original, ModelPart replacement) {
        replacement.x = original.x;
        replacement.y = original.y;
        replacement.z = original.z;
        replacement.xRot = original.xRot;
        replacement.yRot = original.yRot;
        replacement.zRot = original.zRot;
        replacement.xScale = original.xScale;
        replacement.yScale = original.yScale;
        replacement.zScale = original.zScale;
    }
}
