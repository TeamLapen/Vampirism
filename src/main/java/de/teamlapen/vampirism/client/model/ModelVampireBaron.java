package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelVampireBaron<T extends LivingEntity> extends ModelBipedCloaked<T> {


    private RendererModel rightwing1;
    private RendererModel rightwing2;
    private RendererModel leftwing1;
    private RendererModel leftwing2;

    public ModelVampireBaron() {
        super(0.0F, 0.0F, 64, 64, 38, 34);
//        textureWidth = 64;
//        textureHeight = 64;

        int wingx = 0;
        int wingy = 49;
        rightwing1 = new RendererModel(this, wingx, wingy);
        rightwing1.addBox(-8F, -1.5F, -1.5F, 8, 3, 3);
        rightwing1.setRotationPoint(-6F, 2F, 1F);
        rightwing1.setTextureSize(64, 64);
        rightwing1.mirror = true;
        setRotation(rightwing1, 0F, 0F, 0.7853982F);
        rightwing2 = new RendererModel(this, wingx, wingy);
        rightwing2.addBox(-11.5F, -6.5F, -1.5F, 7, 3, 3);
        rightwing2.setRotationPoint(-6F, 2F, 1F);
        rightwing2.setTextureSize(64, 64);
        rightwing2.mirror = true;
        setRotation(rightwing2, 0F, 0F, 0F);
        leftwing1 = new RendererModel(this, wingx, wingy);
        leftwing1.addBox(0F, -1.5F, -1.5F, 8, 3, 3);
        leftwing1.setRotationPoint(6F, 2F, 1F);
        leftwing1.setTextureSize(64, 64);
        leftwing1.mirror = true;
        setRotation(leftwing1, 0F, 0F, -0.7853982F);
        leftwing2 = new RendererModel(this, wingx, wingy);
        leftwing2.addBox(4.5F, -6.5F, -1.5F, 7, 3, 3);
        leftwing2.setRotationPoint(6F, 2F, 1F);
        leftwing2.setTextureSize(64, 64);
        leftwing2.mirror = true;
        setRotation(leftwing2, 0F, 0F, 0F);
    }

    @Override
    public void render(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        rightwing1.render(f5);
        rightwing2.render(f5);
        leftwing1.render(f5);
        leftwing2.render(f5);
    }

    @Override
    public void setRotationAngles(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    }

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}