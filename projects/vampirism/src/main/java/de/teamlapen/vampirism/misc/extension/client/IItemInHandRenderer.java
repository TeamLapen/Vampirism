package de.teamlapen.vampirism.misc.extension.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

public interface IItemInHandRenderer {
    void invokeApplyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg);

    void invokeApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress);
}
