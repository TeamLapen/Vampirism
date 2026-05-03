package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.ILevelRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor extends ILevelRenderer {

    @Override
    @Accessor("submitNodeStorage")
    SubmitNodeStorage  vampirism$submitNodeStorage();

    @Override
    @Accessor("entityRenderDispatcher")
    EntityRenderDispatcher vampirism$entityRenderDispatcher();
}
