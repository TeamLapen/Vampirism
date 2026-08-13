package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.IItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor extends IItemStackRenderState {

    @Accessor("layers")
    @Override
    ItemStackRenderState.LayerRenderState[] vampirism$layers();

    @Accessor("activeLayerCount")
    @Override
    int vampirism$activeLayerCount();
}
