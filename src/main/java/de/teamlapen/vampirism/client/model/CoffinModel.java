package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@OnlyIn(Dist.CLIENT)
public class CoffinModel extends Model {

    private static final String LEFT_PLATE = "left_plate";
    private static final String RIGHT_PLATE = "right_plate";
    private static final String BACK_PLATE = "back_plate";
    private static final String TOP_PLATE = "top_plate";
    private static final String BOTTOM_PLATE = "bottom_plate";
    private static final String LEFT_LID = "left_lid";
    private static final String RIGHT_LID = "right_lid";
    private static final String LEFT_HANDLE = "left_handle";
    private static final String RIGHT_HANDLE = "right_handle";

    // fields
    private final ModelPart leftPlate;
    private final ModelPart rightPlate;
    private final ModelPart backPlate;
    private final ModelPart topPlate;
    private final ModelPart bottomPlate;
    private final ModelPart leftLid;
    private final ModelPart rightLid;
    private final ModelPart leftHandle;
    private final ModelPart rightHandle;

    private final List<ModelPart> modelParts;

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild(LEFT_PLATE, CubeListBuilder.create().texOffs(0, 64).mirror().addBox(7, -12, 0, 1, 12, 32), PartPose.offset(0, 23, -8));
        part.addOrReplaceChild(RIGHT_PLATE, CubeListBuilder.create().texOffs(66, 64).mirror().addBox(-8, -12, 0, 1, 12, 32), PartPose.offset(0, 23, -8));
        part.addOrReplaceChild(BACK_PLATE, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8, 0, 0, 16, 1, 32), PartPose.offset(0, 23, -8));
        part.addOrReplaceChild(TOP_PLATE, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-7, -12, 31, 14, 12, 1), PartPose.offset(0, 23, -8));
        part.addOrReplaceChild(BOTTOM_PLATE, CubeListBuilder.create().texOffs(0, 15).mirror().addBox(-7, -11, 0, 14, 12, 1), PartPose.offset(0, 22, -8));
        part.addOrReplaceChild(LEFT_LID, CubeListBuilder.create().texOffs(0, 33).mirror().addBox(0, 0, 0, 7, 1, 30), PartPose.offset(-7, 11, -7));
        part.addOrReplaceChild(RIGHT_LID, CubeListBuilder.create().texOffs(74, 33).mirror().addBox(-7, 0, 0, 7, 1, 30), PartPose.offset(7, 11, -7));
        part.addOrReplaceChild(LEFT_HANDLE, CubeListBuilder.create().texOffs(64, 0).mirror().addBox(5.5f, -0.5f, 15f, 1, 1, 4), PartPose.offset(-7, 11, -7));
        part.addOrReplaceChild(RIGHT_HANDLE, CubeListBuilder.create().texOffs(74, 0).mirror().addBox(-6.5f, -0.5f, 15, 1, 1, 1), PartPose.offset(7, 11, -7));
        return LayerDefinition.create(mesh, 256, 128);
    }

    public CoffinModel(ModelPart part) {
        super(RenderType::entitySolid);
        this.leftPlate = part.getChild(LEFT_PLATE);
        this.rightPlate = part.getChild(RIGHT_PLATE);
        this.backPlate = part.getChild(BACK_PLATE);
        this.topPlate = part.getChild(TOP_PLATE);
        this.bottomPlate = part.getChild(BOTTOM_PLATE);
        this.leftLid = part.getChild(LEFT_LID);
        this.rightLid = part.getChild(RIGHT_LID);
        this.leftHandle = part.getChild(LEFT_HANDLE);
        this.rightHandle = part.getChild(RIGHT_HANDLE);
        modelParts = ImmutableList.of(this.leftPlate, this.rightPlate, this.backPlate, this.topPlate, this.bottomPlate, this.leftLid, this.rightLid, this.leftHandle, this.rightHandle);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStack, @NotNull VertexConsumer iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {
        modelParts.forEach(part -> part.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3));
    }


    public void rotateLid(float angle) {
        leftLid.zRot = leftHandle.zRot = -angle;
        rightLid.zRot = rightHandle.zRot = angle;
    }
}
