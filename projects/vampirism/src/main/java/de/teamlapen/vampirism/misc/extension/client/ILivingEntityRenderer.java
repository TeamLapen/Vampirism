package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.List;

public interface ILivingEntityRenderer<T extends LivingEntityRenderState, M extends EntityModel<? super T>> {
    List<RenderLayer<T, M>> getLayers();

}
