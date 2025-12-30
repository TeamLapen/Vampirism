package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.models.layers.WingsModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class WingsLayer<T extends LivingEntity, S extends HumanoidRenderState, Q extends HumanoidModel<S>> extends RenderLayer<S, Q> {

    private final WingsModel model;
    private final Identifier texture = VResourceLocation.mod("textures/entity/wings/wings.png");

    public WingsLayer(RenderLayerParent<S, Q> renderer, @NotNull EntityModelSet modelSet) {
        super(renderer);
        this.model = new WingsModel(modelSet.bakeLayer(ModEntitiesRender.WINGS));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        if (renderState.isInvisible) return;
        if (renderState.getRenderDataOrDefault(ModEntityRenderStates.DRACULA_WINGS_STATE, IWingsEntity.WingsState.CLOSED) == IWingsEntity.WingsState.CLOSED) return;
        model.resetPose();
        float s = 1f;
//        if (entity instanceof VampireBaronRenderer.VampireBaronRenderState baron) {
//            s = baron.enragedProgress;
//        }
        WingsModel.State state = new WingsModel.State();
        state.wingsState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_STATE);
        state.growState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_GROW);
        state.flyState = renderState.getRenderDataOrThrow(ModEntityRenderStates.DRACULA_WINGS_FLY);
        state.ageInTicks = renderState.ageInTicks;

        poseStack.pushPose();
        poseStack.translate(0,-11/16f,2/16f);
        poseStack.scale(s, s, s);
        nodeCollector.submitModel(model, state, poseStack, RenderTypes.entityCutoutNoCull(texture), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0), -1, null, 0, null);
        poseStack.popPose();
    }
}
