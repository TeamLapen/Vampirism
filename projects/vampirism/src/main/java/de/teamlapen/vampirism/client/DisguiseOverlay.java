package de.teamlapen.vampirism.client;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayerSpecialAttribute;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DisguiseOverlay extends TextureOverlay {

    public static final ResourceLocation DISGUISE_TEXTURE = FResourceLocation.mod("textures/misc/disguise.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.CLIENT.enableDisguiseOverlayRendering.get()) {
            HunterPlayerSpecialAttribute huntSpecial = HunterPlayer.get(this.player()).getSpecialAttributes();
            if (huntSpecial.isDisguised()) {
                scaleBy(huntSpecial.getDisguiseProgress(), 1 / 4f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, DISGUISE_TEXTURE, 1.0F);
            }
        }
    }
}