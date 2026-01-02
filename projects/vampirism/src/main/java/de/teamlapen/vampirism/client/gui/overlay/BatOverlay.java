package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class BatOverlay extends TextureOverlay {

    public static final Identifier BAT_TEXTURE = VIdentifier.mod("textures/gui/overlay/bat.png");

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