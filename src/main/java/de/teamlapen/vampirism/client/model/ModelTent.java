package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created by Max on 16.08.2015.
 */
public class ModelTent extends ModelBase {
    //fields
    ModelRenderer LeftSide;
    ModelRenderer RightSide;
    ModelRenderer Bottom;
    ModelRenderer Front1;
    ModelRenderer Front2;
    ModelRenderer Shape1;

    public ModelTent() {
        textureWidth = 256;
        textureHeight = 128;

        LeftSide = new ModelRenderer(this, 0, 35);
        LeftSide.addBox(0F, 0F, 0F, 32, 23, 1);
        LeftSide.setRotationPoint(-8F, 7F, 8F);
        LeftSide.setTextureSize(64, 32);
        LeftSide.mirror = true;
        setRotation(LeftSide, 0.7504916F, 0F, 0F);
        RightSide = new ModelRenderer(this, 0, 35);
        RightSide.addBox(0F, 0F, 0F, 32, 23, 1);
        RightSide.setRotationPoint(-8F, 7F, 8F);
        RightSide.setTextureSize(64, 32);
        RightSide.mirror = true;
        setRotation(RightSide, -0.7853982F, 0F, 0F);
        Bottom = new ModelRenderer(this, 0, 0);
        Bottom.addBox(0F, 0F, 0F, 32, 1, 32);
        Bottom.setRotationPoint(-8F, 23F, -8F);
        Bottom.setTextureSize(64, 32);
        Bottom.mirror = true;
        setRotation(Bottom, 0F, 0F, 0F);
        Front1 = new ModelRenderer(this, 132, 0);
        Front1.addBox(0F, 0F, 0F, 1, 23, 23);
        Front1.setRotationPoint(-8F, 23F, -8F);
        Front1.setTextureSize(64, 32);
        Front1.mirror = true;
        setRotation(Front1, 0.7679449F, 0F, 0F);
        Front2 = new ModelRenderer(this, 183, 0);
        Front2.addBox(0F, 0F, 0F, 1, 23, 23);
        Front2.setRotationPoint(23F, 23F, -8F);
        Front2.setTextureSize(64, 32);
        Front2.mirror = true;
        setRotation(Front2, 0.7679449F, 0F, 0F);
        Shape1 = new ModelRenderer(this, 4, 63);
        Shape1.addBox(0F, 0F, 0F, 24, 2, 12);
        Shape1.setRotationPoint(-5F, 22F, 2F);
        Shape1.setTextureSize(256, 128);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        LeftSide.render(f5);
        RightSide.render(f5);
        Bottom.render(f5);
        Front1.render(f5);
        Front2.render(f5);
        Shape1.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}
