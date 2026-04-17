package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

import java.util.function.BiFunction;

public abstract class DualSplitBipedRenderer<TEntity extends Mob,TRenderState extends AvatarLikeRenderState, TModel extends HumanoidModel<TRenderState>> extends DualBipedRenderer<TEntity,TRenderState,TModel> {

    private final TModel headModelWide;
    private final TModel headModelSlim;
    private final boolean renderCustom;

    public DualSplitBipedRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayer, ModelLayerLocation slimModelLayer, BiFunction<ModelPart, Boolean, TModel> modelFactory, float shadowSize) {
        super(context, modelFactory.apply(context.bakeLayer(modelLayer), false), modelFactory.apply(context.bakeLayer(slimModelLayer), true), shadowSize);

        this.renderCustom = splitRenderingEnabled();

        this.headModelWide = modelFactory.apply(context.bakeLayer(modelLayer), false);
        this.headModelSlim = modelFactory.apply(context.bakeLayer(slimModelLayer), true);

        if (this.renderCustom) {
            setHeadVisibility(this.headModelWide, true);
            setHeadVisibility(this.headModelSlim, true);
            setHeadVisibility(this.wideModel, false);
            setHeadVisibility(this.tallModel, false);
        }
    }

    protected abstract boolean splitRenderingEnabled();

    private void setHeadVisibility(TModel model, boolean visible) {
        model.allParts().forEach(part -> part.visible = !visible);
        model.head.visible = visible;
        model.hat.visible = visible;
    }

    @Override
    public final Identifier getTextureLocation(TRenderState renderState) {
        return getTexture(renderState, renderState.getRenderDataOrDefault(ModEntityRenderStates.SPLIT_RENDER_PART, RenderPart.DEFAULT));
    }

    protected abstract Identifier getTexture(TRenderState renderState, RenderPart part);

    //<editor-fold desc="Model Provider">

    @Override
    protected TModel provideModel(TRenderState renderState) {
        return switch (renderState.getRenderDataOrDefault(ModEntityRenderStates.SPLIT_RENDER_PART, RenderPart.DEFAULT)) {
            case HEAD -> provideHeadModel(renderState);
            case BODY -> provideBodyModel(renderState);
            default -> provideDefaultModel(renderState);
        };
    }

    protected TModel provideDefaultModel(TRenderState renderState) {
        return super.provideModel(renderState);
    }

    protected TModel provideHeadModel(TRenderState renderState) {
        return switch (renderState.skin.model()) {
            case SLIM -> headModelSlim;
            case WIDE -> headModelWide;
        };
    }

    protected TModel provideBodyModel(TRenderState renderState) {
        return super.provideModel(renderState);
    }

    //</editor-fold>


    @Override
    public void submit(TRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (this.renderCustom) {
            renderState.setRenderData(ModEntityRenderStates.SPLIT_RENDER_PART, RenderPart.BODY);
            super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
            renderState.setRenderData(ModEntityRenderStates.SPLIT_RENDER_PART, RenderPart.HEAD);
            super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        } else {
            super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    public enum RenderPart {
        HEAD, BODY, DEFAULT
    }
}
