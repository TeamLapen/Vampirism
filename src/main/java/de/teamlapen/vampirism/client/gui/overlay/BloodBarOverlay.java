package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BloodBarOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation BACKGROUND = VResourceLocation.mod("blood_bar/background");
    public static final ResourceLocation QUARTER = VResourceLocation.mod("blood_bar/quarter");
    public static final ResourceLocation HALF = VResourceLocation.mod("blood_bar/half");
    public static final ResourceLocation THREE_QUARTER = VResourceLocation.mod("blood_bar/three_quarter");
    public static final ResourceLocation FULL = VResourceLocation.mod("blood_bar/full");
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker partialTicks) {
        if (this.mc.player != null && Helper.isVampire(this.mc.player) && !IMCHandler.requestedToDisableBloodbar) {
            if (this.mc.gameMode.hasExperience() && this.mc.player.isAlive()) {
                IBloodStats stats = VampirePlayer.get(this.mc.player).getBloodStats();
                int left = this.mc.getWindow().getGuiScaledWidth() / 2 + 91;
                int top = this.mc.getWindow().getGuiScaledHeight() - this.mc.gui.rightHeight;
                this.mc.gui.rightHeight += 10;
                int blood = stats.getBloodLevel();
                int maxBlood = stats.getMaxBlood();
                int blood2 = blood - 20;
                int maxBlood2 = maxBlood - 20;
                for (int i = 0; i < 10; ++i) {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;

                    // Draw Background
                    graphics.blitSprite(BACKGROUND, x, top, 9,9);

                    if (idx < blood) {
                        graphics.blitSprite( idx < blood2 ? FULL : HALF, x, top, 9,9);
                        if (idx == blood2) {
                            graphics.blitSprite(THREE_QUARTER, x, top, 9,9);
                        }
                    } else if (idx == blood) {
                        graphics.blitSprite(QUARTER, x, top, 9,9);
                    }
                }
            }
        }
    }
}
