package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloakModel<T extends LivingEntity> extends BipedModel<T> {
    ModelRenderer cloakback;
    ModelRenderer leftlong;
    ModelRenderer rightmedium;
    ModelRenderer leftmedium;
    ModelRenderer rightshort;
    ModelRenderer leftshort;
    ModelRenderer rightlong;
    ModelRenderer shoulderright;
    ModelRenderer shoulderleft;

    public CloakModel() {
        super(0.0F, 0.0F, 64, 64);
        cloakback = new ModelRenderer(this, 0, 48);
        cloakback.addBox(-4F, 0F, 0F, 8, 15, 1);
        cloakback.setRotationPoint(0F, 0.2F, 2F);
        cloakback.mirror = true;
        setRotation(cloakback, 0.0872665F, 0F, 0F);
        leftlong = new ModelRenderer(this, 18, 48);
        leftlong.addBox(4F, 0F, 0F, 1, 15, 1);
        leftlong.setRotationPoint(0F, 0.2F, 2F);
        leftlong.mirror = true;
        setRotation(leftlong, 0.0872665F, 0F, 0F);
        rightmedium = new ModelRenderer(this, 22, 50);
        rightmedium.addBox(-5F, 0F, -1F, 1, 13, 1);
        rightmedium.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightmedium, 0.0872665F, 0F, 0F);
        leftmedium = new ModelRenderer(this, 22, 50);
        leftmedium.addBox(4F, 0F, -1F, 1, 13, 1);
        leftmedium.setRotationPoint(0F, 0.2F, 2F);
        leftmedium.mirror = true;
        setRotation(leftmedium, 0.0872665F, 0F, 0F);
        rightshort = new ModelRenderer(this, 26, 52);
        rightshort.addBox(-5F, 0F, -2F, 1, 11, 1);
        rightshort.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightshort, 0.0872665F, 0F, 0F);
        leftshort = new ModelRenderer(this, 26, 52);
        leftshort.addBox(4F, 0F, -2F, 1, 11, 1);
        leftshort.setRotationPoint(0F, 0.2F, 2F);
        leftshort.mirror = true;
        setRotation(leftshort, 0.0872665F, 0F, 0F);
        rightlong = new ModelRenderer(this, 18, 48);
        rightlong.addBox(-5F, 0F, 0F, 1, 15, 1);
        rightlong.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightlong, 0.0872665F, 0F, 0F);
        shoulderright = new ModelRenderer(this, 30, 60);
        shoulderright.addBox(0F, 0F, 0F, 1, 1, 3);
        shoulderright.setRotationPoint(-5F, 0F, 0F);
        setRotation(shoulderright, 0F, 0F, 0F);
        shoulderleft = new ModelRenderer(this, 30, 60);
        shoulderleft.addBox(0F, 0F, 0F, 1, 1, 3);
        shoulderleft.setRotationPoint(4F, 0F, 0F);
        shoulderleft.mirror = true;
        setRotation(shoulderleft, 0F, 0F, 0F);
    }

    @Override
    public void render(T entity, float f, float f1, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.render(entity, f, f1, p_225597_4_, p_225597_5_, p_225597_6_);
        boolean flag = entity != null && entity.getTicksElytraFlying() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entity.getMotion().x * entity.getMotion().x + entity.getMotion().y * entity.getMotion().y
                    + entity.getMotion().z * entity.getMotion().z);
            f6 = f6 / 0.2F;
            f6 = f6 * f6 * f6;
        }

        if (f6 < 1.0F) {
            f6 = 1.0F;
        }

        float rotation = MathHelper.cos(f * 0.6662F) * 1.4F * f1 / f6;
        if (rotation < 0.0F)
            rotation *= -1;
        this.cloakback.rotateAngleX = 0.0872665F + (rotation / 3);
        this.leftlong.rotateAngleX = 0.0872665F + (rotation / 3);
        this.rightlong.rotateAngleX = 0.0872665F + (rotation / 3);
        this.leftmedium.rotateAngleX = 0.0872665F + (rotation / 3);
        this.rightmedium.rotateAngleX = 0.0872665F + (rotation / 3);
        this.rightshort.rotateAngleX = 0.0872665F + (rotation / 3);
        this.leftshort.rotateAngleX = 0.0872665F + (rotation / 3);

        if (this.isSneak) {
            this.cloakback.rotateAngleX += 0.5F;
            this.leftlong.rotateAngleX += 0.5F;
            this.rightlong.rotateAngleX += 0.5F;
            this.leftmedium.rotateAngleX += 0.5F;
            this.rightmedium.rotateAngleX += 0.5F;
            this.leftshort.rotateAngleX += 0.5F;
            this.rightshort.rotateAngleX += 0.5F;
        }
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(cloakback, leftlong, rightmedium, leftmedium, rightshort, leftshort, rightlong, shoulderright, shoulderleft);
    }


    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
