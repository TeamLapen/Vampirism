package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloakModel<T extends LivingEntity> extends BipedModel<T> {
    RendererModel cloakback;
    RendererModel leftlong;
    RendererModel rightmedium;
    RendererModel leftmedium;
    RendererModel rightshort;
    RendererModel leftshort;
    RendererModel rightlong;
    RendererModel shoulderright;
    RendererModel shoulderleft;

    public CloakModel() {
        super(0.0F, 0.0F, 64, 64);
        cloakback = new RendererModel(this, 0, 48);
        cloakback.addBox(-4F, 0F, 0F, 8, 15, 1);
        cloakback.setRotationPoint(0F, 0.2F, 2F);
        cloakback.mirror = true;
        setRotation(cloakback, 0.0872665F, 0F, 0F);
        leftlong = new RendererModel(this, 18, 48);
        leftlong.addBox(4F, 0F, 0F, 1, 15, 1);
        leftlong.setRotationPoint(0F, 0.2F, 2F);
        leftlong.mirror = true;
        setRotation(leftlong, 0.0872665F, 0F, 0F);
        rightmedium = new RendererModel(this, 22, 50);
        rightmedium.addBox(-5F, 0F, -1F, 1, 13, 1);
        rightmedium.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightmedium, 0.0872665F, 0F, 0F);
        leftmedium = new RendererModel(this, 22, 50);
        leftmedium.addBox(4F, 0F, -1F, 1, 13, 1);
        leftmedium.setRotationPoint(0F, 0.2F, 2F);
        leftmedium.mirror = true;
        setRotation(leftmedium, 0.0872665F, 0F, 0F);
        rightshort = new RendererModel(this, 26, 52);
        rightshort.addBox(-5F, 0F, -2F, 1, 11, 1);
        rightshort.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightshort, 0.0872665F, 0F, 0F);
        leftshort = new RendererModel(this, 26, 52);
        leftshort.addBox(4F, 0F, -2F, 1, 11, 1);
        leftshort.setRotationPoint(0F, 0.2F, 2F);
        leftshort.mirror = true;
        setRotation(leftshort, 0.0872665F, 0F, 0F);
        rightlong = new RendererModel(this, 18, 48);
        rightlong.addBox(-5F, 0F, 0F, 1, 15, 1);
        rightlong.setRotationPoint(0F, 0.2F, 2F);
        setRotation(rightlong, 0.0872665F, 0F, 0F);
        shoulderright = new RendererModel(this, 30, 60);
        shoulderright.addBox(0F, 0F, 0F, 1, 1, 3);
        shoulderright.setRotationPoint(-5F, 0F, 0F);
        setRotation(shoulderright, 0F, 0F, 0F);
        shoulderleft = new RendererModel(this, 30, 60);
        shoulderleft.addBox(0F, 0F, 0F, 1, 1, 3);
        shoulderleft.setRotationPoint(4F, 0F, 0F);
        shoulderleft.mirror = true;
        setRotation(shoulderleft, 0F, 0F, 0F);
    }

    @Override
    public void render(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        cloakback.render(f5);
        leftlong.render(f5);
        rightmedium.render(f5);
        leftmedium.render(f5);
        rightshort.render(f5);
        leftshort.render(f5);
        rightlong.render(f5);
        shoulderright.render(f5);
        shoulderleft.render(f5);
    }

    @Override
    public void setRotationAngles(T entityIn, float f, float f1, float f2, float f3, float f4, float f5) {
        boolean flag = entityIn instanceof LivingEntity && entityIn.getTicksElytraFlying() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entityIn.getMotion().x * entityIn.getMotion().x + entityIn.getMotion().y * entityIn.getMotion().y
                    + entityIn.getMotion().z * entityIn.getMotion().z);
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

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
