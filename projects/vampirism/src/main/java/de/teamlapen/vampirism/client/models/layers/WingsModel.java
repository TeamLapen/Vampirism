package de.teamlapen.vampirism.client.models.layers;

import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import net.minecraft.client.animation.*;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.AnimationState;

public class WingsModel extends Model<WingsModel.State> {
    private final ModelPart wings;
    private final ModelPart left_wing;
    private final ModelPart outer_left_wing;
    private final ModelPart right_wing;
    private final ModelPart outer_right_wing;

    private static final String WINGS = "wings";
    private static final String LEFT_WING = "left_wing";
    private static final String RIGHT_WING = "right_wing";
    private static final String OUTER_LEFT_WING = "outer_left_wing";
    private static final String OUTER_RIGHT_WING = "outer_right_wing";

    private final KeyframeAnimation flyAnimation;
    private final KeyframeAnimation growAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation shrinkAnimation;

    public WingsModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutNoCull);
        this.wings = root.getChild(WINGS);
        this.left_wing = this.wings.getChild(LEFT_WING);
        this.outer_left_wing = this.left_wing.getChild(OUTER_LEFT_WING);
        this.right_wing = this.wings.getChild(RIGHT_WING);
        this.outer_right_wing = this.right_wing.getChild(OUTER_RIGHT_WING);

        this.flyAnimation = SWING_ANIMATION.bake(root);
        this.growAnimation = GROW_ANIMATION.bake(root);
        this.idleAnimation = IDLE_ANIMATION.bake(root);
        this.shrinkAnimation = SHRINK_ANIMATION.bake(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wings = partdefinition.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition left_wing = wings.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(0.0F, -9.0F, 0.0F, 18.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        PartDefinition outer_left_wing = left_wing.addOrReplaceChild("outer_left_wing", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -9.0F, 0.0F, 16.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(18.0F, 0.0F, 0.0F, 0.0F, 0.2094F, 0.0F));

        PartDefinition outer_left_wing_filler = outer_left_wing.addOrReplaceChild("outer_left_wing_filler", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_left_wing_filler_top = outer_left_wing_filler.addOrReplaceChild("outer_left_wing_filler_top", CubeListBuilder.create().texOffs(11, 1).addBox(0.0F, -9.0F, 0.0F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 2).addBox(5.0F, -8.0F, 0.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 3).addBox(9.0F, -7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 4).addBox(11.0F, -6.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 5).addBox(13.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 6).addBox(14.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).addBox(15.0F, -2.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_left_wing_filler_top_side = outer_left_wing_filler_top.addOrReplaceChild("outer_left_wing_filler_top_side", CubeListBuilder.create().texOffs(11, 0).addBox(5.0F, -9.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 1).addBox(9.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 2).addBox(11.0F, -7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 3).addBox(13.0F, -6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 4).addBox(14.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 5).addBox(15.0F, -4.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_left_wing_filler_bottom = outer_left_wing_filler.addOrReplaceChild("outer_left_wing_filler_bottom", CubeListBuilder.create().texOffs(14, 13).addBox(0.0F, 4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 12).addBox(1.0F, 3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 14).addBox(2.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 17).addBox(3.0F, 8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 15).addBox(4.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(9, 14).addBox(5.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 13).addBox(6.0F, 4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 12).addBox(7.0F, 3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 11).addBox(8.0F, 2.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 12).addBox(9.0F, 3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 13).addBox(10.0F, 4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 15).addBox(11.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 16).addBox(12.0F, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 18).addBox(13.0F, 9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(14.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(-1, 12).addBox(15.0F, 3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_left_wing_filler_bottom_side = outer_left_wing_filler_bottom.addOrReplaceChild("outer_left_wing_filler_bottom_side", CubeListBuilder.create().texOffs(16, 12).addBox(1.0F, 3.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 12).addBox(2.0F, 3.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 14).addBox(3.0F, 5.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 15).addBox(4.0F, 6.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 14).addBox(5.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 14).addBox(6.0F, 4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 14).addBox(7.0F, 3.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 14).addBox(8.0F, 2.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 11).addBox(9.0F, 2.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 11).addBox(10.0F, 3.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 13).addBox(11.0F, 4.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 15).addBox(12.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 16).addBox(13.0F, 7.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 15).addBox(14.0F, 6.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 12).addBox(15.0F, 3.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_wing_filler = left_wing.addOrReplaceChild("left_wing_filler", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_wing_filler_top = left_wing_filler.addOrReplaceChild("left_wing_filler_top", CubeListBuilder.create().texOffs(17, 28).addBox(0.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 29).addBox(1.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 30).addBox(2.0F, -3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 29).addBox(3.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 28).addBox(4.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 27).addBox(5.0F, -6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 26).addBox(6.0F, -7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 25).addBox(8.0F, -8.0F, 0.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(12.0F, -9.0F, 0.0F, 6.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_wing_filler_top_side = left_wing_filler_top.addOrReplaceChild("left_wing_filler_top_side", CubeListBuilder.create().texOffs(17, 27).addBox(1.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 28).addBox(2.0F, -4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 28).addBox(3.0F, -4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 27).addBox(4.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 26).addBox(5.0F, -6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 25).addBox(6.0F, -7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 24).addBox(8.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 23).addBox(12.0F, -9.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_wing_filler_bottom = left_wing_filler.addOrReplaceChild("left_wing_filler_bottom", CubeListBuilder.create().texOffs(16, 38).addBox(0.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 39).addBox(1.0F, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 40).addBox(2.0F, 8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 41).addBox(3.0F, 9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 40).addBox(4.0F, 8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(9, 39).addBox(5.0F, 7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 38).addBox(7.0F, 6.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 37).addBox(9.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 36).addBox(10.0F, 4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 37).addBox(11.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 38).addBox(12.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 41).addBox(13.0F, 9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 39).addBox(14.0F, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 38).addBox(15.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 37).addBox(16.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(-1, 37).addBox(17.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_wing_filler_bottom_side = left_wing_filler_bottom.addOrReplaceChild("left_wing_filler_bottom_side", CubeListBuilder.create().texOffs(17, 38).addBox(1.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 39).addBox(2.0F, 7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 40).addBox(3.0F, 8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 40).addBox(4.0F, 8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 39).addBox(5.0F, 7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 38).addBox(7.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(9, 37).addBox(9.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 36).addBox(10.0F, 4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 36).addBox(11.0F, 4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 37).addBox(12.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 38).addBox(13.0F, 6.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 38).addBox(14.0F, 7.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 38).addBox(15.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 38).addBox(16.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing = wings.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 23).addBox(-18.0F, -9.0F, 0.0F, 18.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition outer_right_wing = right_wing.addOrReplaceChild("outer_right_wing", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -9.0F, 0.0F, 16.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.0F, 0.0F, 0.0F, 0.0F, -0.2094F, 0.0F));

        PartDefinition outer_right_wing_filler = outer_right_wing.addOrReplaceChild("outer_right_wing_filler", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_right_wing_filler_top = outer_right_wing_filler.addOrReplaceChild("outer_right_wing_filler_top", CubeListBuilder.create().texOffs(17, 1).addBox(-5.0F, -9.0F, 0.0F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 2).addBox(-9.0F, -8.0F, 0.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 3).addBox(-11.0F, -7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 4).addBox(-13.0F, -6.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 5).addBox(-14.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 6).addBox(-15.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 8).addBox(-16.0F, -2.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_right_wing_filler_side = outer_right_wing_filler_top.addOrReplaceChild("outer_right_wing_filler_side", CubeListBuilder.create().texOffs(22, 0).addBox(-5.0F, -9.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 1).addBox(-9.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 2).addBox(-11.0F, -7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 3).addBox(-13.0F, -6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 4).addBox(-14.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 5).addBox(-15.0F, -4.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer_right_wing_filler_bottom = outer_right_wing_filler.addOrReplaceChild("outer_right_wing_filler_bottom", CubeListBuilder.create().texOffs(16, 13).addBox(-1.0F, -9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(17, 12).addBox(-2.0F, -10.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 14).addBox(-3.0F, -8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 17).addBox(-4.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 15).addBox(-5.0F, -7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 14).addBox(-6.0F, -8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 13).addBox(-7.0F, -9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 12).addBox(-8.0F, -10.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 11).addBox(-9.0F, -11.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 12).addBox(-10.0F, -10.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 13).addBox(-11.0F, -9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 15).addBox(-12.0F, -7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 16).addBox(-13.0F, -6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(29, 17).addBox(-14.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 15).addBox(-15.0F, -7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 12).addBox(-16.0F, -10.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition outer_right_wing_filler_side2 = outer_right_wing_filler_bottom.addOrReplaceChild("outer_right_wing_filler_side2", CubeListBuilder.create().texOffs(18, 12).addBox(-1.0F, -10.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 12).addBox(-2.0F, -10.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 14).addBox(-3.0F, -8.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 15).addBox(-4.0F, -7.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 14).addBox(-5.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 13).addBox(-6.0F, -9.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 12).addBox(-7.0F, -10.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 11).addBox(-8.0F, -11.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 11).addBox(-9.0F, -11.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 12).addBox(-10.0F, -10.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 13).addBox(-11.0F, -9.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(29, 15).addBox(-12.0F, -7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 16).addBox(-13.0F, -6.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 15).addBox(-14.0F, -7.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 12).addBox(-15.0F, -10.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing_filler = right_wing.addOrReplaceChild("right_wing_filler", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing_filler_top = right_wing_filler.addOrReplaceChild("right_wing_filler_top", CubeListBuilder.create().texOffs(19, 28).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 29).addBox(-2.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 30).addBox(-3.0F, -3.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 29).addBox(-4.0F, -4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 28).addBox(-5.0F, -5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 27).addBox(-6.0F, -6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 26).addBox(-8.0F, -7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 25).addBox(-12.0F, -8.0F, 0.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 24).addBox(-18.0F, -9.0F, 0.0F, 6.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing_filler_top_side = right_wing_filler_top.addOrReplaceChild("right_wing_filler_top_side", CubeListBuilder.create().texOffs(20, 27).addBox(-1.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 28).addBox(-2.0F, -4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 28).addBox(-3.0F, -4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 27).addBox(-4.0F, -5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 26).addBox(-5.0F, -6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 25).addBox(-6.0F, -7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 24).addBox(-8.0F, -8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 23).addBox(-12.0F, -9.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing_filler_bottom = right_wing_filler.addOrReplaceChild("right_wing_filler_bottom", CubeListBuilder.create().texOffs(18, 38).addBox(-1.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 39).addBox(-2.0F, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 40).addBox(-3.0F, 8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 41).addBox(-4.0F, 9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 40).addBox(-5.0F, 8.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 39).addBox(-7.0F, 7.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 38).addBox(-9.0F, 6.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 37).addBox(-10.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 36).addBox(-11.0F, 4.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(29, 37).addBox(-12.0F, 5.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 38).addBox(-13.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 41).addBox(-14.0F, 9.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 39).addBox(-15.0F, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(33, 38).addBox(-16.0F, 6.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(33, 37).addBox(-18.0F, 5.0F, 0.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_wing_filler_bottom_side = right_wing_filler_bottom.addOrReplaceChild("right_wing_filler_bottom_side", CubeListBuilder.create().texOffs(20, 38).addBox(-1.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 39).addBox(-2.0F, 7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 40).addBox(-3.0F, 8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 40).addBox(-4.0F, 8.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 39).addBox(-5.0F, 7.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 38).addBox(-7.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 37).addBox(-9.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(29, 36).addBox(-10.0F, 4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 36).addBox(-11.0F, 4.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 37).addBox(-12.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 38).addBox(-13.0F, 6.0F, 0.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(33, 39).addBox(-14.0F, 7.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 38).addBox(-15.0F, 6.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(35, 37).addBox(-16.0F, 5.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(State renderState) {
        super.setupAnim(renderState);

        switch (renderState.wingsState) {
            case OPENING -> this.growAnimation.apply(renderState.growState, renderState.ageInTicks, IWingsEntity.GROW_SPEED);
            case OPEN -> this.idleAnimation.apply(renderState.flyState, renderState.ageInTicks, 0.25f);
            case FLYING -> this.flyAnimation.apply(renderState.flyState, renderState.ageInTicks);
            case CLOSING -> this.shrinkAnimation.apply(renderState.growState, renderState.ageInTicks, IWingsEntity.GROW_SPEED);
        }
    }

    //<editor-fold desc="Animation Definitions">

    public static final AnimationDefinition SWING_ANIMATION = AnimationDefinition.Builder.withLength(4.0F).looping()
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -40.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 40.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 18.39F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.25F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 18.39F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -18.47F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, -20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.25F, KeyframeAnimations.degreeVec(0.0F, 20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, -18.47F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition GROW_ANIMATION = AnimationDefinition.Builder.withLength(IWingsEntity.GROW_SECONDS)
            .addAnimation(WINGS, new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F, KeyframeAnimations.scaleVec(0.16F, 0.16F, 0.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F, KeyframeAnimations.scaleVec(0.4F, 0.4F, 0.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F, KeyframeAnimations.scaleVec(0.8F, 0.8F, 0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.scaleVec(0.94F, 0.94F, 0.94F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition SHRINK_ANIMATION = AnimationDefinition.Builder.withLength(IWingsEntity.GROW_SECONDS)
            .addAnimation(WINGS, new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.scaleVec(0.94F, 0.94F, 0.94F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.scaleVec(0.8F, 0.8F, 0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.scaleVec(0.2F, 0.2F, 0.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.scaleVec(0.06F, 0.06F, 0.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition IDLE_ANIMATION = AnimationDefinition.Builder.withLength(4.0F).looping()
            .addAnimation(WINGS, new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(1.05F, 1.05F, 1.05F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -12.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 24.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 12.0F, 2.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    //</editor-fold>

    public static class State {
        public float ageInTicks;
        public IWingsEntity.WingsState wingsState;
        public AnimationState flyState;
        public AnimationState growState;
    }
}