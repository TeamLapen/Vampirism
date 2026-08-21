package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.models.layers.WingsModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.function.BiConsumer;

public class WingsLayer<T extends LivingEntity, S extends LivingEntityRenderState, Q extends EntityModel<S>> extends RenderLayer<S, Q> {

    private final WingsModel model;
    private final BiConsumer<S, PoseStack> attachmentPointModifier;

    public WingsLayer(RenderLayerParent<S, Q> renderer, EntityModelSet modelSet, BiConsumer<S, PoseStack> attachmentPointModifier) {
        super(renderer);
        this.model = new WingsModel(modelSet.bakeLayer(ModEntitiesRender.WINGS));
        this.attachmentPointModifier = attachmentPointModifier;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        if (renderState.isInvisible) return;
        if (renderState.getRenderDataOrDefault(ModEntityRenderStates.DRACULA_WINGS_STATE, IWingsEntity.WingsState.CLOSED) == IWingsEntity.WingsState.CLOSED) return;
        float s = 1f;
        WingsModel.State state = new WingsModel.State();
        state.wingsState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_STATE);
        state.growState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_GROW);
        state.flyState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_FLY);
        state.ageInTicks = renderState.ageInTicks;

        poseStack.pushPose();
        this.attachmentPointModifier.accept(renderState, poseStack);
        poseStack.scale(s, s, s);
        nodeCollector.submitModel(this.model, state, poseStack, RenderTypes.entityCutout(renderState.getRenderDataOrDefault(ModEntityRenderStates.DRACULA_WINGS_TEXTURE, IWingsEntity.Texture.DEFAULT).texture), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0), -1, null, 0, null);
        poseStack.popPose();
    }
}
