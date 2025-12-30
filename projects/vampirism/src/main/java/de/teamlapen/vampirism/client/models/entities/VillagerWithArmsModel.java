package de.teamlapen.vampirism.client.models.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

/**
 * Villager Model with usable arms
 */
public class VillagerWithArmsModel extends VillagerModel implements ArmedModel {
    private static final String RIGHT_ARM = "right_arm";
    private static final String LEFT_ARM = "left_arm";

    private final @NotNull ModelPart leftArm;
    private final @NotNull ModelPart rightArm;

    public static @NotNull LayerDefinition createLayer(float scale) {
        MeshDefinition mesh = VillagerModel.createBodyModel();
        PartDefinition root = mesh.getRoot();
        CubeDeformation def = new CubeDeformation(scale);
        root.addOrReplaceChild(RIGHT_ARM, CubeListBuilder.create().texOffs(44, 22).addBox(-4F, -2F, -2F, 4, 8, 4, def).addBox(-4, 6, -2, 4, 3, 4, def), PartPose.offset(0, 2, 0));
        root.addOrReplaceChild(LEFT_ARM, CubeListBuilder.create().texOffs(44, 22).mirror().addBox(0, -2, -2, 4, 8, 4, def).addBox(0, 6, -2, 4, 3, 4, def), PartPose.offset(-5, 2, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public VillagerWithArmsModel(@NotNull ModelPart part) {
        super(part);
        this.leftArm = part.getChild(LEFT_ARM);
        this.rightArm = part.getChild(RIGHT_ARM);

    }


    @Override
    public void setupAnim(@NotNull VillagerRenderState entityIn) {
        super.setupAnim(entityIn);
        this.leftArm.setPos(4, 3, -1);
        this.rightArm.setPos(-4, 3, -1);
        this.leftArm.xRot = -0.75F;
        this.rightArm.xRot = -0.75F;

        float attackTime = entityIn.getRenderDataOrDefault(ModEntityRenderStates.ATTACK_TIME, 0f);
        if (attackTime > 0.0F) {
            HumanoidArm enumhandside = entityIn.getRenderDataOrDefault(ModEntityRenderStates.ATTACK_ARM, HumanoidArm.RIGHT);
            ModelPart modelrenderer = this.getArmForSide(enumhandside);
            float f1;
            f1 = 1.0F - attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = Mth.sin(f1 * (float) Math.PI);
            float f3 = Mth.sin(attackTime * (float) Math.PI) * -(this.getHead().xRot - 0.7F) * 0.75F;
            modelrenderer.xRot = (float) ((double) modelrenderer.xRot - ((double) f2 * 1.2D + (double) f3));
        }
    }

    @Override
    public void translateToHand(EntityRenderState renderState, HumanoidArm arm, PoseStack poseStack) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart part = getArmForSide(arm);
        part.x += f;
        part.translateAndRotate(poseStack);
        part.x -= f;
    }

    protected ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }
}
