package de.teamlapen.vampirism.misc.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.common.util.HumanoidArmorLayerData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<S extends HumanoidRenderState> {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At(value = "HEAD"))
    private void catchRenderState(PoseStack p_435921_, SubmitNodeCollector p_434130_, int p_434678_, S p_435902_, float p_435802_, float p_434554_, CallbackInfo ci) {
        HumanoidArmorLayerData.setRenderState(p_435902_);
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V", at = @At(value = "RETURN"))
    private void clearRenderState(PoseStack par1, SubmitNodeCollector par2, int par3, EntityRenderState par4, float par5, float par6, CallbackInfo ci) {
        HumanoidArmorLayerData.reset();
    }
}
