package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireBaronModel<T extends LivingEntity> extends BipedCloakedModel<T> {


    private ModelRenderer rightwing1;
    private ModelRenderer rightwing2;
    private ModelRenderer leftwing1;
    private ModelRenderer leftwing2;

    public VampireBaronModel() {
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

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(rightwing1, rightwing2, leftwing1, leftwing2));
    }


    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}