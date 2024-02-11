package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;


public class WingModel<T extends LivingEntity> extends AgeableListModel<T> {
    private static final String WING_RIGHT = "wing_right";
    private static final String WING_RIGHT2 = "wing_right2";
    private static final String WING_LEFT = "wing_left";
    private static final String WING_LEFT2 = "wing_left2";

    public final @NotNull ModelPart wingRight;
    public final @NotNull ModelPart wingLeft;
    public final @NotNull ModelPart wingRight2;
    public final @NotNull ModelPart wingLeft2;

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition wingr = part.addOrReplaceChild(WING_RIGHT, CubeListBuilder.create().texOffs(0, 46).addBox(-18, -6, 0, 18, 18, 0), PartPose.offsetAndRotation(0.2f, 2.5f, 2, 0.136659280431156F, 0.5462880558742251F, 0.27314402793711257F));
        wingr.addOrReplaceChild(WING_RIGHT2, CubeListBuilder.create().texOffs(0, 28).addBox(-16, -4, 0, 16, 18, 0), PartPose.offsetAndRotation(-18, -2, 0, 0.0F, -0.8196066167365371F, 0.0F));
        PartDefinition wingl = part.addOrReplaceChild(WING_LEFT, CubeListBuilder.create().texOffs(0, 46).mirror().addBox(0, -6, 0, 18, 18, 0), PartPose.offsetAndRotation(-0.2f, -2.5f, 2, 0.136659280431156F, -0.6373942428283291F, -0.27314402793711257F));
        wingl.addOrReplaceChild(WING_LEFT2, CubeListBuilder.create().texOffs(0, 28).mirror().addBox(0, -4, 0, 16, 18, 0), PartPose.offsetAndRotation(18, -2, 0, 0, 0.8196066167365371F, 0));

        return LayerDefinition.create(mesh, 128, 64);
    }


    public WingModel(@NotNull ModelPart part) {
        wingRight = part.getChild(WING_RIGHT);
        wingRight2 = wingRight.getChild(WING_RIGHT2);
        wingLeft = part.getChild(WING_LEFT);
        wingLeft2 = wingLeft.getChild(WING_LEFT2);


    }

    public void copyRotationFromBody(@NotNull ModelPart body) {
        this.wingLeft.yRot = body.yRot;
        this.wingLeft2.yRot = body.yRot;
        this.wingRight.yRot = body.yRot;
        this.wingRight2.yRot = body.yRot;
        this.wingLeft.xRot = body.xRot;
        this.wingRight.xRot = body.xRot;
        this.wingLeft.zRot = body.zRot;
        this.wingRight.zRot = body.zRot;
    }

    @Override
    public void prepareMobModel(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        if (entityIn.isShiftKeyDown()) {
            this.wingRight.y = 3.0f;
            this.wingLeft.y = 3.0f;
        } else {
            this.wingRight.y = 2.5f;
            this.wingLeft.y = 2.5f;
        }

        this.wingLeft.zRot -= (float) (Mth.cos((entityIn.tickCount + partialTick) * 0.0662F + (float) Math.PI) * 0.06);
        this.wingRight.zRot += (float) (Mth.cos((entityIn.tickCount + partialTick) * 0.0662F + (float) Math.PI) * 0.06);

        this.wingLeft.yRot -= 0.3f;
        this.wingRight.yRot += 0.3f;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(@NotNull ModelPart ModelRenderer, float x, float y, float z) {
        ModelRenderer.xRot = x;
        ModelRenderer.yRot = y;
        ModelRenderer.zRot = z;
    }

    @Override
    public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @NotNull
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.wingLeft, this.wingRight);
    }

    protected @NotNull HumanoidArm getSwingingSide(@NotNull T entity) {
        HumanoidArm handside = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> headParts() {
        return Collections.emptyList();
    }
}