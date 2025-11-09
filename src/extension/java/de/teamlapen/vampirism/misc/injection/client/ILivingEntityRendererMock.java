package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.ILivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.List;

public interface ILivingEntityRendererMock<T extends LivingEntityRenderState, M extends EntityModel<? super T>> extends ILivingEntityRenderer<T, M> {
    @Override
    default List<RenderLayer<T, M>> getLayers() {
        return List.of();
    }
}
