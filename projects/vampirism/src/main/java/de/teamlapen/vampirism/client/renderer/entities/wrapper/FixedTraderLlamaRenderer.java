package de.teamlapen.vampirism.client.renderer.entities.wrapper;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FixedTraderLlamaRenderer extends net.minecraft.client.renderer.entity.LlamaRenderer {

    public FixedTraderLlamaRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.TRADER_LLAMA, ModelLayers.TRADER_LLAMA_BABY);
    }
}
