package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.BaseOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundDraculaEventPacket;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaEvent;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class DraculaEventOverlay extends BaseOverlay {

    /**
     * Active texture style. All atlases share the same layout, see {@link Style}.
     */
    private static final Style STYLE = Style.CRIMSON;

    private static final int TEX_W = 512;
    private static final int TEX_H = 128;
    private static final float SCALE = 0.7f;

    private static final int FRAME_W = 318;
    private static final int FRAME_H = 40;
    private static final int BAR_X = 12;
    private static final int BAR_Y = 15;
    private static final int BAR_W = 294;
    private static final int BAR_H = 10;
    private static final int[] GEM_X = {142, 156, 170};
    private static final int GEM_Y = 30;

    private static final int V_FILL = 40;
    private static final int V_FILL_INVULNERABLE = 70;
    private static final int V_TRAIL = 80;
    private static final int V_SHIELD = 90;
    private static final int SHIELD_PERIOD = 16;
    private static final int V_GLOW = 100;
    private static final int U_GEM = 368;
    private static final int U_EDGE = 376;

    private static final long FADE_MS = 250;
    private static final long FLASH_MS = 350;
    private static final long TRAIL_HOLD_MS = 500;
    private static final float TRAIL_SPEED_PER_MS = 0.0006f;

    @Nullable
    private DraculaEvent event;
    private float trailPercentage;
    private long lastTrailUpdate;
    private long lastDamageTime;
    private long invulnerableChangeTime;
    private long flashTime;

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (this.event == null) {
            return;
        }
        long now = Util.getMillis();
        updateTrail(now);
        float percentage = Mth.clamp(this.event.getPercentage(), 0, 1);
        float pulse = 0.5f + 0.5f * Mth.sin(now / 220f);
        float invulnerableFade = Mth.clamp((now - this.invulnerableChangeTime) / (float) FADE_MS, 0, 1);
        if (!this.event.isInVulnerable()) {
            invulnerableFade = 1 - invulnerableFade;
        }
        Identifier texture = STYLE.texture;

        guiGraphics.nextStratum();
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.scale(SCALE);
        int x = (int) (guiGraphics.guiWidth() / SCALE / 2f - FRAME_W / 2f);
        int y = 3;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, FRAME_W, FRAME_H, TEX_W, TEX_H);

        int fillWidth = Math.round(BAR_W * percentage);
        int trailWidth = Math.round(BAR_W * this.trailPercentage);
        if (trailWidth > fillWidth) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X + fillWidth, y + BAR_Y, fillWidth, V_TRAIL, trailWidth - fillWidth, BAR_H, TEX_W, TEX_H);
        }
        if (fillWidth > 0) {
            int stageRow = Mth.clamp(this.event.getStage().ordinal() - 1, 0, 2);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X, y + BAR_Y, 0, V_FILL + stageRow * BAR_H, fillWidth, BAR_H, TEX_W, TEX_H);
            if (invulnerableFade > 0) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X, y + BAR_Y, 0, V_FILL_INVULNERABLE, fillWidth, BAR_H, TEX_W, TEX_H, ARGB.white(invulnerableFade));
            }
            if (fillWidth >= 2) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X + fillWidth - 2, y + BAR_Y, U_EDGE, V_FILL, 2, BAR_H, TEX_W, TEX_H);
            }
        }

        if (invulnerableFade > 0) {
            int shieldOffset = STYLE.shieldScrollMsPerPixel > 0 ? (int) (now / STYLE.shieldScrollMsPerPixel % SHIELD_PERIOD) : 0;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X, y + BAR_Y, shieldOffset, V_SHIELD, BAR_W, BAR_H, TEX_W, TEX_H, ARGB.white(invulnerableFade * (0.7f + 0.3f * pulse)));
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + BAR_X - 3, y + BAR_Y - 3, 0, V_GLOW, BAR_W + 6, BAR_H + 6, TEX_W, TEX_H, ARGB.white(invulnerableFade * (0.3f + 0.7f * pulse)));
        }

        float flash = 1 - Mth.clamp((now - this.flashTime) / (float) FLASH_MS, 0, 1);
        if (flash > 0) {
            guiGraphics.fill(x + BAR_X, y + BAR_Y, x + BAR_X + BAR_W, y + BAR_Y + BAR_H, ARGB.white(flash * 0.6f));
        }

        int stage = this.event.getStage().ordinal();
        for (int i = 0; i < Math.min(stage, GEM_X.length); i++) {
            float alpha = i == stage - 1 ? 0.55f + 0.45f * pulse : 1f;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + GEM_X[i], y + GEM_Y, U_GEM, V_FILL, 7, 7, TEX_W, TEX_H, ARGB.white(alpha));
        }
        pose.popMatrix();
    }

    /**
     * Lets the "recently lost" health segment linger briefly and then drain towards the current health.
     */
    private void updateTrail(long now) {
        float percentage = this.event.getPercentage();
        if (this.trailPercentage <= percentage) {
            this.trailPercentage = percentage;
        } else if (now - this.lastDamageTime > TRAIL_HOLD_MS) {
            this.trailPercentage = Math.max(percentage, this.trailPercentage - (now - this.lastTrailUpdate) * TRAIL_SPEED_PER_MS);
        }
        this.lastTrailUpdate = now;
    }

    private void setPercentage(float percentage) {
        if (percentage < this.event.getPercentage()) {
            this.lastDamageTime = Util.getMillis();
        }
        this.event.setPercentage(percentage);
    }

    private void setStage(FightStage stage) {
        if (stage != this.event.getStage()) {
            this.flashTime = Util.getMillis();
        }
        this.event.setStage(stage);
    }

    private void setInvulnerable(boolean invulnerable) {
        if (invulnerable != this.event.isInVulnerable()) {
            long now = Util.getMillis();
            this.invulnerableChangeTime = now;
            this.flashTime = now;
        }
        this.event.setInVulnerable(invulnerable);
    }

    public void handle(ClientboundDraculaEventPacket packet) {
        switch (packet.operation()) {
            case ClientboundDraculaEventPacket.AddOperation addOperation -> {
                this.event = DraculaEvent.fromOperation(addOperation);
                this.trailPercentage = this.event.getPercentage();
                this.invulnerableChangeTime = 0;
                this.flashTime = 0;
            }
            case ClientboundDraculaEventPacket.RemoveOperation removeOperation -> {
                if (event != null && event.id().equals(removeOperation.id())) {
                    this.event = null;
                }
            }
            case ClientboundDraculaEventPacket.UpdateOperation updateOperation -> {
                if (event != null && event.id().equals(updateOperation.id())) {
                    setPercentage(updateOperation.percentage());
                    setStage(updateOperation.stage());
                    setInvulnerable(updateOperation.vulnerable());
                }
            }
            case ClientboundDraculaEventPacket.UpdateProgressOperation updateProgressOperation -> {
                if (event != null && event.id().equals(updateProgressOperation.id())) {
                    setPercentage(updateProgressOperation.percentage());
                }
            }
            case ClientboundDraculaEventPacket.UpdateStageOperation updateStageOperation -> {
                if (event != null && event.id().equals(updateStageOperation.id())) {
                    setStage(updateStageOperation.stage());
                }
            }
            case ClientboundDraculaEventPacket.UpdateVulnerableOperation updateVulnerableOperation -> {
                if (event != null && event.id().equals(updateVulnerableOperation.id())) {
                    setInvulnerable(updateVulnerableOperation.vulnerable());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + packet.operation());
        }
    }

    /**
     * Texture styles for the boss bar. Each atlas (512x128) has the same layout:
     * frame at (0,0) 318x40, phase fills at (0,40/50/60), invulnerable fill (0,70), damage trail (0,80),
     * tiling shield overlay (0,90) 310x10, invulnerability glow (0,100) 300x16,
     * lit phase gem (368,40) 7x7, fill edge highlight (376,40) 2x10.
     */
    private enum Style {
        /** Dark iron frame with crimson trim, invulnerability shown as silver chains. */
        CRIMSON("textures/gui/overlay/dracula_event_crimson.png", 0);

        private final Identifier texture;
        /** 0 = static shield overlay */
        private final int shieldScrollMsPerPixel;

        Style(String path, int shieldScrollMsPerPixel) {
            this.texture = VIdentifier.mod(path);
            this.shieldScrollMsPerPixel = shieldScrollMsPerPixel;
        }
    }
}
