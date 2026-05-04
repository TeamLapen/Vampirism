package de.teamlapen.vampirism.client.models.entities;


import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import net.minecraft.client.animation.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Attire designed for the male vampire baron - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronAttireModel extends EntityModel<VampireBaronRenderer.VampireBaronRenderState> {

    private static final String HOOD = "hood";
    private static final String CLOAK = "cloak";

    public final ModelPart hood;
    public final ModelPart cloak;
    private float enragedProgress = 0;
    private final KeyframeAnimation cloakAnimation;

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition hood = part.addOrReplaceChild(HOOD, CubeListBuilder.create().texOffs(44, 0).addBox(-4.5f, -8.5f, -4, 9, 9, 9), PartPose.ZERO);
        hood.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-8.5f, -0.5f, -2.5f, 17, 22, 5), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    public BaronAttireModel(ModelPart part) {
        super(part);
        hood = part.getChild(HOOD);
        cloak = hood.getChild(CLOAK);
        this.cloakAnimation = CLOAK_ANIMATION.bake(part);
    }

    @Override
    public void setupAnim(VampireBaronRenderer.VampireBaronRenderState entityIn) {
        float bodyRotateAngleY = 0;
        if (entityIn.attackTime > 0.0F) {
            HumanoidArm handside = entityIn.attackArm;
            float f1 = entityIn.attackTime;
            bodyRotateAngleY = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HumanoidArm.LEFT) {
                bodyRotateAngleY *= -1.0F;
            }
        }
        this.hood.yRot = bodyRotateAngleY;
        this.cloak.yRot = bodyRotateAngleY;
        this.enragedProgress = entityIn.enragedProgress;
        this.cloakAnimation.apply(entityIn.cloakState, entityIn.ageInTicks);
    }

    private static final AnimationDefinition CLOAK_ANIMATION = AnimationDefinition.Builder.withLength(2)
            .addAnimation("cloak",
                    new AnimationChannel(AnimationChannel.Targets.SCALE, new Keyframe(0, KeyframeAnimations.scaleVec(1f,1f,1f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0, KeyframeAnimations.scaleVec(0.6f,0.3f,0.6f), AnimationChannel.Interpolations.LINEAR))
            ).build();

}