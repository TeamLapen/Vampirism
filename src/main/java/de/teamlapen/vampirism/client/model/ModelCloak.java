package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

/**
 * Model for Vampire Cloak
 */
public class ModelCloak extends ModelBiped {
    public ModelRenderer cloakback;
    public ModelRenderer cloakleft;
    public ModelRenderer cloakright;

    public ModelCloak() {
        super();
        textureHeight = 64;
        textureWidth = 64;

        cloakback = new ModelRenderer(this, 0, 33);
        cloakback.addBox(0F, 0F, 0F, 8, 15, 1);
        cloakback.setRotationPoint(-4F, 0F, 2F);
        cloakback.setTextureSize(128, 64);
        cloakback.mirror = true;
        setRotation(cloakback, 0.0872665F, 0F, 0F);
        cloakleft = new ModelRenderer(this, 0, 33);
        cloakleft.addBox(0F, 0F, 0F, 1, 15, 4);
        cloakleft.setRotationPoint(4F, 0.3F, -1F);
        cloakleft.setTextureSize(128, 64);
        cloakleft.mirror = true;
        setRotation(cloakleft, 0.0872665F, 0F, 0F);
        cloakright = new ModelRenderer(this, 0, 33);
        cloakright.addBox(0F, 0F, 0F, 1, 15, 4);
        cloakright.setRotationPoint(-5F, 0.3F, -1F);
        cloakright.setTextureSize(128, 64);
        cloakright.mirror = true;
        setRotation(cloakright, 0.0872665F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        setOffsets(f, f1, f2, f3, f4, f5, entity);
        cloakback.render(f5);
        cloakleft.render(f5);
        cloakright.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {

        boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getTicksElytraFlying() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY
                    + entityIn.motionZ * entityIn.motionZ);
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
        this.cloakleft.rotateAngleX = 0.0872665F + (rotation / 3);
        this.cloakright.rotateAngleX = 0.0872665F + (rotation / 3);

        if (this.isSneak) {
            this.cloakback.rotateAngleX += 0.5F;
            this.cloakleft.rotateAngleX += 0.5F;
            this.cloakright.rotateAngleX += 0.5F;
        }

    }

    /**
     * Sets the various offsets for movements
     */
    public void setOffsets(float f, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {

        boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getTicksElytraFlying() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY
                    + entityIn.motionZ * entityIn.motionZ);
            f6 = f6 / 0.2F;
            f6 = f6 * f6 * f6;
        }

        if (f6 < 1.0F) {
            f6 = 1.0F;
        }

        float rotation = MathHelper.cos(f * 0.6662F) * 1.4F * f1 / f6;
        if (rotation < 0.0F)
            rotation *= -1;
        this.cloakleft.offsetY = rotation / 30;
        this.cloakright.offsetY = rotation / 30;
        this.cloakback.offsetY = 0F;
        this.cloakleft.offsetZ = rotation / 50;
        this.cloakright.offsetZ = rotation / 50;

        if (this.isSneak) {
            this.cloakback.offsetY += 0.13F;
            this.cloakleft.offsetY += 0.21F;
            this.cloakright.offsetY += 0.21F;
            this.cloakleft.offsetZ += 0.035F;
            this.cloakright.offsetZ += 0.035F;
        }

    }
}
