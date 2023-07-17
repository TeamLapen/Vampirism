package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

public class FactionLevelOverlay extends GuiComponent implements IGuiOverlay {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull ForgeGui gui, @NotNull PoseStack mStack, float partialTicks, int width, int height) {
        if (this.mc.player != null && this.mc.player.isAlive() && this.mc.player.jumpableVehicle() == null && !this.mc.options.hideGui) {
            gui.setupOverlayRenderState(true, false);

            FactionPlayerHandler.getOpt(this.mc.player).ifPresent(handler -> {
                IPlayableFaction<?> faction = handler.getCurrentFaction();
                if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && faction != null) {
                    // boolean flag1 = false;
                    int color = faction.getColor();
                    int lord = handler.getLordLevel();
                    String text;
                    if (lord > 0) {
                        String title = handler.getLordTitle().getString();
                        text = title.substring(0, Math.min(3, title.length()));
                    } else {
                        text = String.valueOf(handler.getCurrentLevel());
                    }
                    int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + VampirismConfig.CLIENT.guiLevelOffsetX.get();
                    int y = this.mc.getWindow().getGuiScaledHeight() - (VampirismConfig.CLIENT.guiLevelOffsetY.get() - 47) - gui.rightHeight;
                    this.mc.font.draw(mStack, text, x + 1, y, 0);
                    this.mc.font.draw(mStack, text, x - 1, y, 0);
                    this.mc.font.draw(mStack, text, x, y + 1, 0);
                    this.mc.font.draw(mStack, text, x, y - 1, 0);
                    this.mc.font.draw(mStack, text, x, y, color);
                }
            });
        }
    }
}
