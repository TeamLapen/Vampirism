package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

public class BloodBarOverlay implements IGuiOverlay {
    private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(ExtendedGui gui, @NotNull GuiGraphics graphics, float partialTicks, int width, int height) {
        if (this.mc.player != null && Helper.isVampire(this.mc.player) && !IMCHandler.requestedToDisableBloodbar) {
            if (this.mc.gameMode.hasExperience() && this.mc.player.isAlive()) {
                VampirePlayer.getOpt(this.mc.player).map(VampirePlayer::getBloodStats).ifPresent(stats -> {
                    int left = this.mc.getWindow().getGuiScaledWidth() / 2 + 91;
                    int top = this.mc.getWindow().getGuiScaledHeight() - ((ExtendedGui) this.mc.gui).rightHeight;
                    ((ExtendedGui) this.mc.gui).rightHeight += 10;
                    int blood = stats.getBloodLevel();
                    int maxBlood = stats.getMaxBlood();
                    int blood2 = blood - 20;
                    int maxBlood2 = maxBlood - 20;
                            for (int i = 0; i < 10; ++i) {
                                int idx = i * 2 + 1;
                                int x = left - i * 8 - 9;

                                // Draw Background
                                graphics.blit(icons, x, top, 0, idx <= maxBlood2 ? 9 : 0, 9, 9);

                                if (idx < blood) {
                                    graphics.blit(icons, x, top, 9, idx < blood2 ? 9 : 0, 9, 9);
                                    if (idx == blood2) {
                                        graphics.blit(icons, x, top, 18, 9, 9, 9);
                                    }
                                } else if (idx == blood) {
                                    graphics.blit(icons, x, top, 18, 0, 9, 9);
                                }
                            }
                        }
                );
            }
        }
    }
}
