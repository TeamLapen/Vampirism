package de.teamlapen.vampirism.client.models.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class VampirismArmorLayerDefinitions {

    public static @NotNull LayerDefinition vampireBootsDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightToes = right_pants.addOrReplaceChild("rightToes", CubeListBuilder.create().texOffs(2, 9).addBox(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightBoot = right_pants.addOrReplaceChild("rightBoot", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftToes = left_pants.addOrReplaceChild("leftToes", CubeListBuilder.create().texOffs(18, 9).addBox(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftBoot = left_pants.addOrReplaceChild("leftBoot", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public static LayerDefinition vampireCrownDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition front = hat.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 4).addBox(-4.0F, -7.7F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(0, 2).addBox(1.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(12, 2).addBox(-3.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(6, 2).addBox(-1.0F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(1, 0).addBox(2.25F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(13, 0).addBox(-3.2F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(7, 0).addBox(-0.5F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition back = hat.addOrReplaceChild("back", CubeListBuilder.create().texOffs(18, 4).addBox(-4.0F, -7.7F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(30, 2).addBox(1.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(18, 2).addBox(-3.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(24, 2).addBox(-1.0F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(31, 0).addBox(2.25F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(19, 0).addBox(-3.2F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(25, 0).addBox(-0.5F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left = hat.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 6).addBox(4.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(12, 17).addBox(4.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(0, 17).addBox(4.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(6, 17).addBox(4.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(13, 15).addBox(4.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(1, 15).addBox(4.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(7, 15).addBox(4.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right = hat.addOrReplaceChild("right", CubeListBuilder.create().texOffs(18, 6).addBox(-5.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(18, 17).addBox(-5.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(30, 17).addBox(-5.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(24, 17).addBox(-5.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(19, 15).addBox(-5.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(31, 15).addBox(-5.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F))
                .texOffs(25, 15).addBox(-5.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);

    }

    public static @NotNull LayerDefinition vampirePantsDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition belt = jacket.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(4, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static @NotNull LayerDefinition hunterTallHatDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition top = hat.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -37.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static @NotNull LayerDefinition hunterBroadHatLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition top = hat.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -35.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static @NotNull LayerDefinition vampireHatDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(18, 1).addBox(-1.5F, -8.0F, -3.5F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition tip = hat.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(2, 1).addBox(-2.4F, -13.5F, -3.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1222F, 0.0F, 0.2775F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition jacket = body.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition right_sleeve = right_arm.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition left_sleeve = left_arm.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_pants = right_leg.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_pants = left_leg.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
