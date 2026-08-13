package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public interface IItemStackRenderState {

    ItemStackRenderState.LayerRenderState[] vampirism$layers();

    int vampirism$activeLayerCount();
}
