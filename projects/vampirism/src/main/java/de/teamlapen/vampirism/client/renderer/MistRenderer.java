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
 * Renders the volumetric cloud shown instead of the model while an entity is in mist form.
 * <p>
 * Renderers only {@linkplain #submitMistCloud submit} their clouds during the normal entity pass; all of them are
 * drawn together afterwards in {@link #onRenderLevelAfterWeather} through {@link VolumetricBillboards}, which
 * explains why the draw cannot happen in the entity pass itself.
 */
public class MistRenderer {

    /**
     * Size of the cloud relative to the entity's bounding box, and where along the entity's height it sits -
     * the main tuning knobs for how the effect reads. Much wider than the entity but no taller, giving a broad
     * low bank of mist that swallows the silhouette. For a 0.6 x 1.8 player: 3.6 wide, 1.8 tall, sitting on the
     * ground.
     */
    private static final float WIDTH_SCALE = 6.0f;
    private static final float HEIGHT_SCALE = 1.0f;
    private static final float CENTER_HEIGHT_SCALE = 0.5f;

    /**
     * How far the trailing side stretches at full speed, and the speed (in blocks/tick) at which that stretch
     * saturates. Kept here rather than in the shader because the bounding radius derived from it has to match the
     * quad the vertex shader builds. Deliberately modest: the wake reads mostly as the noise flowing through the
     * volume, so deforming the silhouette on top of that only makes the shape lurch when the heading changes.
     */
    private static final float TRAIL_STRETCH = 0.4f;
    private static final float TRAIL_SPEED_CAP = 0.6f;
    /**
     * Margin between the ellipsoid the shader carves out and the sphere the raymarch bounds itself to. The
     * shader's density has to reach zero inside this, or its silhouette would show up as a hard circle.
     */
    private static final float BOUND_MARGIN = 1.5f;

    private static final List<MistInstance> INSTANCES = new ArrayList<>();

    /**
     * Records one mist cloud to be drawn at the end of the level render.
     *
     * @param fade         0-1 envelope; the cloud thins out rather than popping in and out
     * @param entityWidth  bounding box width, scaled up to the cloud's own footprint
     * @param entityHeight bounding box height, scaled down to the cloud's flatter profile
     * @param velocity     smoothed movement in blocks/tick, driving the trailing wake and turbulence
     * @param flow         accumulated drift of the noise field in blocks, giving the movement animation
     * @param poseStack    the entity's pose, i.e. camera-relative with world axes
     */
    public static void submitMistCloud(float fade, float entityWidth, float entityHeight, Vec3 velocity, Vec3 flow, PoseStack poseStack) {
        if (fade <= 0.0f || entityWidth <= 0.0f || entityHeight <= 0.0f) {
            return;
        }

        // Semi-axes of the world-aligned ellipsoid the shader carves out, centered at the entity's mid-height.
        float radiusXZ = entityWidth * WIDTH_SCALE * 0.5f;
        float radiusY = entityHeight * HEIGHT_SCALE * 0.5f;

        Vector4f transformed = poseStack.last().pose().transform(new Vector4f(0.0f, entityHeight * CENTER_HEIGHT_SCALE, 0.0f, 1.0f));
        Vec3 center = new Vec3(transformed.x, transformed.y, transformed.z);
        double distance = center.length();
        if (distance < 1.0e-4) {
            return; // camera is inside the cloud; the billboard has no meaningful orientation
        }

        // Horizontal heading only, in world axes. Vertical motion is deliberately ignored: it would otherwise
        // drive the wake and turbulence during a fall, where the entity is not moving across the ground at all.
        float speed = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        float headingX = speed > 1.0e-4f ? (float) (velocity.x / speed) : 0.0f;
        float headingZ = speed > 1.0e-4f ? (float) (velocity.z / speed) : 0.0f;
        float trailStretch = TRAIL_STRETCH * Math.min(speed / TRAIL_SPEED_CAP, 1.0f);

        float boundRadius = Math.max(radiusXZ, radiusY) * BOUND_MARGIN * (1.0f + trailStretch);

        INSTANCES.add(new MistInstance(center, flow, radiusXZ, radiusY, boundRadius, headingX, headingZ, speed, fade, trailStretch));
    }

    /**
     * Draws every cloud submitted this frame. Runs after weather so the depth buffer is complete, which is what
     * the shader tests against.
     */
    @SubscribeEvent
    public void onRenderLevelAfterWeather(RenderLevelStageEvent.AfterWeather event) {
        if (INSTANCES.isEmpty()) {
            return;
        }
        try {
            ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
            RenderPipeline pipeline = ModRenderPipelines.mist(ModRenderPipelines.VolumetricQuality.of(particleStatus));

            // Farthest first, so overlapping clouds blend in the right order - there is no depth write to sort
            // them for us.
            INSTANCES.sort(Comparator.comparingDouble((MistInstance mist) -> mist.center().lengthSqr()).reversed());

            VolumetricBillboards.draw("Vampirism mist", pipeline, event.getModelViewMatrix(), INSTANCES.stream().map(MistInstance::pack).toList());
        } finally {
            INSTANCES.clear();
        }
    }

    private record MistInstance(Vec3 center, Vec3 flow, float radiusXZ, float radiusY, float boundRadius,
                                float headingX, float headingZ, float speed, float fade, float trailStretch) {

        /**
         * Packs the instance into a matrix, one parameter group per column, matching the layout documented in
         * rendertype_mist.fsh.
         */
        private Matrix4f pack() {
            return new Matrix4f(
                    (float) this.center.x, (float) this.center.y, (float) this.center.z, this.boundRadius,
                    (float) this.flow.x, (float) this.flow.y, (float) this.flow.z, this.radiusXZ,
                    this.headingX, this.headingZ, this.speed, this.radiusY,
                    this.fade, this.trailStretch, 0.0f, 0.0f
            );
        }
    }
}
