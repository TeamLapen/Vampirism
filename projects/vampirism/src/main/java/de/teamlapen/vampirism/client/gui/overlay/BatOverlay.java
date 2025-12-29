package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.factions.api.factions.actions.IActionHandler;
import de.teamlapen.factions.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class BatOverlay extends TextureOverlay {

    public static final Identifier BAT_TEXTURE = VResourceLocation.mod("textures/misc/bat.png");

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.client().enableHudBatOverlayRendering.get()) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get(this.player()).getActionHandler();
            if (actionHandler.isActionActive(VampireActions.BAT)) {
                renderTextureOverlay(graphics, BAT_TEXTURE, 1.0F);
            }
        }
    }
}