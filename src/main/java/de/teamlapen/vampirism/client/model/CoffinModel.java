package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CoffinModel extends Model {

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

    public CoffinModel() {
        super(RenderType::entitySolid);
        texWidth = 256;
        texHeight = 128;

        leftPlate = new ModelPart(this, 0, 64);
        leftPlate.addBox(-8F, -12F, 0F, 1, 12, 32);
        leftPlate.setPos(0F, 23F, -8F);
        leftPlate.setTexSize(256, 128);
        leftPlate.mirror = true;
        setRotation(leftPlate, 0F, 0F, 0F);
        rightPlate = new ModelPart(this, 66, 64);
        rightPlate.addBox(7F, -12F, 0F, 1, 12, 32);
        rightPlate.setPos(0F, 23F, -8F);
        rightPlate.setTexSize(256, 128);
        rightPlate.mirror = true;
        setRotation(rightPlate, 0F, 0F, 0F);
        backPlate = new ModelPart(this, 0, 0);
        backPlate.addBox(-8F, 0F, 0F, 16, 1, 32);
        backPlate.setPos(0F, 23F, -8F);
        backPlate.setTexSize(256, 128);
        backPlate.mirror = true;
        setRotation(backPlate, 0F, 0F, 0F);
        topPlate = new ModelPart(this, 0, 0);
        topPlate.addBox(-7F, -12F, 31F, 14, 12, 1);
        topPlate.setPos(0F, 23F, -8F);
        topPlate.setTexSize(256, 128);
        topPlate.mirror = true;
        setRotation(topPlate, 0F, 0F, 0F);
        bottomPlate = new ModelPart(this, 0, 15);
        bottomPlate.addBox(-7F, -11F, 0F, 14, 12, 1);
        bottomPlate.setPos(0F, 22F, -8F);
        bottomPlate.setTexSize(256, 128);
        bottomPlate.mirror = true;
        setRotation(bottomPlate, 0F, 0F, 0F);
        leftLid = new ModelPart(this, 0, 33);
        leftLid.addBox(0F, 0F, 0F, 7, 1, 30);
        leftLid.setPos(-7F, 11F, -7F);
        leftLid.setTexSize(256, 128);
        leftLid.mirror = true;
        setRotation(leftLid, 0F, 0F, 0F);
        rightLid = new ModelPart(this, 74, 33);
        rightLid.addBox(-7F, 0F, 0F, 7, 1, 30);
        rightLid.setPos(7F, 11F, -7F);
        rightLid.setTexSize(256, 128);
        rightLid.mirror = true;
        setRotation(rightLid, 0F, 0F, 0F);
        leftHandle = new ModelPart(this, 64, 0);
        leftHandle.addBox(5.5F, -0.5F, 15F, 1, 1, 4);
        leftHandle.setPos(-7F, 11F, -7F);
        leftHandle.setTexSize(256, 128);
        leftHandle.mirror = true;
        setRotation(leftHandle, 0F, 0F, 0F);
        rightHandle = new ModelPart(this, 74, 0);
        rightHandle.addBox(-6.5F, -0.5F, 15F, 1, 1, 4);
        rightHandle.setPos(7F, 11F, -7F);
        rightHandle.setTexSize(256, 128);
        rightHandle.mirror = true;
        setRotation(rightHandle, 0F, 0F, 0F);
        modelParts = ImmutableList.of(this.leftPlate, this.rightPlate, this.backPlate, this.topPlate, this.bottomPlate, this.leftLid, this.rightLid, this.leftHandle, this.rightHandle);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {
        modelParts.forEach(part -> part.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3));
    }


    public void rotateLid(float angle) {
        leftLid.zRot = leftHandle.zRot = -angle;
        rightLid.zRot = rightHandle.zRot = angle;
    }

    private void setRotation(ModelPart model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }
}
