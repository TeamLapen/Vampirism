package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaModel;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase1Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase2Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase3Model;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.renderer.MistRenderer;
import de.teamlapen.vampirism.client.renderer.MistStateTracker;
import de.teamlapen.vampirism.client.renderer.entities.layers.DraculaItemInHandLayer;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

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
        if (renderState.draculaState == DraculaState.MIST) {
            if (MistRenderer.isEnabled()) {
                MistRenderer.submitMistCloud(mistFadeFactor(renderState), renderState.boundingBoxWidth, renderState.boundingBoxHeight, renderState.velocity, renderState.mistFlow, poseStack);
            }
            return;
        }
        this.model = switch (renderState.state) {
            case PHASE_2 -> phase2Model;
            case PHASE_3 -> phase3Model;
            default -> phase1Model;
        };
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        if (!renderState.siphonTargets.isEmpty()) {
            submitSiphonBeams(renderState, poseStack, nodeCollector);
        }
    }

    private static final float MIST_FADE_MS = 300.0f;

    /**
     * 0 before/after mist form; ramps 0-1 over the first {@link #MIST_FADE_MS}, holds at 1, then ramps back down
     * over the last {@link #MIST_FADE_MS} before {@link Dracula#MIST_DURATION} elapses.
     */
    private static float mistFadeFactor(DraculaRenderState renderState) {
        if (!renderState.mistAnimation.isStarted()) {
            return 0.0f;
        }
        float elapsedMs = renderState.mistAnimation.getTimeInMillis(renderState.ageInTicks);
        if (elapsedMs < 0.0f) {
            return 0.0f;
        }
        float totalMs = Dracula.MIST_DURATION * 50.0f;
        float remainingMs = totalMs - elapsedMs;
        float fadeIn = Mth.clamp(elapsedMs / MIST_FADE_MS, 0.0f, 1.0f);
        float fadeOut = Mth.clamp(remainingMs / MIST_FADE_MS, 0.0f, 1.0f);
        return Math.min(fadeIn, fadeOut);
    }

    /**
     * Renders one blood streak beam (two crossed ribbons) per siphon victim. The v coordinate carries the distance
     * along the beam in blocks for the streak animation in the shader.
     */
    private void submitSiphonBeams(DraculaRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        nodeCollector.submitCustomGeometry(poseStack, ModRenderPipelines.BLOOD_SIPHON_RENDER_TYPE, (pose, consumer) -> {
            Matrix4f matrix = pose.pose();
            Vec3 origin = new Vec3(0, 2.4, 0);
            for (Vec3 target : renderState.siphonTargets) {
                Vec3 delta = target.subtract(origin);
                float length = (float) delta.length();
                if (length < 0.1f) continue;
                Vec3 direction = delta.scale(1.0 / length);
                Vec3 side = direction.cross(new Vec3(0, 1, 0));
                side = side.lengthSqr() < 1.0e-4 ? new Vec3(1, 0, 0) : side.normalize();
                Vec3 up = direction.cross(side).normalize();
                float width = 0.18f;
                addBeamRibbon(consumer, matrix, origin, target, side.scale(width), length);
                addBeamRibbon(consumer, matrix, origin, target, up.scale(width), length);
            }
        });
    }

    private static void addBeamRibbon(VertexConsumer consumer, Matrix4f matrix, Vec3 from, Vec3 to, Vec3 half, float length) {
        consumer.addVertex(matrix, (float) (from.x - half.x), (float) (from.y - half.y), (float) (from.z - half.z)).setUv(0, 0);
        consumer.addVertex(matrix, (float) (from.x + half.x), (float) (from.y + half.y), (float) (from.z + half.z)).setUv(1, 0);
        consumer.addVertex(matrix, (float) (to.x + half.x), (float) (to.y + half.y), (float) (to.z + half.z)).setUv(1, length);
        consumer.addVertex(matrix, (float) (to.x - half.x), (float) (to.y - half.y), (float) (to.z - half.z)).setUv(0, length);
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
        renderState.draculaState = dracula.getState();
        renderState.speedValue = (float) dracula.getDeltaMovement().lengthSqr();
        // From the tracker rather than getDeltaMovement(), which reads as roughly zero for a server-driven mob.
        MistStateTracker mistTracker = VampirismModClient.services().mistStateTracker();
        renderState.velocity = mistTracker.getVelocity(dracula);
        renderState.mistFlow = mistTracker.getFlow(dracula);
        dracula.copyAttackAnimationTo(renderState.attackAnimation);
        dracula.copyTransformationAnimationTo(renderState.transformationAnimation);
        dracula.copyMistAnimationTo(renderState.mistAnimation);
        renderState.attackAnimationType = dracula.getAttackAnimationType();
        renderState.siphonTargets.clear();
        for (int targetId : dracula.getSiphonTargets()) {
            Entity target = dracula.level().getEntity(targetId);
            if (target != null && target.isAlive()) {
                Vec3 offset = target.getPosition(partialTicks).add(0, target.getBbHeight() * 0.5, 0).subtract(dracula.getPosition(partialTicks));
                renderState.siphonTargets.add(offset);
            }
        }
    }

    public static class DraculaRenderState extends ArmedEntityRenderState {
        public float speedValue = 1.0F;
        public Vec3 velocity = Vec3.ZERO;
        /** Accumulated drift of the mist noise field; see MistStateTracker. */
        public Vec3 mistFlow = Vec3.ZERO;

        public FightStage state = FightStage.NONE;
        public DraculaState draculaState = DraculaState.DEFAULT;
        public IDraculaAnimations.Animation attackAnimationType = IDraculaAnimations.Animation.NONE;
        public final AnimationState attackAnimation = new AnimationState();
        public final AnimationState transformationAnimation = new AnimationState();
        public final AnimationState mistAnimation = new AnimationState();
        /** Offsets from Dracula to each blood siphon victim; empty while not channeling. */
        public final List<Vec3> siphonTargets = new ArrayList<>();
    }
}
