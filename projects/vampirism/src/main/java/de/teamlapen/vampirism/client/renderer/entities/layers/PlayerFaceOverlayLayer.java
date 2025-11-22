package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.factions.common.entities.IPlayerOverlay;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Renders an overlay over the entities face
 */
public class PlayerFaceOverlayLayer<T extends Mob & IPlayerOverlay, S extends HumanoidRenderState & IOverlayRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {


    public PlayerFaceOverlayLayer(@NotNull HumanoidMobRenderer<T, S, M> renderBiped) {
        super(renderBiped);
    }


    @Override
    public void submit(@NotNull PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        var renderType = RenderType.entityCutoutNoCull(renderState.overlay());
        this.getParentModel().head.visible = true;
        this.getParentModel().hat.visible = true;
        nodeCollector.submitModelPart(getParentModel().head, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, null);
        nodeCollector.submitModelPart(getParentModel().hat, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, null);
        this.getParentModel().head.visible = false;
        this.getParentModel().hat.visible = false;
    }
}
