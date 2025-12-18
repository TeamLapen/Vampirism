package de.teamlapen.vampirism.misc.injection.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.misc.extension.client.IItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;

@Deprecated
public interface IItemInHandRendererVampirismMock extends IItemInHandRenderer {
    @Override
    default void invokeApplyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void invokeApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
