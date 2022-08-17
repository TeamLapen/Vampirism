package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Mob;

import org.jetbrains.annotations.NotNull;

public class HunterEquipmentModel<T extends Mob> extends HumanoidModel<T> {

    private final static String HAT_TOP = "hat_top";
    private final static String HAT_RIM = "hat_rim";
    private final static String AXE_SHAFT = "axe_shaft";
    private final static String AXE_BLADE1 = "axe_blade1";
    private static final String AXE_BLADE2 = "axe_blade2";
    private static final String STAKE_LEFT = "stake_left";
    private static final String STAKE_RIGHT = "stake_right";
    private static final String HAT_TOP2 = "hat_top2";
    private static final String HAT_RIM2 = "hat_rim2";
    private static final String HAT_RIM3 = "hat_rim3";

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

    public static LayerDefinition createLayer() {
        float offset = 0f;
        PartPose headPose = PartPose.offset(0, offset, 0);
        PartPose rightArmPose = PartPose.offset(-5.0F, 2.0F + offset, 0.0F);
        PartPose leftArmPose = PartPose.offset(5.0F, 2.0F + offset, 0.0F);
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, offset);

        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild(HAT_TOP2, CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-4.5f, -12, -4.5f, 9, 3, 9), headPose);
        part.addOrReplaceChild(HAT_RIM2, CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-8F, -9F, -8F, 16, 1, 16), headPose);
        part.addOrReplaceChild(HAT_RIM3, CubeListBuilder.create().texOffs(0, 37).mirror().addBox(-5F, -6F, -5F, 10, 1, 10), headPose);
        part.addOrReplaceChild(HAT_TOP, CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-4F, -14F, -4F, 8, 5, 8), headPose);
        part.addOrReplaceChild(HAT_RIM, CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-6F, -9F, -6F, 12, 1, 12), headPose);
        part.addOrReplaceChild(AXE_SHAFT, CubeListBuilder.create().texOffs(16, 48).mirror().addBox(-2F, 8F, -17F, 1, 1, 15), rightArmPose);
        part.addOrReplaceChild(AXE_BLADE1, CubeListBuilder.create().texOffs(0, 53).mirror().addBox(-2F, 4F, -16F, 1, 4, 7), rightArmPose);
        part.addOrReplaceChild(AXE_BLADE2, CubeListBuilder.create().texOffs(0, 53).mirror().addBox(-2F, 9F, -16F, 1, 4, 7), rightArmPose);
        part.addOrReplaceChild(STAKE_LEFT, CubeListBuilder.create().texOffs(16, 48).mirror().addBox(1F, 8F, -8F, 1, 1, 6), leftArmPose);
        part.addOrReplaceChild(STAKE_RIGHT, CubeListBuilder.create().texOffs(16, 48).mirror().addBox(-2F, 8F, -8, 1, 1, 6), rightArmPose);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public HunterEquipmentModel(ModelPart part) {
        super(part);
        hatTop = part.getChild(HAT_TOP);
        hatTop2 = part.getChild(HAT_TOP2);
        hatRim = part.getChild(HAT_RIM);
        hatRim2 = part.getChild(HAT_RIM2);
        hatRim3 = part.getChild(HAT_RIM3);
        axeShaft = part.getChild(AXE_SHAFT);
        axeBlade1 = part.getChild(AXE_BLADE1);
        axeBlade2 = part.getChild(AXE_BLADE2);
        stake = part.getChild(STAKE_LEFT);
        stakeRight = part.getChild(STAKE_RIGHT);

        super.setAllVisible(false);
    }

    public void setHat(HatType hatType) {
        hatTop.visible = hatRim.visible = hatType == HatType.HAT1;
        hatTop2.visible = hatRim2.visible = hatType == HatType.HAT2;
        hatRim3.visible = hatType == HatType.RIM_ONLY;

    }

    public void setWeapons(StakeType type) {
        stakeRight.visible = type == StakeType.ONLY;
        boolean axe = type == StakeType.FULL || type == StakeType.AXE_ONLY;
        axeBlade1.visible = axeBlade2.visible = axeShaft.visible = axe;
        stake.visible = type == StakeType.FULL;
    }

    @Override
    public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

    @NotNull
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.axeBlade1, this.axeBlade2, this.axeShaft, this.stake, this.stakeRight));
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> headParts() {
        return Iterables.concat(super.headParts(), ImmutableList.of(this.hatRim, this.hatRim2, this.hatRim3, this.hatTop, this.hatTop2));
    }


    public enum StakeType {
        NONE, ONLY, FULL, AXE_ONLY
    }

    public enum HatType {
        NONE, RIM_ONLY /*>=2*/, HAT1 /*-1/0*/, HAT2 /*1*/;

        public static HatType from(int id) {
            return switch (id) {
                case 0 -> HAT1;
                case 1 -> HAT2;
                case 2 -> RIM_ONLY;
                default -> NONE;
            };
        }
    }
}
