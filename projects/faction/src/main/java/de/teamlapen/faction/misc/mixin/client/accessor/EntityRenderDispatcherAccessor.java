package de.teamlapen.faction.misc.mixin.client.accessor;

import de.teamlapen.faction.misc.extensions.client.IEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor extends IEntityRenderDispatcher {

    @Override
    @Accessor("renderers")
    Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers();
}
