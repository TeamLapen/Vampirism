package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.config.ClientConfig;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Renders the thin shell of darkness around entities carrying the aura of darkness effect.
 * <p>
 * Works the same way as {@link MistRenderer}: renderers {@linkplain #submitAura submit} their auras during the
 * normal entity pass and they are all drawn together afterwards through {@link VolumetricBillboards}, which is
 * where the reason for the split is explained. Unlike mist, the entity itself is still rendered - the aura is a
 * shell around it, and the raymarch stops at the scene depth, so only the part of the shell in front of the
 * entity is drawn.
 */
public class AuraOfDarknessRenderer {

    /**
     * Semi-axes of the shell relative to the entity's bounding box, and where along its height it is centered.
     * Deliberately tight: this is a halo hugging the silhouette, not a cloud. For a 0.6 x 1.8 player it comes
     * out 1.3 wide and 2.2 tall, centered at the waist.
     */
    private static final float WIDTH_SCALE = 1.1f;
    private static final float HEIGHT_SCALE = 0.62f;
    private static final float CENTER_HEIGHT_SCALE = 0.5f;
    /**
     * How far past the radii the shell reaches, and so the ellipsoid both the billboard and the raymarch are
     * fitted to. Not a margin: the band's outer falloff ends at exactly this, so fitting to it neither clips the
     * halo nor pads it. Kept in sync with SHELL_OUTER in rendertype_aura_of_darkness.fsh.
     */
    private static final float SUPPORT_SCALE = 1.02f;

    /**
     * The effect lands on every vampire in a 10 block radius at once, so a crowded fight could ask for dozens of
     * raymarched volumes in a frame. Past this distance the aura is a few pixels wide and not worth a full march,
     * and beyond this count only the nearest are drawn - both keep the worst case bounded.
     */
    private static final double MAX_DISTANCE_SQ = 32.0 * 32.0;
    private static final int MAX_INSTANCES = 12;

    private static final List<AuraInstance> INSTANCES = new ArrayList<>();

    /**
     * Records one aura to be drawn at the end of the level render.
     *
     * @param fade         0-1 envelope; the shell thins out rather than popping in and out
     * @param entityWidth  bounding box width, scaled to the shell's horizontal semi-axis
     * @param entityHeight bounding box height, scaled to the shell's vertical semi-axis
     * @param phaseSeed    per-entity value offsetting the swirl, so neighbouring auras do not move in lockstep
     * @param poseStack    the entity's pose, i.e. camera-relative with world axes
     */
    public static void submitAura(float fade, float entityWidth, float entityHeight, int phaseSeed, PoseStack poseStack) {
        if (fade <= 0.0f || entityWidth <= 0.0f || entityHeight <= 0.0f) {
            return;
        }

        Vector4f transformed = poseStack.last().pose().transform(new Vector4f(0.0f, entityHeight * CENTER_HEIGHT_SCALE, 0.0f, 1.0f));
        Vec3 center = new Vec3(transformed.x, transformed.y, transformed.z);
        double distanceSq = center.lengthSqr();
        if (distanceSq < 1.0e-8 || distanceSq > MAX_DISTANCE_SQ) {
            // Too far to be worth marching, or the camera is exactly on the center, where the billboard has no
            // orientation at all to derive.
            return;
        }

        // Semi-axes of the world-aligned ellipsoid the shader carves out, centered at the entity's mid-height.
        float radiusXZ = entityWidth * WIDTH_SCALE;
        float radiusY = entityHeight * HEIGHT_SCALE;

        // Spread the seed over a minute of animation; the exact mapping does not matter, only that two entities
        // rarely land on the same phase.
        float phase = (Math.abs(phaseSeed) % 997) * 0.061f;

        INSTANCES.add(new AuraInstance(center, radiusXZ, radiusY, radiusXZ * SUPPORT_SCALE, radiusY * SUPPORT_SCALE, phase, fade));
    }

    /**
     * Draws every aura submitted this frame. Runs after weather so the depth buffer is complete, which is what
     * the shader tests against.
     */
    @SubscribeEvent
    public void onRenderLevelAfterWeather(RenderLevelStageEvent.AfterWeather event) {
        if (INSTANCES.isEmpty()) {
            return;
        }
        try {
            ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
            RenderPipeline pipeline = ModRenderPipelines.auraOfDarkness(ModRenderPipelines.VolumetricQuality.of(particleStatus));

            // Farthest first, so overlapping auras blend in the right order - there is no depth write to sort
            // them for us. Which also means the nearest, the ones worth keeping under the cap, are last.
            INSTANCES.sort(Comparator.comparingDouble((AuraInstance aura) -> aura.center().lengthSqr()).reversed());
            List<AuraInstance> drawn = INSTANCES.size() > MAX_INSTANCES ? INSTANCES.subList(INSTANCES.size() - MAX_INSTANCES, INSTANCES.size()) : INSTANCES;

            VolumetricBillboards.draw("Vampirism aura of darkness", pipeline, event.getModelViewMatrix(), drawn.stream().map(AuraInstance::pack).toList());
        } finally {
            INSTANCES.clear();
        }
    }

    private record AuraInstance(Vec3 center, float radiusXZ, float radiusY, float supportXZ, float supportY, float phase, float fade) {

        /**
         * Packs the instance into a matrix, one parameter group per column, matching the layout documented in
         * rendertype_aura_of_darkness.fsh. The last slot is left for VolumetricBillboards to fill in with the
         * resolution the pass ends up drawn at.
         */
        private Matrix4f pack() {
            return new Matrix4f(
                    (float) this.center.x, (float) this.center.y, (float) this.center.z, this.supportXZ,
                    this.radiusXZ, this.radiusY, this.phase, this.supportY,
                    this.fade, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 0.0f
            );
        }
    }

}
