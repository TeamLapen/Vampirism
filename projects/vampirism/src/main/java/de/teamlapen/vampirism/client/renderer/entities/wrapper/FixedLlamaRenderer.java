package de.teamlapen.vampirism.client.renderer.entities.wrapper;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FixedLlamaRenderer extends net.minecraft.client.renderer.entity.LlamaRenderer {

    public FixedLlamaRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY);
    }
}
