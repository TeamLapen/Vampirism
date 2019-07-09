package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CoffinModel extends Model {

    // fields
    private RendererModel leftPlate;
    private RendererModel rightPlate;
    private RendererModel backPlate;
    private RendererModel topPlate;
    private RendererModel bottomPlate;
    private RendererModel leftLid;
    private RendererModel rightLid;
    private RendererModel leftHandle;
    private RendererModel rightHandle;

    public CoffinModel() {
        textureWidth = 256;
        textureHeight = 128;

        leftPlate = new RendererModel(this, 0, 64);
        leftPlate.addBox(-8F, -12F, 0F, 1, 12, 32);
        leftPlate.setRotationPoint(0F, 23F, -8F);
        leftPlate.setTextureSize(256, 128);
        leftPlate.mirror = true;
        setRotation(leftPlate, 0F, 0F, 0F);
        rightPlate = new RendererModel(this, 66, 64);
        rightPlate.addBox(7F, -12F, 0F, 1, 12, 32);
        rightPlate.setRotationPoint(0F, 23F, -8F);
        rightPlate.setTextureSize(256, 128);
        rightPlate.mirror = true;
        setRotation(rightPlate, 0F, 0F, 0F);
        backPlate = new RendererModel(this, 0, 0);
        backPlate.addBox(-8F, 0F, 0F, 16, 1, 32);
        backPlate.setRotationPoint(0F, 23F, -8F);
        backPlate.setTextureSize(256, 128);
        backPlate.mirror = true;
        setRotation(backPlate, 0F, 0F, 0F);
        topPlate = new RendererModel(this, 0, 0);
        topPlate.addBox(-7F, -12F, 31F, 14, 12, 1);
        topPlate.setRotationPoint(0F, 23F, -8F);
        topPlate.setTextureSize(256, 128);
        topPlate.mirror = true;
        setRotation(topPlate, 0F, 0F, 0F);
        bottomPlate = new RendererModel(this, 0, 15);
        bottomPlate.addBox(-7F, -11F, 0F, 14, 12, 1);
        bottomPlate.setRotationPoint(0F, 22F, -8F);
        bottomPlate.setTextureSize(256, 128);
        bottomPlate.mirror = true;
        setRotation(bottomPlate, 0F, 0F, 0F);
        leftLid = new RendererModel(this, 0, 33);
        leftLid.addBox(0F, 0F, 0F, 7, 1, 30);
        leftLid.setRotationPoint(-7F, 11F, -7F);
        leftLid.setTextureSize(256, 128);
        leftLid.mirror = true;
        setRotation(leftLid, 0F, 0F, 0F);
        rightLid = new RendererModel(this, 74, 33);
        rightLid.addBox(-7F, 0F, 0F, 7, 1, 30);
        rightLid.setRotationPoint(7F, 11F, -7F);
        rightLid.setTextureSize(256, 128);
        rightLid.mirror = true;
        setRotation(rightLid, 0F, 0F, 0F);
        leftHandle = new RendererModel(this, 64, 0);
        leftHandle.addBox(5.5F, -0.5F, 15F, 1, 1, 4);
        leftHandle.setRotationPoint(-7F, 11F, -7F);
        leftHandle.setTextureSize(256, 128);
        leftHandle.mirror = true;
        setRotation(leftHandle, 0F, 0F, 0F);
        rightHandle = new RendererModel(this, 74, 0);
        rightHandle.addBox(-6.5F, -0.5F, 15F, 1, 1, 4);
        rightHandle.setRotationPoint(7F, 11F, -7F);
        rightHandle.setTextureSize(256, 128);
        rightHandle.mirror = true;
        setRotation(rightHandle, 0F, 0F, 0F);
    }

    public void render(float f5) {
        leftPlate.render(f5);
        rightPlate.render(f5);
        backPlate.render(f5);
        topPlate.render(f5);
        bottomPlate.render(f5);
        leftLid.render(f5);
        rightLid.render(f5);
        leftHandle.render(f5);
        rightHandle.render(f5);
    }

    public void rotateLid(float angle) {
        leftLid.rotateAngleZ = leftHandle.rotateAngleZ = -angle;
        rightLid.rotateAngleZ = rightHandle.rotateAngleZ = angle;
    }

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
