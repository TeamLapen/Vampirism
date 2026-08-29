package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Renders a flat-colored fill over entities carrying the aura of darkness effect, with a more solid border
 * hugging the silhouette's edge.
 * <p>
 * Works the same way as {@link MistRenderer}: renderers {@linkplain #submitAura submit} their auras during the
 * normal entity pass and they are all drawn together afterwards through {@link VolumetricBillboards}, which is
 * where the reason for the split is explained. Unlike mist, the entity itself is still rendered - the aura covers
 * its silhouette, occluded by scene depth so only the part of it in front of the entity is drawn. Unlike a
 * raymarched volume, each fragment is a single evaluation against the entity's bounding ellipsoid - there is
 * nothing to march.
 */
public class AuraOfDarknessRenderer {

    /**
     * Semi-axes of the ring relative to the entity's bounding box, and where along its height it is centered.
     * Deliberately tight: this is a halo hugging the silhouette, not a cloud. For a 0.6 x 1.8 player it comes
     * out 1.3 wide and 2.2 tall, centered at the waist.
     */
    private static final float WIDTH_SCALE = 1.1f;
    private static final float HEIGHT_SCALE = 0.62f;
    private static final float CENTER_HEIGHT_SCALE = 0.5f;
    /**
     * How far past the radii the ring's outer edge reaches, and so the ellipsoid both the billboard and the
     * ring band are fitted to. Not a margin: the band's outer falloff ends at exactly this, so fitting to it
     * neither clips the border nor pads it. Kept in sync with RING_OUTER in rendertype_aura_of_darkness.fsh.
     */
    private static final float SUPPORT_SCALE = 1.15f;

    /**
     * The effect lands on every vampire in a 10 block radius at once, so a crowded fight could ask for dozens of
     * borders in a frame. Past this distance the border is a few pixels wide and not worth drawing, and beyond
     * this count only the nearest are drawn - both keep the worst case bounded.
     */
    private static final double MAX_DISTANCE_SQ = 32.0 * 32.0;
    private static final int MAX_INSTANCES = 12;

    private static final List<AuraInstance> INSTANCES = new ArrayList<>();

    /**
     * Records one aura border to be drawn at the end of the level render.
     *
     * @param fade         0-1 envelope; the border thins out rather than popping in and out
     * @param entityWidth  bounding box width, scaled to the border's horizontal semi-axis
     * @param entityHeight bounding box height, scaled to the border's vertical semi-axis
     * @param poseStack    the entity's pose, i.e. camera-relative with world axes
     */
    public static void submitAura(float fade, float entityWidth, float entityHeight, PoseStack poseStack) {
        if (fade <= 0.0f || entityWidth <= 0.0f || entityHeight <= 0.0f) {
            return;
        }

        Vector4f transformed = poseStack.last().pose().transform(new Vector4f(0.0f, entityHeight * CENTER_HEIGHT_SCALE, 0.0f, 1.0f));
        Vec3 center = new Vec3(transformed.x, transformed.y, transformed.z);
        double distanceSq = center.lengthSqr();
        if (distanceSq < 1.0e-8 || distanceSq > MAX_DISTANCE_SQ) {
            // Too far to be worth drawing, or the camera is exactly on the center, where the billboard has no
            // orientation at all to derive.
            return;
        }

        // Semi-axes of the world-aligned ellipsoid the shader carves the ring out of, centered at the entity's
        // mid-height.
        float radiusXZ = entityWidth * WIDTH_SCALE;
        float radiusY = entityHeight * HEIGHT_SCALE;

        INSTANCES.add(new AuraInstance(center, radiusXZ, radiusY, radiusXZ * SUPPORT_SCALE, radiusY * SUPPORT_SCALE, fade));
    }

    /**
     * Draws every aura border submitted this frame. Runs after weather so the depth buffer is complete, which is
     * what the shader tests against.
     */
    @SubscribeEvent
    public void onRenderLevelAfterWeather(RenderLevelStageEvent.AfterWeather event) {
        if (INSTANCES.isEmpty()) {
            return;
        }
        try {
            // Farthest first, so overlapping borders blend in the right order - there is no depth write to sort
            // them for us. Which also means the nearest, the ones worth keeping under the cap, are last.
            INSTANCES.sort(Comparator.comparingDouble((AuraInstance aura) -> aura.center().lengthSqr()).reversed());
            List<AuraInstance> drawn = INSTANCES.size() > MAX_INSTANCES ? INSTANCES.subList(INSTANCES.size() - MAX_INSTANCES, INSTANCES.size()) : INSTANCES;

            VolumetricBillboards.draw("Vampirism aura of darkness", ModRenderPipelines.AURA_OF_DARKNESS, event.getModelViewMatrix(), drawn.stream().map(AuraInstance::pack).toList());
        } finally {
            INSTANCES.clear();
        }
    }

    private record AuraInstance(Vec3 center, float radiusXZ, float radiusY, float supportXZ, float supportY, float fade) {

        /**
         * Packs the instance into a matrix, one parameter group per column, matching the layout documented in
         * rendertype_aura_of_darkness.fsh. The last slot is left for VolumetricBillboards to fill in with the
         * resolution the pass ends up drawn at.
         */
        private Matrix4f pack() {
            return new Matrix4f(
                    (float) this.center.x, (float) this.center.y, (float) this.center.z, this.supportXZ,
                    this.radiusXZ, this.radiusY, 0.0f, this.supportY,
                    this.fade, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 0.0f
            );
        }
    }

}
