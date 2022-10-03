package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
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

    public static CloakModel getAdjustedCloak(HumanoidModel<?> wearerModel) {
        if (cloakItemModel == null) {
            cloakItemModel = new CloakModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOAK));
        }
        cloakItemModel.copyFromHumanoid(wearerModel);
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

//    @Override
//    public void setupAnim(@NotNull LivingEntity entity, float f, float f1, float ageInTicks, float netHeadYaw, float headPitch) {
//        super.setupAnim(entity, f, f1, ageInTicks, netHeadYaw, headPitch);
//        //Isn't use afaik
//
//
//        boolean flag = entity != null && entity.getFallFlyingTicks() > 4;
//
//        float f6 = 1.0F;
//        if (flag) {
//            f6 = (float) (entity.getDeltaMovement().x * entity.getDeltaMovement().x + entity.getDeltaMovement().y * entity.getDeltaMovement().y
//                    + entity.getDeltaMovement().z * entity.getDeltaMovement().z);
//            f6 = f6 / 0.2F;
//            f6 = f6 * f6 * f6;
//        }
//
//        if (f6 < 1.0F) {
//            f6 = 1.0F;
//        }
//
//        float rotation = Mth.cos(f * 0.6662F) * 1.4F * f1 / f6;
//        if (rotation < 0.0F)
//            rotation *= -1;
//        this.cloakback.xRot = 0.0872665F + (rotation / 3);
//        this.leftlong.xRot = 0.0872665F + (rotation / 3);
//        this.rightlong.xRot = 0.0872665F + (rotation / 3);
//        this.leftmedium.xRot = 0.0872665F + (rotation / 3);
//        this.rightmedium.xRot = 0.0872665F + (rotation / 3);
//        this.rightshort.xRot = 0.0872665F + (rotation / 3);
//        this.leftshort.xRot = 0.0872665F + (rotation / 3);
//
//        if (this.crouching) {
//            this.cloakback.xRot += 0.5F;
//            this.leftlong.xRot += 0.5F;
//            this.rightlong.xRot += 0.5F;
//            this.leftmedium.xRot += 0.5F;
//            this.rightmedium.xRot += 0.5F;
//            this.leftshort.xRot += 0.5F;
//            this.rightshort.xRot += 0.5F;
//        }
//    }

    @Override
    protected @NotNull Iterable<ModelPart> getBodyModels() {
        return ImmutableList.of(cloakback, leftlong, rightmedium, leftmedium, rightshort, leftshort, rightlong, shoulderright, shoulderleft);
    }


}
