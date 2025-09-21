package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.client.renderer.screen.BloodVisionRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CustomBufferSource extends MultiBufferSource.BufferSource {
    private static final Set<String> entityRenderTypes = Set.of("entity_translucent", "entity_translucent_emissive", "entity_smooth_cutout", "entity_cutout_no_cull", "entity_cutout_no_cull_z_offset","entity_solid_z_offset_forward","entity_solid", "entity_no_outline" );
    private int teamR = 255;
    private int teamG = 255;
    private int teamB = 255;
    private int teamA = 255;

    public CustomBufferSource(BufferSource bufferSource) {
        super(bufferSource.sharedBuffer, bufferSource.fixedBuffers);
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        if (!entityRenderTypes.contains(renderType.name)) {
            return new NoOpGenerator();
        }
        return new OutlineGenerator(super.getBuffer(BloodVisionRenderer.SOLID_TRANSPARENCY_ENTITY), teamR, teamG, teamB, teamA);
    }

    public void setColor(int red, int green, int blue, int alpha) {
        this.teamR = red;
        this.teamG = green;
        this.teamB = blue;
        this.teamA = alpha;
    }

    public void normalize(float percentage) {
        this.teamA = (int) ( this.teamA - (this.teamA * percentage));
    }

    public record OutlineGenerator(VertexConsumer consumer, int color) implements VertexConsumer {


        public OutlineGenerator(VertexConsumer vertexConsumer, int r, int g, int b, int a) {
            this(vertexConsumer, ARGB.color(a, r, g, b));
        }

        @Override
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            this.consumer.addVertex(x, y, z).setColor(color);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float u, float v) {
            this.consumer.setUv(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int u, int v) {
            this.consumer.setUv1(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int u, int v) {
            this.consumer.setUv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            this.consumer.setNormal(normalX, normalY, normalZ);
            return this;
        }
    }

    public record NoOpGenerator() implements VertexConsumer {

        @Override
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float u, float v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            return this;
        }
    }
}
