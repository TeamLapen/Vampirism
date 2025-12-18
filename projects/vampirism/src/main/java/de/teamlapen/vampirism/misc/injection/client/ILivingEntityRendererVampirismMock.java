package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.ILivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.List;

@Deprecated
public interface ILivingEntityRendererVampirismMock<T extends LivingEntityRenderState, M extends EntityModel<? super T>> extends ILivingEntityRenderer<T, M> {
    @Override
    default List<RenderLayer<T, M>> getLayers() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
