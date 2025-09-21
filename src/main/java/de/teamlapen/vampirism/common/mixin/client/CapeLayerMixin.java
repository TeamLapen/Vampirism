package de.teamlapen.vampirism.common.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void hideCapeWhenWearingCloak(PoseStack poseStack, MultiBufferSource bufferSource, int light, PlayerRenderState playerState, float limbSwing, float limbSwingAmount, CallbackInfo ci) {
        if (playerState.chestEquipment.is(ModItemTags.DISABLES_CAPE)) {
            ci.cancel();
        }
    }
}
