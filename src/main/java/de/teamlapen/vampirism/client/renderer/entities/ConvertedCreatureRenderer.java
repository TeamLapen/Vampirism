package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.common.entity.converted.ConvertedCreatureEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

/**
 * Renders a converted creature, by rendering its old creature
 */
public class ConvertedCreatureRenderer extends EntityRenderer<ConvertedCreatureEntity<?>, ConvertedCreatureRenderer.ConvertedCreateRenderState> { // RawType because of ConvertedCreatureEntity#IMob
    public static boolean renderOverlay = false;

    public ConvertedCreatureRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @Override
    public void render(ConvertedCreateRenderState state, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (state.oldEntity == null) return;
        renderOverlay = true;
        this.entityRenderDispatcher.render(state.oldEntity, 0,0,0,0,stack, bufferSource, packedLight);
        renderOverlay = false;
        super.render(state, stack, bufferSource, packedLight);
    }

    @Override
    public void extractRenderState(@NotNull ConvertedCreatureEntity<?> entity, @NotNull ConvertedCreateRenderState state, float p_362204_) {
        super.extractRenderState(entity, state, p_362204_);
        state.oldEntity = entity.getOldCreature().orElse(null);
    }

    @Override
    public @NotNull ConvertedCreateRenderState createRenderState() {
        return new ConvertedCreateRenderState();
    }

    public static class ConvertedCreateRenderState extends EntityRenderState {
        public PathfinderMob oldEntity;
    }
}
