package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BatOverlay extends TextureOverlay {

    public static final ResourceLocation BAT_TEXTURE = VResourceLocation.mod("textures/misc/bat.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableHudBatOverlayRendering.get()) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get(this.mc.player).getActionHandler();
            if (actionHandler.isActionActive(VampireActions.BAT.get())) {
                renderTextureOverlay(graphics, BAT_TEXTURE, 1.0F);
            }
        }
    }
}