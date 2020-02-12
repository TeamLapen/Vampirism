package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
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
public class BasicHunterModel<T extends LivingEntity> extends BipedModel<T> {
    private ModelRenderer hatTop, hatRim, axeShaft, axeBlade1, axeBlade2, stake, stakeRight, secondHead, hatTop2, hatRim2, hatRim3;
    private boolean targetingLeft = false;
    private boolean targetingRight = false;
    private float xAngle = 0;

    public BasicHunterModel() {
        super(0.0F, 0.0F, 64, 64);
        this.bipedHeadwear.showModel = false;

        hatTop2 = new ModelRenderer(this, 0, 31);
        hatTop2.addBox(-4F, -12F, -4F, 8, 3, 8);
        hatTop2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop2.setTextureSize(128, 64);
        hatTop2.mirror = true;

        hatRim2 = new ModelRenderer(this, 0, 31);
        hatRim2.addBox(-8F, -9F, -8F, 16, 1, 16);
        hatRim2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim2.setTextureSize(128, 64);
        hatRim2.mirror = true;

        hatRim3 = new ModelRenderer(this, 0, 37);
        hatRim3.addBox(-5F, -6F, -5F, 10, 1, 10);
        hatRim3.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim3.setTextureSize(128, 64);
        hatRim3.mirror = true;

        hatTop = new ModelRenderer(this, 0, 31);
        hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
        hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop.setTextureSize(128, 64);
        hatTop.mirror = true;

        hatRim = new ModelRenderer(this, 0, 35);
        hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
        hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim.setTextureSize(128, 64);
        hatRim.mirror = true;

        axeShaft = new ModelRenderer(this, 16, 48);
        axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
        axeShaft.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeShaft.setTextureSize(128, 64);
        axeShaft.mirror = true;

        axeBlade1 = new ModelRenderer(this, 0, 53);
        axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
        axeBlade1.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade1.setTextureSize(128, 64);
        axeBlade1.mirror = true;

        axeBlade2 = new ModelRenderer(this, 0, 53);
        axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
        axeBlade2.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade2.setTextureSize(128, 64);
        axeBlade2.mirror = true;

        stake = new ModelRenderer(this, 16, 48);
        stake.addBox(1F, 8F, -8F, 1, 1, 6);
        stake.setRotationPoint(super.bipedLeftArm.rotationPointX, super.bipedLeftArm.rotationPointY, super.bipedLeftArm.rotationPointZ);
        stake.setTextureSize(128, 64);
        stake.mirror = true;

        stakeRight = new ModelRenderer(this, 16, 48);
        stakeRight.addBox(-2F, 8F, -8, 1, 1, 6);
        stakeRight.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        stakeRight.setTextureSize(128, 64);
        stakeRight.mirror = true;

        secondHead = new ModelRenderer(this, 0, 0);
        secondHead.setTextureSize(64, 32);
        secondHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        secondHead.setRotationPoint(0.0F, 0.0F + 0.0F, 0.0F);

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
    public void setRotationAngles(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.setRotationAngles(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
        hatRim.copyModelAngles(this.bipedHead);
        hatTop.copyModelAngles(this.bipedHead);
        hatRim2.copyModelAngles(this.bipedHead);
        hatTop2.copyModelAngles(this.bipedHead);
        hatRim3.copyModelAngles(this.bipedHead);

        axeShaft.copyModelAngles(this.bipedRightArm);
        axeBlade1.copyModelAngles(this.bipedRightArm);
        axeBlade2.copyModelAngles(this.bipedRightArm);
        stake.copyModelAngles(this.bipedLeftArm);
        stakeRight.copyModelAngles(this.bipedRightArm);
        secondHead.copyModelAngles(this.bipedHead);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.axeBlade1, this.axeBlade2, this.axeShaft, this.stake, this.stakeRight));
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return Iterables.concat(super.getHeadParts(), ImmutableList.of(this.hatRim, this.hatRim2, this.hatRim3, this.hatTop, this.hatTop2, this.secondHead));
    }
}
