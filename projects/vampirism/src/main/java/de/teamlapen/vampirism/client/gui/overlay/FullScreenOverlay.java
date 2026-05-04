package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.BaseOverlay;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;

public class FullScreenOverlay extends BaseOverlay {

    private int color;
    private long startTicks = -1;
    private long onTicks;
    private long mainTicks;
    private long offTicks;
    private float percentage;

    public void start(Level level, int onTicks, int offTicks, int color) {
        start(level, onTicks, 0, offTicks, color);
    }

    public void start(Level level, int onTicks, int mainTicks, int offTicks, int color) {
        this.startTicks = level.getGameTime();
        this.onTicks = onTicks;
        this.offTicks = offTicks;
        this.mainTicks = mainTicks;
        this.color = color;
        this.percentage = 0;
    }

    @Override
    public boolean canRenderOverlays() {
        return super.canRenderOverlays() && ModConfig.client().showFullScreenOverlay.get();
    }

    @Override
    public void render(GuiGraphicsExtractor GuiGraphicsExtractor, DeltaTracker deltaTracker) {
        if (this.percentage <= 0) return;
        if (!canRenderOverlays()) return;

        GuiGraphicsExtractor.fill(0,0,GuiGraphicsExtractor.guiWidth(),GuiGraphicsExtractor.guiHeight(), ARGB.color(this.percentage, color));
    }

    public void update() {
        if (this.startTicks == -1) return;
        var level = level();

        //noinspection ConstantValue
        if (level == null) {
            this.startTicks = -1;
            return;
        }
        long gameTime = level.getGameTime();
        if (gameTime < this.startTicks + this.onTicks) {
            this.percentage = (gameTime - this.startTicks) / (float) this.onTicks;
        } else if (gameTime < this.startTicks + this.onTicks + this.mainTicks) {
            this.percentage = 1;
        } else if (gameTime < this.startTicks + this.onTicks + this.mainTicks + this.offTicks) {
            this.percentage = 1f - (gameTime - this.startTicks - this.onTicks - this.mainTicks) / (float) this.offTicks;
        } else {
            this.startTicks = -1;
            this.percentage = 0;
        }
    }
}
