package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaModel;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase1Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase2Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase3Model;
import de.teamlapen.vampirism.client.renderer.entities.layers.DraculaItemInHandLayer;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;

public class DraculaRenderer extends LivingEntityRenderer<Dracula, DraculaRenderer.DraculaRenderState, DraculaModel> {

    private static final Identifier TEXTURE_PHASE_1 = VIdentifier.mod("textures/entity/dracula/phase1.png");
    private static final Identifier TEXTURE_PHASE_2 = VIdentifier.mod("textures/entity/dracula/phase2.png");
    private static final Identifier TEXTURE_PHASE_3 = VIdentifier.mod("textures/entity/dracula/phase3.png");

    private final DraculaModel phase1Model;
    private final DraculaModel phase2Model;
    private final DraculaModel phase3Model;

    public DraculaRenderer(EntityRendererProvider.Context context) {
        super(context, new DraculaPhase1Model(context.getModelSet().bakeLayer(ModEntitiesRender.DRACULA_PHASE_1)), 0.5f);
        this.phase1Model = this.model;
        this.phase2Model = new DraculaPhase2Model(context.getModelSet().bakeLayer(ModEntitiesRender.DRACULA_PHASE_2));
        this.phase3Model = new DraculaPhase3Model(context.getModelSet().bakeLayer(ModEntitiesRender.DRACULA_PHASE_3));
        this.addLayer(new DraculaItemInHandLayer(this));
    }

    @Override
    public void submit(DraculaRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        this.model = switch (renderState.state) {
            case PHASE_2 -> phase2Model;
            case PHASE_3 -> phase3Model;
            default -> phase1Model;
        };
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    }



    @Override
    protected void scale(DraculaRenderState renderState, PoseStack poseStack) {
        poseStack.scale(0.62f, 0.62f, 0.62f);
    }

    @Override
    public Identifier getTextureLocation(DraculaRenderState renderState) {
        return switch (renderState.state) {
            case PHASE_2 -> TEXTURE_PHASE_2;
            case PHASE_3 -> TEXTURE_PHASE_3;
            default -> TEXTURE_PHASE_1;
        };
    }

    @Override
    public DraculaRenderState createRenderState() {
        return new DraculaRenderState();
    }

    @Override
    public void extractRenderState(Dracula dracula, DraculaRenderState renderState, float partialTicks) {
        super.extractRenderState(dracula, renderState, partialTicks);
        ArmedEntityRenderState.extractArmedEntityRenderState(dracula, renderState, this.itemModelResolver, partialTicks);
        renderState.state = dracula.getStage();
        renderState.speedValue = (float) dracula.getDeltaMovement().lengthSqr();
        dracula.copyAttackAnimationTo(renderState.attackAnimation);
        renderState.attackAnimationType = dracula.getAttackAnimationType();
    }

    public static class DraculaRenderState extends ArmedEntityRenderState {
        public float speedValue = 1.0F;

        public FightStage state = FightStage.NONE;
        public IDraculaAnimations.Animation attackAnimationType = IDraculaAnimations.Animation.NONE;
        public final AnimationState attackAnimation = new AnimationState();
    }
}
