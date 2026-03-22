package de.teamlapen.vampirism.client;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterSkillProperties;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DisguiseOverlay extends TextureOverlay {

    public static final Identifier DISGUISE_TEXTURE = FIdentifier.mod("textures/misc/disguise.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.client().showDisguiseHUDOverlay.get()) {
            HunterSkillProperties huntSpecial = HunterPlayer.get(this.player()).getSpecialAttributes();
            if (huntSpecial.isConcealed()) {
                scaleBy(huntSpecial.getConcealmentProgress(), 1 / 4f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, DISGUISE_TEXTURE, 1.0F);
            }
        }
    }
}