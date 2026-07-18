package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaModel;
import de.teamlapen.vampirism.client.renderer.entities.DraculaRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;

public class DraculaItemInHandLayer extends ItemInHandLayer<DraculaRenderer.DraculaRenderState, DraculaModel> {

    public DraculaItemInHandLayer(RenderLayerParent<DraculaRenderer.DraculaRenderState, DraculaModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int p_433450_, DraculaRenderer.DraculaRenderState p_434546_, float p_433047_, float p_433527_) {
        if (getParentModel().hasArms()) {
            super.submit(poseStack, nodeCollector, p_433450_, p_434546_, p_433047_, p_433527_);
        }
    }

    /**
     * Copied from ItemInHandLayer
     */
    @Override
    protected void submitArmWithItem(DraculaRenderer.DraculaRenderState p_433403_, ItemStackRenderState p_434808_, ItemStack p_454825_, HumanoidArm p_433781_, PoseStack p_435302_, SubmitNodeCollector p_435985_, int p_434421_) {
        if (!p_434808_.isEmpty()) {
            p_435302_.pushPose();
            ((ArmedModel)this.getParentModel()).translateToHand(p_433403_, p_433781_, p_435302_);
            p_435302_.mulPose(Axis.XP.rotationDegrees(-90.0F));
            p_435302_.mulPose(Axis.YP.rotationDegrees(180.0F));
            boolean flag = p_433781_ == HumanoidArm.LEFT;
            p_435302_.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            if (p_433403_.attackTime > 0.0F && p_433403_.mainArm == p_433781_ && p_433403_.swingAnimationType == SwingAnimationType.STAB) {
                SpearAnimations.thirdPersonAttackItem(p_433403_, p_435302_);
            }

            float f = p_433403_.ticksUsingItem(p_433781_);
            if (f != 0.0F) {
                (p_433781_ == HumanoidArm.RIGHT ? p_433403_.rightArmPose : p_433403_.leftArmPose).animateUseItem(p_433403_, p_435302_, f, p_433781_, p_454825_);
            }

            //--------------added line----------------------
            p_435302_.scale(2,2,2);
            //----------------------------------------------
            p_434808_.submit(p_435302_, p_435985_, p_434421_, OverlayTexture.NO_OVERLAY, p_433403_.outlineColor);
            p_435302_.popPose();
        }

    }
}
