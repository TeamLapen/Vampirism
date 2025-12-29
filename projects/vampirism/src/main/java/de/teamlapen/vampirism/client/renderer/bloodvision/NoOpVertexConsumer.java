package de.teamlapen.vampirism.client.renderer.bloodvision;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class NoOpVertexConsumer implements VertexConsumer {

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float p_456188_) {
        return this;
    }
}
