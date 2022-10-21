package de.teamlapen.vampirism.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V", shift = At.Shift.AFTER))
    private void test(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_109607_, CallbackInfo ci) {
        VampirismMod.proxy.endBloodVisionBatch();
    }
}
