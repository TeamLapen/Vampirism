package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;

public class HunterEquipmentModel<T extends MobEntity> extends BipedModel<T> {
    private final ModelRenderer hatTop;
    private final ModelRenderer hatRim;
    private final ModelRenderer axeShaft;
    private final ModelRenderer axeBlade1;
    private final ModelRenderer axeBlade2;
    private final ModelRenderer stake;
    private final ModelRenderer stakeRight;
    private final ModelRenderer hatTop2;
    private final ModelRenderer hatRim2;
    private final ModelRenderer hatRim3;

    public HunterEquipmentModel() {
        super(0, 0, 64, 64);
        hatTop2 = new ModelRenderer(this, 0, 31);
        hatTop2.addBox(-4.5F, -12F, -4.5F, 9, 3, 9);
        hatTop2.setPos(super.head.x, super.head.y, super.head.z);
        hatTop2.mirror = true;

        hatRim2 = new ModelRenderer(this, 0, 31);
        hatRim2.addBox(-8F, -9F, -8F, 16, 1, 16);
        hatRim2.setPos(super.head.x, super.head.y, super.head.z);
        hatRim2.mirror = true;

        hatRim3 = new ModelRenderer(this, 0, 37);
        hatRim3.addBox(-5F, -6F, -5F, 10, 1, 10);
        hatRim3.setPos(super.head.x, super.head.y, super.head.z);
        hatRim3.mirror = true;

        hatTop = new ModelRenderer(this, 0, 31);
        hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
        hatTop.setPos(super.head.x, super.head.y, super.head.z);
        hatTop.mirror = true;

        hatRim = new ModelRenderer(this, 0, 35);
        hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
        hatRim.setPos(super.head.x, super.head.y, super.head.z);
        hatRim.mirror = true;

        axeShaft = new ModelRenderer(this, 16, 48);
        axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
        axeShaft.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeShaft.mirror = true;

        axeBlade1 = new ModelRenderer(this, 0, 53);
        axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
        axeBlade1.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeBlade1.mirror = true;

        axeBlade2 = new ModelRenderer(this, 0, 53);
        axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
        axeBlade2.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeBlade2.mirror = true;

        stake = new ModelRenderer(this, 16, 48);
        stake.addBox(1F, 8F, -8F, 1, 1, 6);
        stake.setPos(super.leftArm.x, super.leftArm.y, super.leftArm.z);
        stake.mirror = true;

        stakeRight = new ModelRenderer(this, 16, 48);
        stakeRight.addBox(-2F, 8F, -8, 1, 1, 6);
        stakeRight.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        stakeRight.mirror = true;

        super.setAllVisible(false);
    }

    //TODO 1.17 maybe migrate hat type to enum or similar
    public void setHat(int hatType) {
        hatRim.visible = hatTop.visible = hatType <= 0 && hatType > -2;
        hatTop2.visible = hatRim2.visible = hatType == 1;
        hatRim3.visible = hatType >= 2;

    }

    public void setWeapons(StakeType type) {
        stakeRight.visible = type == StakeType.ONLY;
        boolean axe = type == StakeType.FULL || type == StakeType.AXE_ONLY;
        axeBlade1.visible = axeBlade2.visible = axeShaft.visible = axe;
        stake.visible = type == StakeType.FULL;
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        hatRim.copyFrom(this.head);
        hatTop.copyFrom(this.head);
        hatRim2.copyFrom(this.head);
        hatTop2.copyFrom(this.head);
        hatRim3.copyFrom(this.head);

        axeShaft.copyFrom(this.rightArm);
        axeBlade1.copyFrom(this.rightArm);
        axeBlade2.copyFrom(this.rightArm);
        stake.copyFrom(this.leftArm);
        stakeRight.copyFrom(this.rightArm);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.axeBlade1, this.axeBlade2, this.axeShaft, this.stake, this.stakeRight));
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return Iterables.concat(super.headParts(), ImmutableList.of(this.hatRim, this.hatRim2, this.hatRim3, this.hatTop, this.hatTop2));
    }


    public enum StakeType {
        NONE, ONLY, FULL, AXE_ONLY
    }
}
