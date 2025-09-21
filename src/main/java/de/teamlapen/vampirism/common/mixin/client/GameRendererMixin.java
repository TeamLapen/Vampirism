package de.teamlapen.vampirism.common.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
//
//    @Shadow @Final private Minecraft minecraft;
//
//    @Shadow @Final private CrossFrameResourcePool resourcePool;
//
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V", shift = At.Shift.BEFORE))
//    public void s(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
//        if (((IVampirismPlayer) Minecraft.getInstance().player).getVampAtts().getVampSpecial().blood_vision) {
//            var chain =this.minecraft.getShaderManager().getPostChain(VResourceLocation.mc("blur"), LevelTargetBundle.MAIN_TARGETS);
//            chain.setUniform("Radius", 0.6f);
//            chain.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
//
//            VampirismModClient.getINSTANCE().getRenderHandler().endBloodVisionBatch();
//        }
//    }
}
