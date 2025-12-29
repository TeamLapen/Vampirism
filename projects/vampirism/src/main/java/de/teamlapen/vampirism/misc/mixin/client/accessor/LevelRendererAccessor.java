package de.teamlapen.vampirism.misc.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.misc.extension.client.ILevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor extends ILevelRenderer {

    @Override
    @Accessor("submitNodeStorage")
    SubmitNodeStorage  vampirism$submitNodeStorage();

    @Override
    @Accessor("entityRenderDispatcher")
    EntityRenderDispatcher vampirism$entityRenderDispatcher();
}
