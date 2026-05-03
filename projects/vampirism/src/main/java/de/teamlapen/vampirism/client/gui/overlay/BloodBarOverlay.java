package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.BaseOverlay;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class BloodBarOverlay extends BaseOverlay {
    public static final Identifier BACKGROUND = VIdentifier.mod("blood_bar/background");
    public static final Identifier QUARTER = VIdentifier.mod("blood_bar/quarter");
    public static final Identifier HALF = VIdentifier.mod("blood_bar/half");
    public static final Identifier THREE_QUARTER = VIdentifier.mod("blood_bar/three_quarter");
    public static final Identifier FULL = VIdentifier.mod("blood_bar/full");
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker partialTicks) {
        if (canRenderOverlays() && Helper.isVampire(this.player()) && !VampirismMod.services().imc().isRequestedToDisableBloodbar()) {
            if (this.mc.gameMode.hasExperience()) {
                IBloodStats stats = VampirePlayer.get(this.player()).getBloodStats();
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
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, top, 9, 9);

                    if (idx < blood) {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, idx < blood2 ? FULL : HALF, x, top, 9, 9);
                        if (idx == blood2) {
                            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, THREE_QUARTER, x, top, 9, 9);
                        }
                    } else if (idx == blood) {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, QUARTER, x, top, 9, 9);
                    }
                }
            }
        }
    }
}
