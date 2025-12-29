package de.teamlapen.vampirism.client.renderer.bloodvision;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public class NoOpOutlineBufferSource extends OutlineBufferSource {

    private final VertexConsumer consumer = new NoOpVertexConsumer();

    @Override
    public VertexConsumer getBuffer(RenderType p_458937_) {
        return this.consumer;
    }
}
