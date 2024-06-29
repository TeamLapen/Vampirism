package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

public class CloakModel extends VampirismArmorModel {

    private static final String CLOAK_BACK = "cloak_back";
    private static final String LEFT_LONG = "left_long";
    private static final String RIGHT_MEDIUM = "right_medium";
    private static final String LEFT_MEDIUM = "left_medium";
    private static final String RIGHT_SHORT = "right_short";
    private static final String LEFT_SHORT = "left_short";
    private static final String RIGHT_LONG = "right_long";
    private static final String SHOULDER_LEFT = "shoulder_left";
    private static final String SHOULDER_RIGHT = "shoulder_right";

    private static CloakModel cloakItemModel;

    public static CloakModel getAdjustedCloak(HumanoidModel<?> wearerModel, LivingEntity entity) {
        if (cloakItemModel == null) {
            cloakItemModel = new CloakModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOAK));
        }
        cloakItemModel.copyFromHumanoid(wearerModel);
        cloakItemModel.setupAnimation(entity);
        return cloakItemModel;
    }

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild(CLOAK_BACK, CubeListBuilder.create().texOffs(0, 48).mirror().addBox(-4, 0, 2, 8, 15, 1), PartPose.offsetAndRotation(0, 0.2f, 2, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(LEFT_LONG, CubeListBuilder.create().texOffs(18, 48).mirror().addBox(4, 0, 2, 1, 15, 1), PartPose.offsetAndRotation(0, 0.2f, 2, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(RIGHT_MEDIUM, CubeListBuilder.create().texOffs(22, 50).addBox(-5, 0, 1, 1, 13, 1), PartPose.offsetAndRotation(0, 0.2f, 2f, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(LEFT_MEDIUM, CubeListBuilder.create().texOffs(22, 50).mirror().addBox(4, 0, 1, 1, 13, 1), PartPose.offsetAndRotation(0, 0.2f, 2, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(RIGHT_SHORT, CubeListBuilder.create().texOffs(26, 52).addBox(-5, 0, 0, 1, 11, 1), PartPose.offsetAndRotation(0, 0.2f, 2, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(LEFT_SHORT, CubeListBuilder.create().texOffs(26, 52).addBox(4, 0, 0, 1, 11, 1), PartPose.offsetAndRotation(0, 0.2f, 2, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(RIGHT_LONG, CubeListBuilder.create().texOffs(18, 48).addBox(-5, 0, 2, 1, 15, 1), PartPose.offsetAndRotation(0, 0.2f, 2f, 0.0872665F, 0F, 0F));
        part.addOrReplaceChild(SHOULDER_RIGHT, CubeListBuilder.create().texOffs(30, 60).addBox(-4, 0, 0, 1, 1, 3), PartPose.offset(-5, 0, 0));
        part.addOrReplaceChild(SHOULDER_LEFT, CubeListBuilder.create().texOffs(30, 60).mirror().addBox(3, 0, 0, 1, 1, 3), PartPose.offset(-5, 0, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    private final @NotNull ModelPart cloakback;
    private final @NotNull ModelPart leftlong;
    private final @NotNull ModelPart rightmedium;
    private final @NotNull ModelPart leftmedium;
    private final @NotNull ModelPart rightshort;
    private final @NotNull ModelPart leftshort;
    private final @NotNull ModelPart rightlong;
    private final @NotNull ModelPart shoulderright;
    private final @NotNull ModelPart shoulderleft;

    public CloakModel(@NotNull ModelPart part) {
        cloakback = part.getChild(CLOAK_BACK);
        leftlong = part.getChild(LEFT_LONG);
        rightmedium = part.getChild(RIGHT_MEDIUM);
        leftmedium = part.getChild(LEFT_MEDIUM);
        rightshort = part.getChild(RIGHT_SHORT);
        rightlong = part.getChild(RIGHT_LONG);
        leftshort = part.getChild(LEFT_SHORT);
        shoulderleft = part.getChild(SHOULDER_LEFT);
        shoulderright = part.getChild(SHOULDER_RIGHT);
    }

    private void setupAnimation(LivingEntity entity) {
        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());

        float f = Mth.rotLerp(MixinHooks.armorLayerPartialTicks, entity.yBodyRotO, entity.yBodyRot);
        float f1 = Mth.rotLerp(MixinHooks.armorLayerPartialTicks, entity.yHeadRotO, entity.yHeadRot);
        float f2 = f1 - f;

        float f6 = Mth.lerp(MixinHooks.armorLayerPartialTicks, entity.xRotO, entity.getXRot());

        if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
            f = Mth.rotLerp(MixinHooks.armorLayerPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        if (isEntityUpsideDown(entity)) {
            f6 *= -1.0F;
            f2 *= -1.0F;
        }

        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entity.isAlive()) {
            f8 = entity.walkAnimation.speed(MixinHooks.armorLayerPartialTicks);
            f5 = entity.walkAnimation.position(MixinHooks.armorLayerPartialTicks);
            if (entity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }
        cloakItemModel.setupAnim(entity, f5, f8, entity.tickCount + MixinHooks.armorLayerPartialTicks, f2, f6);
    }

    public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity != null && entity.getFallFlyingTicks() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entity.getDeltaMovement().x * entity.getDeltaMovement().x + entity.getDeltaMovement().y * entity.getDeltaMovement().y + entity.getDeltaMovement().z * entity.getDeltaMovement().z);
            f6 = f6 / 0.2F;
            f6 = f6 * f6 * f6;
        }

        if (f6 < 1.0F) {
            f6 = 1.0F;
        }

        float rotation = Mth.cos(limbSwing * 0.6662F) * 1.4F * (limbSwingAmount / 1.8f) / f6;
        if (rotation < 0.0F) {
            rotation *= -1;
        }
        this.cloakback.xRot = 0.0872665F + (rotation / 3);
        this.leftlong.xRot = 0.0872665F + (rotation / 3);
        this.rightlong.xRot = 0.0872665F + (rotation / 3);
        this.leftmedium.xRot = 0.0872665F + (rotation / 3);
        this.rightmedium.xRot = 0.0872665F + (rotation / 3);
        this.rightshort.xRot = 0.0872665F + (rotation / 3);
        this.leftshort.xRot = 0.0872665F + (rotation / 3);

        if (entity.isCrouching()) {
            this.cloakback.xRot += 0.5F;
            this.leftlong.xRot += 0.5F;
            this.rightlong.xRot += 0.5F;
            this.leftmedium.xRot += 0.5F;
            this.rightmedium.xRot += 0.5F;
            this.leftshort.xRot += 0.5F;
            this.rightshort.xRot += 0.5F;
        }
    }

    @Override
    protected @NotNull Iterable<ModelPart> getBodyModels() {
        return ImmutableList.of(cloakback, leftlong, rightmedium, leftmedium, rightshort, leftshort, rightlong, shoulderright, shoulderleft);
    }


}
