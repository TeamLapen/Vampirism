package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;

public class HunterEquipmentModel<T extends MobEntity> extends BipedModel<T> {
    private ModelRenderer hatTop, hatRim, axeShaft, axeBlade1, axeBlade2, stake, stakeRight, hatTop2, hatRim2, hatRim3;

    public HunterEquipmentModel() {
        super(0, 0, 64, 64);
        hatTop2 = new ModelRenderer(this, 0, 31);
        hatTop2.addBox(-4F, -12F, -4F, 8, 3, 8);
        hatTop2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop2.mirror = true;

        hatRim2 = new ModelRenderer(this, 0, 31);
        hatRim2.addBox(-8F, -9F, -8F, 16, 1, 16);
        hatRim2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim2.mirror = true;

        hatRim3 = new ModelRenderer(this, 0, 37);
        hatRim3.addBox(-5F, -6F, -5F, 10, 1, 10);
        hatRim3.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim3.mirror = true;

        hatTop = new ModelRenderer(this, 0, 31);
        hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
        hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop.mirror = true;

        hatRim = new ModelRenderer(this, 0, 35);
        hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
        hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim.mirror = true;

        axeShaft = new ModelRenderer(this, 16, 48);
        axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
        axeShaft.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeShaft.mirror = true;

        axeBlade1 = new ModelRenderer(this, 0, 53);
        axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
        axeBlade1.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade1.mirror = true;

        axeBlade2 = new ModelRenderer(this, 0, 53);
        axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
        axeBlade2.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade2.mirror = true;

        stake = new ModelRenderer(this, 16, 48);
        stake.addBox(1F, 8F, -8F, 1, 1, 6);
        stake.setRotationPoint(super.bipedLeftArm.rotationPointX, super.bipedLeftArm.rotationPointY, super.bipedLeftArm.rotationPointZ);
        stake.mirror = true;

        stakeRight = new ModelRenderer(this, 16, 48);
        stakeRight.addBox(-2F, 8F, -8, 1, 1, 6);
        stakeRight.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        stakeRight.mirror = true;

        super.setVisible(false);
    }

    public void setHat(int hatType) {
        hatRim.showModel = hatTop.showModel = hatType <= 0;
        hatTop2.showModel = hatRim2.showModel = hatType == 1;
        hatRim3.showModel = hatType >= 2;

    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    }

    public void setWeapons(StakeType type) {
        stakeRight.showModel = type == StakeType.ONLY;
        stake.showModel = axeBlade1.showModel = axeBlade2.showModel = axeShaft.showModel = type == StakeType.FULL;
    }


    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.axeBlade1, this.axeBlade2, this.axeShaft, this.stake, this.stakeRight));
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return Iterables.concat(super.getHeadParts(), ImmutableList.of(this.hatRim, this.hatRim2, this.hatRim3, this.hatTop, this.hatTop2));
    }


    public enum StakeType {
        NONE, ONLY, FULL
    }
}
