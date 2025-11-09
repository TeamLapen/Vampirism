package de.teamlapen.vampirism.misc.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;
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

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void vampirism$render(GuiGraphics guiGraphics, int width, Font font, float fade, CallbackInfo ci) {
        if ("MOTHER".equals(splash)) {
            Integer newColor = ChatFormatting.DARK_RED.getColor();
            if (newColor == null) return;

            // This part is original code, except for the color part
            Matrix3x2fStack pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate((float) width / 2.0F + 123.0F, 69.0F);
            pose.rotate(-20);
            float f = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
            f = f * 100.0F / (float)(font.width(this.splash) + 32);
            pose.scale(f, f);
            guiGraphics.drawCenteredString(font, this.splash, 0, -8, newColor);
            pose.popMatrix();

            ci.cancel();
        }
    }
}
