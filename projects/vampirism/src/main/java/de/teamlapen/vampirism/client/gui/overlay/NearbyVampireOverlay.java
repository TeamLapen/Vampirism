package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterSkillProperties;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class NearbyVampireOverlay extends TextureOverlay {

    public static final Identifier AWARENESS_TEXTURE = VIdentifier.mod("textures/gui/overlay/awareness.png");

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.client().showNearbyVampireOverlay.get()) {
            HunterSkillProperties huntSpecial = HunterPlayer.get(this.player()).getSpecialAttributes();
            if (huntSpecial.isVampireNearby()) {
                guiGraphics.pose().pushMatrix();
                scaleBy(huntSpecial.getVampireNearbyProgress(), 1 / 4f, 2F, 1.0F, guiGraphics);
                renderTextureOverlay(guiGraphics, AWARENESS_TEXTURE, 1.0F);
                guiGraphics.pose().popMatrix();
            }
        }
    }
}
