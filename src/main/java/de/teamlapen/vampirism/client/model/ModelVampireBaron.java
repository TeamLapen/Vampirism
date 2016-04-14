package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVampireBaron extends ModelBipedCloaked {


    private ModelRenderer rightwing1;
    private ModelRenderer rightwing2;
    private ModelRenderer leftwing1;
    private ModelRenderer leftwing2;

    public ModelVampireBaron() {
        super(0.0F, 0.0F, 64, 64, 38, 34);
//        textureWidth = 64;
//        textureHeight = 64;

        int wingx = 0;
        int wingy = 49;
        rightwing1 = new ModelRenderer(this, wingx, wingy);
        rightwing1.addBox(-8F, -1.5F, -1.5F, 8, 3, 3);
        rightwing1.setRotationPoint(-6F, 2F, 1F);
        rightwing1.setTextureSize(64, 64);
        rightwing1.mirror = true;
        setRotation(rightwing1, 0F, 0F, 0.7853982F);
        rightwing2 = new ModelRenderer(this, wingx, wingy);
        rightwing2.addBox(-11.5F, -6.5F, -1.5F, 7, 3, 3);
        rightwing2.setRotationPoint(-6F, 2F, 1F);
        rightwing2.setTextureSize(64, 64);
        rightwing2.mirror = true;
        setRotation(rightwing2, 0F, 0F, 0F);
        leftwing1 = new ModelRenderer(this, wingx, wingy);
        leftwing1.addBox(0F, -1.5F, -1.5F, 8, 3, 3);
        leftwing1.setRotationPoint(6F, 2F, 1F);
        leftwing1.setTextureSize(64, 64);
        leftwing1.mirror = true;
        setRotation(leftwing1, 0F, 0F, -0.7853982F);
        leftwing2 = new ModelRenderer(this, wingx, wingy);
        leftwing2.addBox(4.5F, -6.5F, -1.5F, 7, 3, 3);
        leftwing2.setRotationPoint(6F, 2F, 1F);
        leftwing2.setTextureSize(64, 64);
        leftwing2.mirror = true;
        setRotation(leftwing2, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        rightwing1.render(f5);
        rightwing2.render(f5);
        leftwing1.render(f5);
        leftwing2.render(f5);
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}