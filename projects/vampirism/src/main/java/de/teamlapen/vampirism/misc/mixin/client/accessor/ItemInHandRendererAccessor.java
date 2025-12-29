package de.teamlapen.vampirism.misc.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.misc.extension.client.IItemInHandRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor extends IItemInHandRenderer {

    @Override
    @Invoker("applyItemArmTransform")
    void invokeApplyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg);

    @Override
    @Invoker("applyItemArmAttackTransform")
    void invokeApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress);
}
