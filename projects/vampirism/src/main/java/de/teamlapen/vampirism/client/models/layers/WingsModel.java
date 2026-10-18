package de.teamlapen.vampirism.client.models.layers;

import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import net.minecraft.client.animation.*;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
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
    private final KeyframeAnimation shrinkAnimation;

    public WingsModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.wings = root.getChild(WINGS);
        this.left_wing = this.wings.getChild(LEFT_WING);
        this.outer_left_wing = this.left_wing.getChild(OUTER_LEFT_WING);
        this.right_wing = this.wings.getChild(RIGHT_WING);
        this.outer_right_wing = this.right_wing.getChild(OUTER_RIGHT_WING);

        this.flyAnimation = SWING_ANIMATION.bake(root);
        this.growAnimation = GROW_ANIMATION.bake(root);
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
            case OPEN -> animateIdle(renderState);
            case FLYING -> this.flyAnimation.apply(renderState.flyState, renderState.ageInTicks);
            case CLOSING -> this.shrinkAnimation.apply(renderState.growState, renderState.ageInTicks, IWingsEntity.GROW_SPEED);
        }
    }

    //<editor-fold desc="Idle Animation">

    /*
     * Sign conventions (left wing, the right wing is mirrored):
     * - yRot: positive swings the wing forward. The wings sit directly on the back, so the inner wing must never swing
     *   forward past its rest pose, otherwise it clips into the body and arms.
     * - zRot: negative raises the wing tips. Rolling stays in the plane of the back and can't clip.
     * - The root must not pitch or yaw, as that would tilt half of the wings into the body. It only rolls and bobs.
     */

    private static final float IDLE_FADE_IN_SECONDS = 0.6f;
    /**
     * How far the inner wing may open past its rest pose (rest is -10°)
     */
    private static final float INNER_MAX_SPREAD = 5f;
    /**
     * Length of the idle choreography before it repeats
     */
    private static final float IDLE_LOOP_SECONDS = 48f;

    private static final float BREATH_PERIOD = 5f;
    private static final float BREATH_PERIOD_WATER = 7.5f;
    private static final float BREATH_DEPTH_PERIOD = 17f;
    private static final float BREATH_INNER = 5f;
    private static final float BREATH_OUTER = 12f;
    private static final float BREATH_LIFT = 3f;
    private static final float BREATH_OUTER_LAG = 0.9f;
    private static final float BREATH_RIGHT_PHASE = 0.35f;
    private static final float FLUTTER_PERIOD = 1.7f;
    private static final float FLUTTER_OUTER = 2f;

    private static final float SWAY_ROLL = 1.5f;
    private static final float SWAY_ROLL_PERIOD = 5.3f;
    private static final float SWAY_ROLL_SLOW = 0.8f;
    private static final float SWAY_ROLL_SLOW_PERIOD = 11.9f;

    private static final float GESTURE_RIGHT_DELAY = 0.1f;

    private static final float STRETCH_START = 6f;
    private static final float STRETCH_DURATION = 2.4f;
    private static final float STRETCH_OUTER = 30f;
    private static final float STRETCH_LIFT = 14f;
    private static final float STRETCH_SHAKE = 6f;

    private static final float RUFFLE_START = 14f;
    private static final float RUFFLE_DURATION = 0.9f;
    private static final float RUFFLE_OUTER = 4f;
    private static final float RUFFLE_LIFT = 1.5f;
    private static final float RUFFLE_TUCK = 3f;

    private static final float FOLD_START = 29f;
    private static final float FOLD_DURATION = 5f;
    private static final float FOLD_INNER = 20f;
    private static final float FOLD_OUTER = 45f;
    private static final float FOLD_LIFT = 4f;
    private static final float FOLD_OVERSHOOT = 8f;

    private static final float SIGH_START = 38f;
    private static final float SIGH_DURATION = 3.5f;
    private static final float SIGH_LIFT = 10f;
    private static final float SIGH_DROOP = 7f;
    private static final float SIGH_OUTER = 10f;
    private static final float SIGH_OUTER_EXHALE = 5f;

    private static final float FLICK_START = 44f;
    private static final float FLICK_DURATION = 1.2f;
    private static final float FLICK_OUTER = 18f;
    private static final float FLICK_LIFT = 5f;

    private static final float WALK_FOLD_INNER = 22f;
    private static final float WALK_FOLD_OUTER = 30f;
    private static final float WALK_FOLD_LIFT = 4f;
    private static final float WALK_STEP_OUTER = 6f;
    private static final float WALK_STEP_LIFT = 4f;
    private static final float WALK_BOB = 0.6f;

    private static final float CROUCH_INNER = 12f;
    private static final float CROUCH_OUTER = 15f;
    private static final float CROUCH_LIFT = 8f;
    private static final float WATER_INNER = 25f;
    private static final float WATER_OUTER = 40f;
    private static final float HURT_INNER = 8f;
    private static final float HURT_OUTER = 25f;

    /**
     * Procedural idle animation, layered on top of the rest pose.
     * <p>
     * Continuous breathing and sway, plus a {@link #IDLE_LOOP_SECONDS} long choreography of gestures
     * (stretch, ruffle, fold &amp; settle, sigh, double flick) that are suppressed while moving.
     * <p>
     * Per side the wing is described by three values in degrees:
     * <ul>
     *     <li>inner spread: positive opens the inner wing (limited by {@link #INNER_MAX_SPREAD})</li>
     *     <li>outer spread: positive unfurls the outer wing</li>
     *     <li>lift: positive raises the wing tips</li>
     * </ul>
     */
    private void animateIdle(State state) {
        float seconds = state.ageInTicks / 20f;
        float fadeIn = state.flyState.isStarted() ? smoothstep(state.flyState.getTimeInMillis(state.ageInTicks) / 1000f / IDLE_FADE_IN_SECONDS) : 1f;
        float movement = Mth.clamp(state.walkAnimationSpeed, 0f, 1f);
        float calm = 1f - 0.8f * movement;
        float breathDepth = 0.75f + 0.25f * Mth.sin(seconds * Mth.TWO_PI / BREATH_DEPTH_PERIOD);
        float breathAmp = calm * breathDepth * (state.isInWater ? 0.5f : 1f);
        float gestureAmp = Math.max(0f, 1f - movement * 2f) * (state.isCrouching || state.isInWater ? 0f : 1f);
        float step = Mth.cos(state.walkAnimationPos * 0.6662f);
        float cycle = Mth.positiveModulo(seconds, IDLE_LOOP_SECONDS);

        float rootRoll = (SWAY_ROLL * Mth.sin(seconds * Mth.TWO_PI / SWAY_ROLL_PERIOD) + SWAY_ROLL_SLOW * Mth.sin(seconds * Mth.TWO_PI / SWAY_ROLL_SLOW_PERIOD)) * calm;
        this.wings.zRot += rootRoll * Mth.DEG_TO_RAD * fadeIn;
        this.wings.y += WALK_BOB * Math.abs(step) * movement * fadeIn;

        animateIdleSide(state, this.left_wing, this.outer_left_wing, 1f, 0f, 0f, seconds, cycle, movement, step, breathAmp, gestureAmp, fadeIn);
        animateIdleSide(state, this.right_wing, this.outer_right_wing, -1f, BREATH_RIGHT_PHASE, GESTURE_RIGHT_DELAY, seconds, cycle, movement, -step, breathAmp, gestureAmp, fadeIn);
    }

    private static void animateIdleSide(State state, ModelPart inner, ModelPart outer, float side, float breathPhaseOffset, float gestureDelay, float seconds, float cycle, float movement, float step, float breathAmp, float gestureAmp, float fadeIn) {
        float breathPeriod = state.isInWater ? BREATH_PERIOD_WATER : BREATH_PERIOD;
        float breathPhase = seconds * Mth.TWO_PI / breathPeriod + breathPhaseOffset;
        float innerSpread = BREATH_INNER * Mth.sin(breathPhase) * breathAmp;
        // the outer wing trails behind the inner one so the motion ripples out to the tips
        float outerSpread = BREATH_OUTER * Mth.sin(breathPhase - BREATH_OUTER_LAG) * breathAmp;
        float lift = BREATH_LIFT * Mth.sin(breathPhase - BREATH_OUTER_LAG * 0.5f) * breathAmp;
        outerSpread += FLUTTER_OUTER * Mth.sin(seconds * Mth.TWO_PI / FLUTTER_PERIOD + side * 1.3f) * breathAmp;

        if (gestureAmp > 0) {
            float t = cycle - gestureDelay;

            // both wings reach up and unfurl, then shake out the tips
            float stretch = progress(t, STRETCH_START, STRETCH_DURATION);
            float stretchReach = bell(stretch / 0.6f) * gestureAmp;
            outerSpread += STRETCH_OUTER * stretchReach + STRETCH_SHAKE * shake(stretch, 0.45f, 6) * gestureAmp;
            lift += STRETCH_LIFT * stretchReach;

            // quick tremor through the wings
            float ruffle = progress(t, RUFFLE_START, RUFFLE_DURATION);
            float ruffleAmp = bell(ruffle) * gestureAmp;
            outerSpread += RUFFLE_OUTER * Mth.sin(ruffle * Mth.PI * 14 + side) * ruffleAmp;
            lift += RUFFLE_LIFT * Mth.sin(ruffle * Mth.PI * 18) * ruffleAmp;
            innerSpread -= RUFFLE_TUCK * ruffleAmp;

            // fold in against the back, hold, then open again with a small overshoot
            float fold = progress(t, FOLD_START, FOLD_DURATION);
            float folded = plateau(fold, 0.25f, 0.6f) * gestureAmp;
            innerSpread -= FOLD_INNER * folded;
            outerSpread -= FOLD_OUTER * folded;
            lift -= FOLD_LIFT * folded;
            outerSpread += FOLD_OVERSHOOT * bell((fold - 0.75f) / 0.25f) * gestureAmp;

            // raise and open, then slowly droop
            float sigh = progress(t, SIGH_START, SIGH_DURATION);
            float inhale = bell(sigh / 0.35f) * gestureAmp;
            float exhale = bell((sigh - 0.3f) / 0.7f) * gestureAmp;
            lift += SIGH_LIFT * inhale - SIGH_DROOP * exhale;
            outerSpread += SIGH_OUTER * inhale - SIGH_OUTER_EXHALE * exhale;

            // two sharp flicks of the tips
            float flick = progress(t, FLICK_START, FLICK_DURATION);
            float flicks = bell(flick / 0.4f) + bell((flick - 0.45f) / 0.4f);
            flicks *= flicks * gestureAmp;
            outerSpread += FLICK_OUTER * flicks;
            lift += FLICK_LIFT * flicks;
        }

        // fold back while moving and flap slightly with each step
        innerSpread -= WALK_FOLD_INNER * movement;
        outerSpread -= WALK_FOLD_OUTER * movement;
        lift -= WALK_FOLD_LIFT * movement;
        outerSpread += WALK_STEP_OUTER * step * movement;
        lift += WALK_STEP_LIFT * Math.max(0, step) * movement;

        if (state.isCrouching) {
            innerSpread -= CROUCH_INNER;
            outerSpread -= CROUCH_OUTER;
            lift -= CROUCH_LIFT;
        }
        if (state.isInWater) {
            innerSpread -= WATER_INNER;
            outerSpread -= WATER_OUTER;
        }
        if (state.hurt) {
            innerSpread -= HURT_INNER;
            outerSpread -= HURT_OUTER;
        }

        innerSpread = Math.min(innerSpread, INNER_MAX_SPREAD);

        float scale = side * Mth.DEG_TO_RAD * fadeIn;
        inner.yRot += innerSpread * scale;
        inner.zRot -= lift * scale;
        outer.yRot -= outerSpread * scale;
    }

    /**
     * @return progress of a gesture in [0,1] while it plays, outside of that range otherwise
     */
    private static float progress(float time, float start, float duration) {
        return (time - start) / duration;
    }

    /**
     * Smooth 0 → 1 → 0 bump over [0,1], 0 outside
     */
    private static float bell(float x) {
        if (x <= 0 || x >= 1) return 0;
        float s = Mth.sin(x * Mth.PI);
        return s * s;
    }

    /**
     * Smooth rise over [0,rise], hold, smooth fall over [fallStart,1], 0 outside
     */
    private static float plateau(float x, float rise, float fallStart) {
        if (x <= 0 || x >= 1) return 0;
        return smoothstep(x / rise) * (1 - smoothstep((x - fallStart) / (1 - fallStart)));
    }

    /**
     * Decaying oscillation over [from,1], 0 outside
     */
    private static float shake(float x, float from, float halfWaves) {
        if (x <= from || x >= 1) return 0;
        float q = (x - from) / (1 - from);
        return Mth.sin(q * Mth.PI * halfWaves) * (1 - q) * (1 - q);
    }

    private static float smoothstep(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3 - 2 * x);
    }

    //</editor-fold>

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
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.0833F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.16F, 0.16F, 0.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.4F, 0.4F, 0.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3333F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.8F, 0.8F, 0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.94F, 0.94F, 0.94F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition SHRINK_ANIMATION = AnimationDefinition.Builder.withLength(IWingsEntity.GROW_SECONDS)
            .addAnimation(WINGS, new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.5F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.94F, 0.94F, 0.94F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.8F, 0.8F, 0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.2F, 0.2F, 0.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.06F, 0.06F, 0.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 75.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation(OUTER_RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F * IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(IWingsEntity.GROW_SECONDS, KeyframeAnimations.degreeVec(0.0F, -125.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    //</editor-fold>

    public static class State {
        public float ageInTicks;
        public IWingsEntity.WingsState wingsState;
        public AnimationState flyState;
        public AnimationState growState;
        public float walkAnimationPos;
        public float walkAnimationSpeed;
        public boolean isCrouching;
        public boolean isInWater;
        public boolean hurt;
    }
}