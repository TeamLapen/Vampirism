package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.ILivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor<T extends LivingEntityRenderState, M extends EntityModel<? super T>> extends ILivingEntityRenderer<T, M> {

    @Override
    @Accessor("layers")
    List<RenderLayer<T, M>> getLayers();
}
