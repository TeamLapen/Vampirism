package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloakModel extends VampirismArmorModel {

    private static CloakModel cloakItemModel;

    public static CloakModel getRotatedCloak() {
        if (cloakItemModel == null) {
            cloakItemModel = new CloakModel();
        }
        return cloakItemModel;
    }

    private final ModelRenderer cloakback;
    private final ModelRenderer leftlong;
    private final ModelRenderer rightmedium;
    private final ModelRenderer leftmedium;
    private final ModelRenderer rightshort;
    private final ModelRenderer leftshort;
    private final ModelRenderer rightlong;
    private final ModelRenderer shoulderright;
    private final ModelRenderer shoulderleft;

    public CloakModel() {
        super(64, 64);
        cloakback = new ModelRenderer(this, 0, 48);
        cloakback.addBox(-4F, 0F, 2F, 8, 15, 1);
        cloakback.setPos(0F, 0.2F, 2F);
        cloakback.mirror = true;
        setRotation(cloakback, 0.0872665F, 0F, 0F);
        leftlong = new ModelRenderer(this, 18, 48);
        leftlong.addBox(4F, 0F, 2F, 1, 15, 1);
        leftlong.setPos(0F, 0.2F, 2F);
        leftlong.mirror = true;
        setRotation(leftlong, 0.0872665F, 0F, 0F);
        rightmedium = new ModelRenderer(this, 22, 50);
        rightmedium.addBox(-5F, 0F, 1F, 1, 13, 1);
        rightmedium.setPos(0F, 0.2F, 2F);
        setRotation(rightmedium, 0.0872665F, 0F, 0F);
        leftmedium = new ModelRenderer(this, 22, 50);
        leftmedium.addBox(4F, 0F, 1F, 1, 13, 1);
        leftmedium.setPos(0F, 0.2F, 2F);
        leftmedium.mirror = true;
        setRotation(leftmedium, 0.0872665F, 0F, 0F);
        rightshort = new ModelRenderer(this, 26, 52);
        rightshort.addBox(-5F, 0F, 0F, 1, 11, 1);
        rightshort.setPos(0F, 0.2F, 2F);
        setRotation(rightshort, 0.0872665F, 0F, 0F);
        leftshort = new ModelRenderer(this, 26, 52);
        leftshort.addBox(4F, 0F, 0F, 1, 11, 1);
        leftshort.setPos(0F, 0.2F, 2F);
        leftshort.mirror = true;
        setRotation(leftshort, 0.0872665F, 0F, 0F);
        rightlong = new ModelRenderer(this, 18, 48);
        rightlong.addBox(-5F, 0F, 2F, 1, 15, 1);
        rightlong.setPos(0F, 0.2F, 2F);
        setRotation(rightlong, 0.0872665F, 0F, 0F);
        shoulderright = new ModelRenderer(this, 30, 60);
        shoulderright.addBox(-4F, 0F, 0F, 1, 1, 3);
        shoulderright.setPos(-5F, 0F, 0F);
        setRotation(shoulderright, 0F, 0F, 0F);
        shoulderleft = new ModelRenderer(this, 30, 60);
        shoulderleft.addBox(3F, 0F, 0F, 1, 1, 3);
        shoulderleft.setPos(-5F, 0F, 0F);
        shoulderleft.mirror = true;
        setRotation(shoulderleft, 0F, 0F, 0F);
        getBodyModels().forEach(this.body::addChild);  //Make sure hierarchy is correct (e.g. for EpicFightMod)
    }

    @Override
    public void setupAnim(LivingEntity entity, float f, float f1, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, f, f1, ageInTicks, netHeadYaw, headPitch);
        //Isn't use afaik


        boolean flag = entity != null && entity.getFallFlyingTicks() > 4;

        float f6 = 1.0F;
        if (flag) {
            f6 = (float) (entity.getDeltaMovement().x * entity.getDeltaMovement().x + entity.getDeltaMovement().y * entity.getDeltaMovement().y
                    + entity.getDeltaMovement().z * entity.getDeltaMovement().z);
            f6 = f6 / 0.2F;
            f6 = f6 * f6 * f6;
        }

        if (f6 < 1.0F) {
            f6 = 1.0F;
        }

        float rotation = MathHelper.cos(f * 0.6662F) * 1.4F * f1 / f6;
        if (rotation < 0.0F)
            rotation *= -1;
        this.cloakback.xRot = 0.0872665F + (rotation / 3);
        this.leftlong.xRot = 0.0872665F + (rotation / 3);
        this.rightlong.xRot = 0.0872665F + (rotation / 3);
        this.leftmedium.xRot = 0.0872665F + (rotation / 3);
        this.rightmedium.xRot = 0.0872665F + (rotation / 3);
        this.rightshort.xRot = 0.0872665F + (rotation / 3);
        this.leftshort.xRot = 0.0872665F + (rotation / 3);

        if (this.crouching) {
            this.cloakback.xRot += 0.5F;
            this.leftlong.xRot += 0.5F;
            this.rightlong.xRot += 0.5F;
            this.leftmedium.xRot += 0.5F;
            this.rightmedium.xRot += 0.5F;
            this.leftshort.xRot += 0.5F;
            this.rightshort.xRot += 0.5F;
        }
    }

    @Override
    protected Iterable<ModelRenderer> getBodyModels() {
        return ImmutableList.of(cloakback, leftlong, rightmedium, leftmedium, rightshort, leftshort, rightlong, shoulderright, shoulderleft);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

}
