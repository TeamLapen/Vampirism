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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import org.jetbrains.annotations.Nullable;
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
 * Every volume is a camera-facing quad bounding a sphere, expanded from a shared unit quad by
 * {@code volumetric_billboard.vsh}, with its per-instance parameters riding in the texture matrix slot of the
 * standard per-draw uniform. Each fragment shader documents its own layout for those.
 */
public class VolumetricBillboards {

    private static final Vector4f NO_COLOR_MODULATION = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f NO_MODEL_OFFSET = new Vector3f();

    private static @Nullable GpuBuffer quadBuffer;

    /**
     * Draws one instance per entry of {@code instances}, in the order given - which callers are expected to have
     * sorted back to front, since these passes do not write depth and so have nothing to sort them.
     *
     * @param label     names the pass in GPU debug captures
     * @param pipeline  the volume's pipeline, which must declare a {@code DepthSampler} and no depth stencil state
     * @param modelView the frame's model view matrix
     * @param instances per-instance parameters, packed as documented by the pipeline's fragment shader
     */
    public static void draw(String label, RenderPipeline pipeline, Matrix4fc modelView, List<? extends Matrix4fc> instances) {
        if (instances.isEmpty()) {
            return;
        }

        // Under fabulous graphics the stage these are drawn in targets the separate weather target, as
        // WorldBorderRenderer does right after; anywhere else it is the main target.
        RenderTarget weather = Minecraft.getInstance().levelRenderer.getWeatherTarget();
        RenderTarget target = weather != null ? weather : Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        GpuTextureView depth = target.getDepthTextureView();
        if (color == null || depth == null) {
            return;
        }

        GpuBuffer quad = unitQuad();
        RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer indexBuffer = indices.getBuffer(6);

        // Every instance's uniforms are written up front, before the pass is opened - the ring buffer they land
        // in must not be written to while a pass is recording. Riding in the texture matrix slot of the standard
        // per-draw uniform means this pass needs no GPU buffer of its own.
        DynamicUniforms.Transform[] transforms = instances.stream()
                .map(params -> new DynamicUniforms.Transform(modelView, NO_COLOR_MODULATION, NO_MODEL_OFFSET, params))
                .toArray(DynamicUniforms.Transform[]::new);
        GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(transforms);

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> label, color, OptionalInt.empty())) {
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
    }

    /**
     * A unit quad spanning -1 to 1; the vertex shader scales and orients it per instance.
     */
    private static GpuBuffer unitQuad() {
        if (quadBuffer == null) {
            VertexFormat format = DefaultVertexFormat.POSITION_TEX;
            try (ByteBufferBuilder allocator = ByteBufferBuilder.exactlySized(format.getVertexSize() * 4)) {
                BufferBuilder builder = new BufferBuilder(allocator, VertexFormat.Mode.QUADS, format);
                builder.addVertex(-1.0f, -1.0f, 0.0f).setUv(0.0f, 0.0f);
                builder.addVertex(1.0f, -1.0f, 0.0f).setUv(1.0f, 0.0f);
                builder.addVertex(1.0f, 1.0f, 0.0f).setUv(1.0f, 1.0f);
                builder.addVertex(-1.0f, 1.0f, 0.0f).setUv(0.0f, 1.0f);
                try (MeshData mesh = builder.buildOrThrow()) {
                    quadBuffer = RenderSystem.getDevice().createBuffer(() -> "Vampirism volumetric billboard quad", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
                }
            }
        }
        return quadBuffer;
    }
}
