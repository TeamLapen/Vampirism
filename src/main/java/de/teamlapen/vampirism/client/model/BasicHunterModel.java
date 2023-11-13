package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterModel<T extends LivingEntity> extends BipedCloakedModel<T> {

    public static @NotNull LayerDefinition createBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(false), 64, 64);
    }

    public static @NotNull LayerDefinition createSlimBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(true), 64, 64);
    }

    public BasicHunterModel(ModelPart part, boolean smallArms) {
        super(part, smallArms);
    }

    @Override
    public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        HumanoidModel.ArmPose mainPose = getArmPose(entityIn, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose offPose = getArmPose(entityIn, InteractionHand.OFF_HAND);
        if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = mainPose;
            this.leftArmPose = offPose;
        }
        if (entityIn.getMainArm() == HumanoidArm.LEFT) {
            this.rightArmPose = offPose;
            this.leftArmPose = mainPose;
        }
    }

    private static HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand pHand) {
        ItemStack itemstack = entity.getItemInHand(pHand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (entity.getUsedItemHand() == pHand && entity.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.CROSSBOW && pHand == entity.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!entity.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(entity, pHand, itemstack);
            if (forgeArmPose != null) return forgeArmPose;

            return HumanoidModel.ArmPose.ITEM;
        }
    }
}
