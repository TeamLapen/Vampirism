package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.IFeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FeatureRenderDispatcher.class)
public interface FeatureRenderDispatcherAccessor extends IFeatureRenderDispatcher {

    @Accessor("modelPartFeatureRenderer")
    @Override
    ModelPartFeatureRenderer vampirism$modelPartFeatureRenderer();

    @Accessor("modelFeatureRenderer")
    @Override
    ModelFeatureRenderer vampirism$modelFeatureFeatureRenderer();
}
