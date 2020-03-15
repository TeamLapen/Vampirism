package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterModel<T extends LivingEntity> extends BipedCloakedModel<T> {
    private boolean targetingLeft = false;
    private boolean targetingRight = false;
    private float xAngle = 0;

    public BasicHunterModel() {
        super(0.0F, 0.0F, 64, 64);
        this.bipedHeadwear.showModel = false;
    }

    @Override
    public void setLivingAnimations(T entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        this.targetingRight = false;
        this.targetingLeft = false;
        ItemStack itemStack = entitylivingbaseIn.getHeldItem(Hand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof VampirismItemCrossbow && entitylivingbaseIn instanceof BasicHunterEntity && ((BasicHunterEntity) entitylivingbaseIn).isSwingingArms()) {
            if (entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT) {
                this.targetingRight = true;
            } else {
                this.targetingLeft = true;
            }
            xAngle = -((BasicHunterEntity) entitylivingbaseIn).getTargetAngle() - (float) Math.PI / 3;
        }

        super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);

    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (targetingRight) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = xAngle;
            this.bipedLeftArm.rotateAngleX = xAngle / 2F;
        } else if (targetingLeft) {
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = xAngle / 2F;
            this.bipedLeftArm.rotateAngleX = xAngle;
        }
    }
}
