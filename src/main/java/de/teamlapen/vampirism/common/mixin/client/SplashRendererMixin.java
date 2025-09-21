package de.teamlapen.vampirism.common.mixin.client;

import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public abstract class SplashRendererMixin {

    @Final
    @Shadow
    private String splash;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V", at = @At("HEAD"), cancellable = true)
    public void vampirism$render(GuiGraphics guiGraphics, int screenWidth, Font font, int color, CallbackInfo ci) {
        if ("MOTHER".equals(splash)) {
            Integer newColor = ChatFormatting.DARK_RED.getColor();
            if (newColor == null) return;

            // This part is original code, except for the color part
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)screenWidth / 2.0F + 123.0F, 69.0F, 0.0F);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
            float f = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
            f = f * 100.0F / (float)(font.width(this.splash) + 32);
            guiGraphics.pose().scale(f, f, f);
            guiGraphics.drawCenteredString(font, this.splash, 0, -8, newColor);
            guiGraphics.pose().popPose();

            ci.cancel();
        }
    }
}
