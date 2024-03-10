package de.teamlapen.vampirism.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {

    @Invoker("applyItemArmTransform")
    void invokeApplyItemArmTransform(PoseStack pPoseStack, HumanoidArm pHand, float pEquippedProg);

    @Invoker("applyItemArmAttackTransform")
    void invokeApplyItemArmAttackTransform(PoseStack pPoseStack, HumanoidArm pHand, float pSwingProgress);
}
