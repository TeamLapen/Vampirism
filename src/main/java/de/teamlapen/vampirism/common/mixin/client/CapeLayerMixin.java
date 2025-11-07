package de.teamlapen.vampirism.common.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void hideCapeWhenWearingCloak(PoseStack p_434174_, SubmitNodeCollector p_434543_, int p_432874_, AvatarRenderState p_445735_, float p_433069_, float p_435707_, CallbackInfo ci) {
        if (p_445735_.chestEquipment.is(ModItemTags.DISABLES_CAPE)) {
            ci.cancel();
        }
    }
}
