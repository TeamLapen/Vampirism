package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.factions.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RageOverlay extends TextureOverlay {

    public static final ResourceLocation RAGE_TEXTURE = VResourceLocation.mod("textures/misc/rage.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (canRenderOverlays() && ModConfig.CLIENT.enableRageOverlayRendering.get()) {
            if (VampirePlayer.get(this.player()).getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE)) {
                renderTextureOverlay(graphics, RAGE_TEXTURE, 1.0F);
            }
        }
    }
}
