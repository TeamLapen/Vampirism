package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.BaseOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.SunBlindUtil;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jetbrains.annotations.Nullable;

public class SunBlindOverlay extends BaseOverlay {

    private static final Identifier SUN_BLIND_TEXTURE = VIdentifier.mod("textures/misc/sun_blind.png");

    private static final int CONTRAST_VEIL_COLOR = 0x9AA0A6;
    private static final int EXPOSURE_WASH_COLOR = 0xFFF6E0;
    private static final int BLOOM_HALO_COLOR = 0xFFF3D0;
    private static final int BLOOM_CORE_COLOR = 0xFFFFFF;

    private static final int CONTRAST_VEIL_COLOR_INVERTED = 0x655F59;
    private static final int EXPOSURE_WASH_COLOR_INVERTED = 0x00091F;
    private static final int BLOOM_HALO_COLOR_INVERTED = 0x000C2F;
    private static final int BLOOM_CORE_COLOR_INVERTED = 0x000000;

    private static final int LENS_FLARE_WARM_COLOR = 0xFFE6B0;
    private static final int LENS_FLARE_COOL_COLOR = 0xBFE0FF;

    private float effectIntensity;

    public void update() {
        LocalPlayer player = mc().player;
        // This is for non-vampires, vampires are affected in the VampirePlayer class
        boolean affected = player != null && !Helper.isVampire(player) && player.hasEffect(ModEffects.SUN_SENSITIVITY);
        this.effectIntensity = SunBlindUtil.update(this.effectIntensity, affected ? SunBlindUtil.computeTarget(player) : 0f);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!this.player().isAlive() || !mc().options.getCameraType().isFirstPerson()) return;

        float blindIntensity = Helper.isVampire(player()) ? VampirePlayer.get(player()).getSunBlindIntensity() : effectIntensity;
        if (blindIntensity <= 0) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        boolean inverted = mc().options.vampirism$invertedSunBlindness().get();

        renderFullScreenDazzle(graphics, screenWidth, screenHeight, blindIntensity, inverted);

        Vec3 sunOnScreen = projectSunOntoScreen(screenWidth, screenHeight);
        if (sunOnScreen == null) return;

        renderSunBloom(graphics, sunOnScreen, screenHeight, blindIntensity, inverted);
        if (!inverted) {
            renderLensFlare(graphics, sunOnScreen, screenWidth, screenHeight, blindIntensity);
        }
    }

    private void renderFullScreenDazzle(GuiGraphicsExtractor graphics, int screenWidth, int screenHeight, float blindIntensity, boolean inverted) {
        // Gray wash over the whole screen that makes dark areas lighter, so the image looks flat
        graphics.fill(0, 0, screenWidth, screenHeight, ARGB.color(0.2f * blindIntensity, inverted ? CONTRAST_VEIL_COLOR_INVERTED : CONTRAST_VEIL_COLOR));
        // Warm white that floods the whole screen, so everything looks too bright
        graphics.fill(0, 0, screenWidth, screenHeight, ARGB.color(0.85f * blindIntensity, inverted ? EXPOSURE_WASH_COLOR_INVERTED : EXPOSURE_WASH_COLOR));
    }

    private void renderSunBloom(GuiGraphicsExtractor graphics, Vec3 sunOnScreen, int screenHeight, float blindIntensity, boolean inverted) {
        float sunX = (float) sunOnScreen.x;
        float sunY = (float) sunOnScreen.y;
        // Soft warm glow that spreads out around the sun
        drawGlow(graphics, sunX, sunY, screenHeight, inverted ? BLOOM_HALO_COLOR_INVERTED : BLOOM_HALO_COLOR, blindIntensity);
        // Small, very bright white spot right on the sun
        drawGlow(graphics, sunX, sunY, screenHeight * 0.7f, inverted ? BLOOM_CORE_COLOR_INVERTED : BLOOM_CORE_COLOR, blindIntensity);
    }

    private void renderLensFlare(GuiGraphicsExtractor graphics, Vec3 sunOnScreen, int screenWidth, int screenHeight, float blindIntensity) {
        float sunX = (float) sunOnScreen.x;
        float sunY = (float) sunOnScreen.y;
        float screenCenterX = screenWidth / 2f;
        float screenCenterY = screenHeight / 2f;

        for (int ghostIndex = 0; ghostIndex < 6; ghostIndex++) {
            float positionAlongAxis = 0.55f - ghostIndex * 0.34f;
            float ghostX = screenCenterX + (sunX - screenCenterX) * positionAlongAxis;
            float ghostY = screenCenterY + (sunY - screenCenterY) * positionAlongAxis;
            float ghostSize = screenHeight * (0.075f + 0.06f * (ghostIndex % 3));
            int ghostColor = ghostIndex % 2 == 0 ? LENS_FLARE_WARM_COLOR : LENS_FLARE_COOL_COLOR;
            // Warm and cool circles take turns along the line from the sun to the screen center, giving a colorful look
            drawGlow(graphics, ghostX, ghostY, ghostSize, ghostColor, blindIntensity * 0.5f);
        }
    }

    private void drawGlow(GuiGraphicsExtractor graphics, float centerX, float centerY, float diameter, int color, float alpha) {
        if (alpha <= 0) return;
        int size = (int) diameter;
        int left = (int) (centerX - diameter / 2f);
        int top = (int) (centerY - diameter / 2f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, SUN_BLIND_TEXTURE, left, top, 0f, 0f, size, size, size, size, ARGB.color(Math.min(1f, alpha), color));
    }

    @Nullable
    private Vec3 projectSunOntoScreen(int screenWidth, int screenHeight) {
        ClientLevel level = mc().level;
        AbstractClientPlayer player = this.player();
        if (level == null) return null;

        float sunAngleDegrees = level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, player.blockPosition());
        double sunAngleRadians = Math.toRadians(sunAngleDegrees);
        Vec3 sunWorldDirection = new Vec3(-Math.sin(sunAngleRadians), Math.cos(sunAngleRadians), 0);

        Camera camera = mc().gameRenderer.getMainCamera();
        double sunDistanceForward = dot(sunWorldDirection, camera.forwardVector());
        if (sunDistanceForward <= 0.001) return null;

        double sunOffsetRight = -dot(sunWorldDirection, camera.leftVector());
        double sunOffsetUp = dot(sunWorldDirection, camera.upVector());

        double halfVerticalFovTangent = Math.tan(Math.toRadians(mc().options.fov().get()) / 2.0);
        double halfHorizontalFovTangent = halfVerticalFovTangent * (screenWidth / (double) screenHeight);

        double screenX = screenWidth * (0.5 + 0.5 * (sunOffsetRight / sunDistanceForward) / halfHorizontalFovTangent);
        double screenY = screenHeight * (0.5 - 0.5 * (sunOffsetUp / sunDistanceForward) / halfVerticalFovTangent);

        return new Vec3(screenX, screenY, 0);
    }

    private static double dot(Vec3 worldVector, Vector3fc cameraAxis) {
        return worldVector.x * cameraAxis.x() + worldVector.y * cameraAxis.y() + worldVector.z * cameraAxis.z();
    }

    @Override
    protected boolean isEnabledInConfig() {
        return ModConfig.client().showSunBlindOverlay.get();
    }
}
