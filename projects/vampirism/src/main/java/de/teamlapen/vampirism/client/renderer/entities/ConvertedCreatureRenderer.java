package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.common.world.entity.converted.ConvertedCreatureEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a converted creature, by rendering its old creature
 */
public class ConvertedCreatureRenderer extends EntityRenderer<ConvertedCreatureEntity<?>, ConvertedCreatureRenderer.ConvertedCreateRenderState> { // RawType because of ConvertedCreatureEntity#IMob
    public static boolean renderOverlay = false;

    public ConvertedCreatureRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @Override
    public void submit(ConvertedCreateRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.renderState == null) return;
        renderOverlay = true;
        this.entityRenderDispatcher.submit(renderState.renderState, cameraRenderState, 0, 0, 0, poseStack, nodeCollector);
        renderOverlay = false;

    }

    @Override
    public void extractRenderState(@NotNull ConvertedCreatureEntity<?> entity, @NotNull ConvertedCreateRenderState state, float p_362204_) {
        super.extractRenderState(entity, state, p_362204_);
        state.renderState = entity.getOldCreature().map(oldEntity -> {
            PathfinderMob pathfinderMob = oldEntity;
            var renderer = this.entityRenderDispatcher.getRenderer(pathfinderMob);
            return renderer.createRenderState(pathfinderMob, p_362204_);
        }).orElse(null);
    }

    @Override
    public @NotNull ConvertedCreateRenderState createRenderState() {
        return new ConvertedCreateRenderState();
    }

    public static class ConvertedCreateRenderState extends EntityRenderState {
        @Nullable
        public EntityRenderState renderState;
    }
}
