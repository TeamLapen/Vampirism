package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


import org.jetbrains.annotations.NotNull;

public class ClothingBootsModel extends VampirismArmorModel {

    private static final String RIGHT_BOOT = "right_boot";
    private static final String RIGHT_TOES = "right_toes";
    private static final String LEFT_BOOT = "left_boot";
    private static final String LEFT_TOES = "left_toes";

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        CubeDeformation scale = new CubeDeformation(0.4f);
        part.addOrReplaceChild(RIGHT_BOOT, CubeListBuilder.create().addBox(-2, 7, -2, 4, 5, 4, scale), PartPose.offset(1.9f, 12, 0));
        part.addOrReplaceChild(RIGHT_TOES, CubeListBuilder.create().texOffs(2, 9).addBox(-2, 10, -4, 4, 2, 2, scale), PartPose.offset(1.9f, 12, 0));
        part.addOrReplaceChild(LEFT_BOOT, CubeListBuilder.create().texOffs(16, 0).addBox(-2, 7, -2, 4, 5, 4, scale), PartPose.offset(-1.9f, 12, 0));
        part.addOrReplaceChild(LEFT_TOES, CubeListBuilder.create().texOffs(18, 9).addBox(-2, 10, -4, 4, 2, 2, scale), PartPose.offset(-1.9f, 12, 0));
        return LayerDefinition.create(mesh, 32, 16);
    }

    private static ClothingBootsModel instance;

    public static ClothingBootsModel getAdjustedInstance(HumanoidModel<?> wearerModel) {
        if (instance == null) {
            instance = new ClothingBootsModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOTHING_BOOTS));
        }
        instance.copyFromHumanoid(wearerModel);
        return instance;
    }

    public final @NotNull ModelPart rightBoot;
    public final @NotNull ModelPart leftBoot;
    public final @NotNull ModelPart leftToes;
    public final @NotNull ModelPart rightToes;


    public ClothingBootsModel(@NotNull ModelPart part) {
        super();
        this.rightBoot = part.getChild(RIGHT_BOOT);
        this.rightToes = part.getChild(RIGHT_TOES);
        this.leftBoot = part.getChild(LEFT_BOOT);
        this.leftToes = part.getChild(LEFT_TOES);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getLeftLegModels() {
        return ImmutableList.of(this.leftBoot, this.leftToes);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getRightLegModels() {
        return ImmutableList.of(this.rightBoot, this.rightToes);
    }
}
