package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class ClothingCrownModel extends VampirismArmorModel {

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String FRONT = "front";
    private static final String BACK = "back";


    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        CubeDeformation def1 = new CubeDeformation(-0.2F, 0.1F, -0.1F);
        part.addOrReplaceChild(LEFT, CubeListBuilder.create().texOffs(0, 6).addBox(-5.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(-0.2F, 0.1F, 0.1F)).texOffs(12, 17).addBox(-5.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, def1).texOffs(0, 17).addBox(-5.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, def1).texOffs(6, 17).addBox(-5.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F, 0.0F, 0.1F)).texOffs(13, 15).addBox(-5.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, def1).texOffs(1, 15).addBox(-5.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, def1).texOffs(7, 15).addBox(-5.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F, 0.0F, -0.05F)), PartPose.ZERO);
        CubeDeformation def2 = new CubeDeformation(-0.1F, 0.0F, -0.2F);
        part.addOrReplaceChild(FRONT, CubeListBuilder.create().texOffs(0, 4).addBox(-4.0F, -7.7F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.1F, 0.1F, -0.2F))
                .texOffs(0, 2).addBox(-3.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, def2)
                .texOffs(12, 2).addBox(1.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, def2)
                .texOffs(6, 2).addBox(-1.0F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.1F, 0.0F, -0.2F))
                .texOffs(1, 0).addBox(-3.25F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, def2)
                .texOffs(13, 0).addBox(2.2F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, def2)
                .texOffs(7, 0).addBox(-0.5F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F, 0.0F, -0.2F)), PartPose.ZERO);
        part.addOrReplaceChild(BACK, CubeListBuilder.create().texOffs(18, 4).addBox(-4.0F, -7.7F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.1F, 0.1F, -0.2F))
                .texOffs(30, 2).addBox(-3.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, def2)
                .texOffs(18, 2).addBox(1.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, def2)
                .texOffs(24, 2).addBox(-1.0F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.1F, 0.0F, -0.2F))
                .texOffs(31, 0).addBox(-3.25F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, def2)
                .texOffs(19, 0).addBox(2.2F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, def2)
                .texOffs(25, 0).addBox(-0.5F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F, 0.0F, -0.2F)), PartPose.ZERO);
        part.addOrReplaceChild(RIGHT, CubeListBuilder.create().texOffs(18, 6).addBox(4.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(-0.2F, 0.1F, 0.1F))
                .texOffs(18, 17).addBox(4.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, def1)
                .texOffs(30, 17).addBox(4.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, def1)
                .texOffs(24, 17).addBox(4.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F, 0.0F, 0.1F))
                .texOffs(19, 15).addBox(4.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, def1)
                .texOffs(31, 15).addBox(4.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, def1)
                .texOffs(25, 15).addBox(4.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F, 0.0F, -0.05F)), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    private static ClothingCrownModel instance;

    public static ClothingCrownModel getAdjustedInstance(HumanoidModel<?> wearerModel) {
        if (instance == null) {
            instance = new ClothingCrownModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOTHING_CROWN));
        }
        instance.copyFromHumanoid(wearerModel);
        return instance;
    }

    public final @NotNull ModelPart front;
    public final @NotNull ModelPart back;
    public final @NotNull ModelPart left;
    public final @NotNull ModelPart right;

    public ClothingCrownModel(@NotNull ModelPart part) {
        super();
        front = part.getChild(FRONT);
        back = part.getChild(BACK);
        left = part.getChild(LEFT);
        right = part.getChild(RIGHT);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getHeadModels() {
        return ImmutableList.of(this.front, this.back, this.right, this.left);
    }
}
