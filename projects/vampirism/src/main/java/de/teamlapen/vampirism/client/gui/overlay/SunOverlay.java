package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.factions.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

public class SunOverlay extends TextureOverlay {

    public static final ResourceLocation SUN_TEXTURE = VResourceLocation.mod("textures/misc/sun.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.CLIENT.enableSunOverlayRendering.get()) {
            AbstractClientPlayer player = this.player();
            VampirePlayer vampire = VampirePlayer.get(player);
            MobEffectInstance effect = player.getEffect(ModEffects.SUNSCREEN);
            float progress = Math.clamp(vampire.getTicksInSun() / 50f, 0f, 1f);
            if (progress > 0 && (effect == null || effect.getAmplifier() < 5)) {
                graphics.pose().pushMatrix();
                if (player.getAbilities().instabuild || (effect != null && effect.getAmplifier() >= 3)) {
                    progress = Math.min(0.5f, progress);
                }
                scaleBy(progress, 1 / 5f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, SUN_TEXTURE, 1.0F);
                graphics.pose().popMatrix();
            }
        }
    }

}