package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Similar to {@link ItemInHandLayer} but for VillagerRenderState/ArmedModel
 * Requires {@link ModEntityRenderStates.ATTACK_ARM} to be set in RenderStateExtension event
 */
public class ItemInVillagerHandLayer<S extends VillagerRenderState, M extends EntityModel<S> & ArmedModel<VillagerRenderState>> extends RenderLayer<S, M> {
    public ItemInVillagerHandLayer(RenderLayerParent<S, M> p_234846_) {
        super(p_234846_);
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packetLight, S renderState, float yRot, float xRot) {
        HumanoidArm arm = renderState.getRenderData(ModEntityRenderStates.ATTACK_ARM);
        if (!renderState.heldItem.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().translateToHand(renderState, arm, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            boolean flag = arm == HumanoidArm.LEFT;
            poseStack.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            //Usually the correct item animations should be executed here (see {@link ItemInHandLayer}, but this would require more data to be stored in the render state extensions. Not really worth it here

            renderState.heldItem.submit(poseStack, nodeCollector, packetLight, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            poseStack.popPose();
        }
    }

}