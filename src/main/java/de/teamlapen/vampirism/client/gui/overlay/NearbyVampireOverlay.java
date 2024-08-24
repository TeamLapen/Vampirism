package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NearbyVampireOverlay extends TextureOverlay {

    public static final ResourceLocation AWARENESS_TEXTURE = VResourceLocation.mod("textures/misc/awareness.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableNearbyVampireOverlayRendering.get()) {
            HunterPlayerSpecialAttribute huntSpecial = ((IVampirismPlayer) Minecraft.getInstance().player).getVampAtts().getHuntSpecial();
            if (huntSpecial.isVampireNearby()) {
                graphics.pose().pushPose();
                scaleBy(huntSpecial.getVampireNearbyProgress(), 1 / 4f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, AWARENESS_TEXTURE, 1.0F);
                graphics.pose().popPose();
            }
        }
    }
}
