package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.common.world.entity.GhostEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GhostRenderer extends MobRenderer<GhostEntity, GhostRenderer.GhostRenderState, GhostModel> {

    public static final ResourceLocation TEXTURE = VResourceLocation.mod("textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GhostModel(pContext.bakeLayer(ModEntitiesRender.GHOST)), 0.1f);
    }

    @Override
    public @NotNull GhostRenderState createRenderState() {
        return new GhostRenderState();
    }

    @Override
    public void extractRenderState(@NotNull GhostEntity ghost, @NotNull GhostRenderState renderState, float p_361157_) {
        super.extractRenderState(ghost, renderState, p_361157_);
        renderState.aggressive = ghost.isAggressive();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GhostRenderState pEntity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(@NotNull GhostRenderState pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return super.getRenderType(pLivingEntity, pBodyVisible, true, pGlowing);
    }

    @Override
    protected int getModelTint(GhostRenderState state) {
        if (state.aggressive) {
            return super.getModelTint(state);
        } else {
            return 0x80FFFFFF; // 50% transparency applied to white (ARGB format)
        }
    }

    @Override
    protected void scale(GhostRenderState state, PoseStack stack) {
        super.scale(state, stack);
        stack.scale(0.5f, 0.5f, 0.5f);
        stack.translate(0, 1.5, 0);
    }

    public static class GhostRenderState extends LivingEntityRenderState {
        public boolean aggressive;
    }
}
