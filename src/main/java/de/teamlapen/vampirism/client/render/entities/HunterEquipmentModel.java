package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;

public class HunterEquipmentModel<T extends Mob> extends HumanoidModel<T> {
    private final ModelPart hatTop;
    private final ModelPart hatRim;
    private final ModelPart axeShaft;
    private final ModelPart axeBlade1;
    private final ModelPart axeBlade2;
    private final ModelPart stake;
    private final ModelPart stakeRight;
    private final ModelPart hatTop2;
    private final ModelPart hatRim2;
    private final ModelPart hatRim3;

    public HunterEquipmentModel() {
        super(0, 0, 64, 64);
        hatTop2 = new ModelPart(this, 0, 31);
        hatTop2.addBox(-4.5F, -12F, -4.5F, 9, 3, 9);
        hatTop2.setPos(super.head.x, super.head.y, super.head.z);
        hatTop2.mirror = true;

        hatRim2 = new ModelPart(this, 0, 31);
        hatRim2.addBox(-8F, -9F, -8F, 16, 1, 16);
        hatRim2.setPos(super.head.x, super.head.y, super.head.z);
        hatRim2.mirror = true;

        hatRim3 = new ModelPart(this, 0, 37);
        hatRim3.addBox(-5F, -6F, -5F, 10, 1, 10);
        hatRim3.setPos(super.head.x, super.head.y, super.head.z);
        hatRim3.mirror = true;

        hatTop = new ModelPart(this, 0, 31);
        hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
        hatTop.setPos(super.head.x, super.head.y, super.head.z);
        hatTop.mirror = true;

        hatRim = new ModelPart(this, 0, 35);
        hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
        hatRim.setPos(super.head.x, super.head.y, super.head.z);
        hatRim.mirror = true;

        axeShaft = new ModelPart(this, 16, 48);
        axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
        axeShaft.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeShaft.mirror = true;

        axeBlade1 = new ModelPart(this, 0, 53);
        axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
        axeBlade1.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeBlade1.mirror = true;

        axeBlade2 = new ModelPart(this, 0, 53);
        axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
        axeBlade2.setPos(super.rightArm.x, super.rightArm.y, super.rightArm.z);
        axeBlade2.mirror = true;

        stake = new ModelPart(this, 16, 48);
        stake.addBox(1F, 8F, -8F, 1, 1, 6);
        stake.setPos(super.leftArm.x, super.leftArm.y, super.leftArm.z);
        stake.mirror = true;

        stakeRight = new ModelPart(this, 16, 48);
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
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.axeBlade1, this.axeBlade2, this.axeShaft, this.stake, this.stakeRight));
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return Iterables.concat(super.headParts(), ImmutableList.of(this.hatRim, this.hatRim2, this.hatRim3, this.hatTop, this.hatTop2));
    }


    public enum StakeType {
        NONE, ONLY, FULL, AXE_ONLY
    }
}
