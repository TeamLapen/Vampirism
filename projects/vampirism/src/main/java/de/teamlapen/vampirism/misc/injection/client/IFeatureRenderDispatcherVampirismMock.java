package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IFeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;

@Deprecated
public interface IFeatureRenderDispatcherVampirismMock extends IFeatureRenderDispatcher {
    @Override
    default ModelPartFeatureRenderer vampirism$modelPartFeatureRenderer() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default ModelFeatureRenderer vampirism$modelFeatureFeatureRenderer() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
