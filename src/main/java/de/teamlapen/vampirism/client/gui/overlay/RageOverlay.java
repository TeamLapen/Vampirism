package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RageOverlay extends TextureOverlay {

    public static final ResourceLocation RAGE_TEXTURE = VResourceLocation.mod("textures/misc/rage.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableRageOverlayRendering.get()) {
            if (VampirePlayer.get(Minecraft.getInstance().player).getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())) {
                renderTextureOverlay(graphics, RAGE_TEXTURE, 1.0F);
            }
        }
    }
}
