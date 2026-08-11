package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.OptionalInt;

/**
 * The draw shared by every raymarched volume in the mod - mist form and the aura of darkness.
 * <p>
 * Entity renderers cannot draw these themselves: the shaders need the scene depth texture bound as a sampler so
 * they can fade the volume into geometry, and neither {@code RenderSetup} nor a {@code SubmitNodeCollector} can
 * bind a render target's depth view. So renderers only collect their volumes during the normal entity pass and
 * hand them here afterwards, to be drawn through a custom render pass that binds no depth attachment - leaving
 * depth free to sample.
 * <p>
 * Every volume is a camera-facing quad bounding an ellipsoid, expanded from a shared unit quad by
 * {@code volumetric_billboard.vsh}, with its per-instance parameters riding in the texture matrix slot of the
 * standard per-draw uniform. Each fragment shader documents its own layout for those.
 * <p>
 * These are raymarches covering a large part of the screen at close range, so the pass is drawn at half
 * resolution by default and composited back up. A volume is soft and low-frequency by nature, which is what
 * makes that survive: the only place the missing resolution shows is where it meets geometry, and the shaders
 * already taper density to nothing there. {@link #HALF_RESOLUTION_SCALE} is the one place the two halves of
 * that agree.
 */
public class VolumetricBillboards {

    private static final Vector4f NO_COLOR_MODULATION = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f NO_MODEL_OFFSET = new Vector3f();

    /**
     * Both the linear factor the offscreen target is smaller by and the number the fragment shaders multiply
     * gl_FragCoord by to recover a full-screen UV for the depth lookup. The target is sized by rounding up, so
     * that product never overshoots 1 on an odd-sized window.
     */
    private static final int HALF_RESOLUTION_SCALE = 2;

    private static @Nullable GpuBuffer quadBuffer;
    private static @Nullable TextureTarget reducedTarget;

    /**
     * Draws one instance per entry of {@code instances}, in the order given - which callers are expected to have
     * sorted back to front, since these passes do not write depth and so have nothing to sort them.
     *
     * @param label     names the pass in GPU debug captures
     * @param pipeline  the volume's pipeline, which must declare {@code DepthSampler} and {@code NoiseSampler}
     *                  and no depth stencil state
     * @param modelView the frame's model view matrix
     * @param instances per-instance parameters, packed as documented by the pipeline's fragment shader. The
     *                  resolution scale in m33 is filled in here, since only this class knows the pass size.
     */
    public static void draw(String label, RenderPipeline pipeline, Matrix4fc modelView, List<Matrix4f> instances) {
        if (instances.isEmpty()) return;

        // Under fabulous graphics the stage these are drawn in targets the separate weather target, as
        // WorldBorderRenderer does right after; anywhere else it is the main target.
        RenderTarget weather = Minecraft.getInstance().levelRenderer.getWeatherTarget();
        RenderTarget scene = weather != null ? weather : Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView sceneColor = scene.getColorTextureView();
        GpuTextureView sceneDepth = scene.getDepthTextureView();
        if (sceneColor == null || sceneDepth == null) return;

        TextureTarget reduced = reducedTarget(scene);
        GpuTextureView colorTarget = reduced.getColorTextureView();
        if (colorTarget == null) return;

        GpuBuffer quad = unitQuad();
        RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer indexBuffer = indices.getBuffer(6);
        SamplerCache samplers = RenderSystem.getSamplerCache();
        // Resolved before the pass opens, not at the bind below: the first call builds and uploads the sheet,
        // and a texture write is one of the commands the encoder refuses to record mid-pass.
        GpuTextureView noise = VolumetricNoise.view();

        // Every instance's uniforms are written up front, before the pass is opened - the ring buffer they land
        // in must not be written to while a pass is recording. Riding in the texture matrix slot of the standard
        // per-draw uniform means this pass needs no GPU buffer of its own.
        DynamicUniforms.Transform[] transforms = instances.stream().map(params -> new DynamicUniforms.Transform(modelView, NO_COLOR_MODULATION, NO_MODEL_OFFSET, params.m33((float) HALF_RESOLUTION_SCALE))).toArray(DynamicUniforms.Transform[]::new);
        GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(transforms);

        // Cleared to transparent black, which is the identity the premultiplied accumulation below builds on -
        // volumes overlapping inside the offscreen target blend against each other exactly as they would have
        // against the scene, so compositing the result once is equivalent to having drawn them straight.
        OptionalInt clear = OptionalInt.of(0);

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> label, colorTarget, clear)) {
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.bindTexture("DepthSampler", sceneDepth, samplers.getClampToEdge(FilterMode.NEAREST));
            // Repeat and linear are both load-bearing: the sheet is sampled at unbounded coordinates as the
            // noise drifts, and the filtering is the interpolation that makes it noise at all.
            pass.bindTexture("NoiseSampler", noise, samplers.getRepeat(FilterMode.LINEAR));
            pass.setVertexBuffer(0, quad);
            pass.setIndexBuffer(indexBuffer, indices.type());

            for (GpuBufferSlice uniform : uniforms) {
                pass.setUniform("DynamicTransforms", uniform);
                pass.drawIndexed(0, 0, 6, 1);
            }
        }

        composite(label, reduced, sceneColor, samplers);
    }

    /**
     * Upscales the reduced-resolution result over the scene. Linear filtering is all the reconstruction a volume
     * this soft needs, and the blend is premultiplied to match what the target holds.
     */
    private static void composite(String label, TextureTarget reduced, GpuTextureView sceneColor, SamplerCache samplers) {
        GpuTextureView reducedColor = reduced.getColorTextureView();
        if (reducedColor == null) {
            return;
        }
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> label + " composite", sceneColor, OptionalInt.empty())) {
            pass.setPipeline(ModRenderPipelines.VOLUMETRIC_COMPOSITE);
            RenderSystem.bindDefaultUniforms(pass);
            pass.bindTexture("InSampler", reducedColor, samplers.getClampToEdge(FilterMode.LINEAR));
            // Vanilla's screenquad vertex shader builds a full-screen triangle from gl_VertexID alone, so this
            // needs no vertex buffer.
            pass.draw(0, 3);
        }
    }

    /**
     * The offscreen target the volumes are marched into, resized to follow the window. Rounded up so a full-res
     * pixel always maps inside it.
     */
    private static TextureTarget reducedTarget(RenderTarget scene) {
        int width = Math.max(1, (scene.width + HALF_RESOLUTION_SCALE - 1) / HALF_RESOLUTION_SCALE);
        int height = Math.max(1, (scene.height + HALF_RESOLUTION_SCALE - 1) / HALF_RESOLUTION_SCALE);
        if (reducedTarget == null) {
            reducedTarget = new TextureTarget("Vampirism volumetric half resolution", width, height, false);
        } else if (reducedTarget.width != width || reducedTarget.height != height) {
            reducedTarget.resize(width, height);
        }
        return reducedTarget;
    }

    /**
     * A unit quad spanning -1 to 1; the vertex shader scales and orients it per instance. It carries no texture
     * coordinates - the fragment shaders derive everything from the interpolated position, which they need for
     * the ray anyway.
     */
    private static GpuBuffer unitQuad() {
        if (quadBuffer == null) {
            VertexFormat format = DefaultVertexFormat.POSITION;
            try (ByteBufferBuilder allocator = ByteBufferBuilder.exactlySized(format.getVertexSize() * 4)) {
                BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.Mode.QUADS, format);
                builder.addVertex(-1.0f, -1.0f, 0.0f);
                builder.addVertex(1.0f, -1.0f, 0.0f);
                builder.addVertex(1.0f, 1.0f, 0.0f);
                builder.addVertex(-1.0f, 1.0f, 0.0f);
                try (MeshData mesh = builder.buildOrThrow()) {
                    quadBuffer = RenderSystem.getDevice().createBuffer(() -> "Vampirism volumetric billboard quad", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
                }
            }
        }
        return quadBuffer;
    }
}
