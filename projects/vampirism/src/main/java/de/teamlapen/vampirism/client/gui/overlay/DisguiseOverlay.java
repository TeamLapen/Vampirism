package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterSkillProperties;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class DisguiseOverlay extends TextureOverlay {

    public static final Identifier DISGUISE_TEXTURE = VIdentifier.mod("textures/gui/overlay/disguise.png");

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.client().showDisguiseHUDOverlay.get()) {
            HunterSkillProperties huntSpecial = HunterPlayer.get(this.player()).getSpecialAttributes();
            if (huntSpecial.isConcealed()) {
                graphics.pose().pushMatrix();
                scaleBy(huntSpecial.getConcealmentProgress(), 1 / 4f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, DISGUISE_TEXTURE, 1.0F);
                graphics.pose().popMatrix();
            }
        }
    }
}