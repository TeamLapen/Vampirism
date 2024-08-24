package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DisguiseOverlay extends TextureOverlay {

    public static final ResourceLocation DISGUISE_TEXTURE = VResourceLocation.mod("textures/misc/disguise.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableDisguiseOverlayRendering.get()) {
            HunterPlayerSpecialAttribute huntSpecial = ((IVampirismPlayer) this.mc.player).getVampAtts().getHuntSpecial();
            if (huntSpecial.isDisguised()) {
                graphics.pose().pushPose();
                scaleBy(huntSpecial.getDisguiseProgress(), 1 / 4f, 2F, 1.0F, graphics);
                renderTextureOverlay(graphics, DISGUISE_TEXTURE, 1.0F);
                graphics.pose().popPose();
            }
        }
    }
}