package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import de.teamlapen.vampirism.client.config.ClientConfig;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

/**
 * Renders the volumetric cloud shown instead of the model while an entity is in mist form.
 * <p>
 * Entity renderers cannot draw it themselves: the shader needs the scene depth texture bound as a sampler so it
 * can fade the volume into geometry, and neither {@code RenderSetup} nor a {@code SubmitNodeCollector} can bind a
 * render target's depth view. So renderers only {@linkplain #submitMistCloud submit} their clouds during the
 * normal entity pass, and all of them are drawn together afterwards in {@link #onRenderLevelAfterWeather} through
 * a custom render pass that binds no depth attachment - leaving depth free to sample.
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

    private static final Vector4f NO_COLOR_MODULATION = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f NO_MODEL_OFFSET = new Vector3f();

    private static final List<MistInstance> INSTANCES = new ArrayList<>();
    private static @Nullable GpuBuffer quadBuffer;

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
            RenderPipeline pipeline = ModRenderPipelines.mist(ModConfig.client().volumetricMistQuality.get());
            if (pipeline == null) {
                return;
            }

            // Under fabulous graphics this stage draws into the separate weather target, as WorldBorderRenderer
            // does right after us; anywhere else it is the main target.
            RenderTarget weather = Minecraft.getInstance().levelRenderer.getWeatherTarget();
            RenderTarget target = weather != null ? weather : Minecraft.getInstance().getMainRenderTarget();
            GpuTextureView color = target.getColorTextureView();
            GpuTextureView depth = target.getDepthTextureView();
            if (color == null || depth == null) {
                return;
            }

            // Farthest first, so overlapping clouds blend in the right order - there is no depth write to sort
            // them for us.
            INSTANCES.sort(Comparator.comparingDouble((MistInstance mist) -> mist.center().lengthSqr()).reversed());

            GpuBuffer quad = quadBuffer();
            RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
            GpuBuffer indexBuffer = indices.getBuffer(6);

            // Every instance's uniforms are written up front, before the pass is opened - the ring buffer they
            // land in must not be written to while a pass is recording. The per-instance parameters ride in the
            // texture matrix slot of the standard per-draw uniform, so this pass needs no GPU buffer of its own.
            // See rendertype_mist.fsh for the layout.
            Matrix4fc modelView = event.getModelViewMatrix();
            DynamicUniforms.Transform[] transforms = INSTANCES.stream()
                    .map(mist -> new DynamicUniforms.Transform(modelView, NO_COLOR_MODULATION, NO_MODEL_OFFSET, mist.pack()))
                    .toArray(DynamicUniforms.Transform[]::new);
            GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(transforms);

            try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Vampirism mist", color, OptionalInt.empty())) {
                pass.setPipeline(pipeline);
                RenderSystem.bindDefaultUniforms(pass);
                pass.bindTexture("DepthSampler", depth, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                pass.setVertexBuffer(0, quad);
                pass.setIndexBuffer(indexBuffer, indices.type());

                for (GpuBufferSlice uniform : uniforms) {
                    pass.setUniform("DynamicTransforms", uniform);
                    pass.drawIndexed(0, 0, 6, 1);
                }
            }
        } finally {
            INSTANCES.clear();
        }
    }

    /**
     * A unit quad spanning -1 to 1; the vertex shader scales and orients it per instance.
     */
    private static GpuBuffer quadBuffer() {
        if (quadBuffer == null) {
            VertexFormat format = DefaultVertexFormat.POSITION_TEX;
            try (ByteBufferBuilder allocator = ByteBufferBuilder.exactlySized(format.getVertexSize() * 4)) {
                BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.Mode.QUADS, format);
                builder.addVertex(-1.0f, -1.0f, 0.0f).setUv(0.0f, 0.0f);
                builder.addVertex(1.0f, -1.0f, 0.0f).setUv(1.0f, 0.0f);
                builder.addVertex(1.0f, 1.0f, 0.0f).setUv(1.0f, 1.0f);
                builder.addVertex(-1.0f, 1.0f, 0.0f).setUv(0.0f, 1.0f);
                try (MeshData mesh = builder.buildOrThrow()) {
                    quadBuffer = RenderSystem.getDevice().createBuffer(() -> "Vampirism mist quad", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
                }
            }
        }
        return quadBuffer;
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

    /**
     * @return whether mist should be rendered at all; when disabled, callers hide the entity instead.
     */
    public static boolean isEnabled() {
        return ModConfig.client().volumetricMistQuality.get() != ClientConfig.MistQuality.OFF;
    }
}
