package de.teamlapen.vampirism.misc.injection.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.misc.extension.client.IItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;

public interface IItemInHandRendererMock extends IItemInHandRenderer {
    @Override
    default void invokeApplyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg) {

    }

    @Override
    default void invokeApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress) {

    }
}
