package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BatOverlay extends TextureOverlay {

    public static final ResourceLocation BAT_TEXTURE = VResourceLocation.mod("textures/misc/bat.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.CLIENT.enableHudBatOverlayRendering.get()) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get(this.player()).getActionHandler();
            if (actionHandler.isActionActive(VampireActions.BAT)) {
                renderTextureOverlay(graphics, BAT_TEXTURE, 1.0F);
            }
        }
    }
}