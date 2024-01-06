package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


import org.jetbrains.annotations.NotNull;

public class ClothingPantsModel extends VampirismArmorModel {

    private static final String LEFT_LEG = "left_leg";
    private static final String RIGHT_LEG = "right_leg";
    private static final String BELT = "belt";

    private static ClothingPantsModel instance;

    public static ClothingPantsModel getAdjustedInstance(HumanoidModel<?> wearerModel) {
        if (instance == null) {
            instance = new ClothingPantsModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOTHING_PANTS));
        }
        instance.copyFromHumanoid(wearerModel);
        return instance;
    }

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        CubeDeformation def = new CubeDeformation(0.25f);
        part.addOrReplaceChild(LEFT_LEG, CubeListBuilder.create().texOffs(16, 0).addBox(-2, 0, -2, 4, 12, 4, def), PartPose.offset(-4, 12, 0));
        part.addOrReplaceChild(RIGHT_LEG, CubeListBuilder.create().addBox(-2, 0, -2, 4, 12, 4, def), PartPose.offset(1.9f, 12, 0));
        part.addOrReplaceChild(BELT, CubeListBuilder.create().texOffs(4, 16).addBox(-4, 7, -2, 8, 5, 4, def), PartPose.ZERO);
        return LayerDefinition.create(mesh, 32, 32);
    }

    public final @NotNull ModelPart rightLeg;
    public final @NotNull ModelPart leftLeg;
    public final @NotNull ModelPart belt;

    public ClothingPantsModel(@NotNull ModelPart part) {
        super();
        this.belt = part.getChild(BELT);
        this.leftLeg = part.getChild(LEFT_LEG);
        this.rightLeg = part.getChild(RIGHT_LEG);

    }

    @Override
    protected @NotNull Iterable<ModelPart> getBodyModels() {
        return ImmutableList.of(this.belt);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getLeftLegModels() {
        return ImmutableList.of(this.leftLeg);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getRightLegModels() {
        return ImmutableList.of(this.rightLeg);
    }
}
