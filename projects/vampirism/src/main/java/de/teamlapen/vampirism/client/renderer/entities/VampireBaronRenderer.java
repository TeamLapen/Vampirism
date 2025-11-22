package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.BaronBaseModel;
import de.teamlapen.vampirism.client.models.entities.BaronModel;
import de.teamlapen.vampirism.client.models.entities.BaronessModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.BaronAttireLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.WingsLayer;
import de.teamlapen.vampirism.common.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.NotNull;

public class VampireBaronRenderer extends MobRenderer<VampireBaronEntity, VampireBaronRenderer.VampireBaronRenderState, BaronBaseModel> {

    private static final ResourceLocation textureLord = VResourceLocation.mod("textures/entity/baron.png");
    private static final ResourceLocation textureLady = VResourceLocation.mod("textures/entity/baroness.png");
    private static final ResourceLocation textureLordEnraged = VResourceLocation.mod("textures/entity/baron_enraged.png");
    private static final ResourceLocation textureLadyEnraged = VResourceLocation.mod("textures/entity/baroness_enraged.png");

    private final BaronModel baronModel;
    private final BaronessModel baronessModel;

    public VampireBaronRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BaronModel(context.bakeLayer(ModEntitiesRender.BARON)), 0.5F);
        this.baronModel = new BaronModel(context.bakeLayer(ModEntitiesRender.BARON));
        this.baronessModel = new BaronessModel(context.bakeLayer(ModEntitiesRender.BARONESS));
        this.addLayer(new WingsLayer<>(this, context.getModelSet(), vampireBaronEntity -> true, (entity, model) -> model.getBody()));
        this.addLayer(new BaronAttireLayer(this, context, (VampireBaronRenderState state) -> state.isLady ));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull VampireBaronRenderState entity) {
        return entity.isEnraged ? (entity.isLady ? textureLadyEnraged : textureLordEnraged) : (entity.isLady ? textureLady : textureLord);
    }

    @Override
    public void submit(VampireBaronRenderState p_433493_, PoseStack p_434615_, SubmitNodeCollector p_433768_, CameraRenderState p_450931_) {
        this.model = p_433493_.isLady ? baronessModel : baronModel;
        super.submit(p_433493_, p_434615_, p_433768_, p_450931_);
    }

    @Override
    public VampireBaronRenderState createRenderState() {
        return new VampireBaronRenderState();
    }

    @Override
    public void extractRenderState(VampireBaronEntity entity, VampireBaronRenderState state, float p_361157_) {
        super.extractRenderState(entity, state, p_361157_);
        state.isEnraged = entity.isEnraged();
        state.isLady = entity.isLady();
        state.enragedProgress = entity.getEnragedProgress();
    }

    public static class VampireBaronRenderState extends HumanoidRenderState {
        public boolean isEnraged;
        public boolean isLady;
        public float enragedProgress;
        public AnimationState cloakState = new AnimationState();
    }
}