package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class RageOverlay extends TextureOverlay {

    public static final Identifier RAGE_TEXTURE = VIdentifier.mod("textures/misc/rage.png");

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.client().showVampireRageHUDOverlay.get()) {
            if (VampirePlayer.get(this.player()).getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE)) {
                renderTextureOverlay(graphics, RAGE_TEXTURE, 1.0F);
            }
        }
    }
}
