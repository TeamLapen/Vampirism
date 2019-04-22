package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Villager Model with usable arms
 */
@OnlyIn(Dist.CLIENT)
public class ModelVillagerWithArms extends ModelVillager {
    private ModelRenderer leftArm;
    private ModelRenderer rightArm;

    public ModelVillagerWithArms(float scale) {
        this(scale, 0F, 64, 64);

    }

    public ModelVillagerWithArms(float scale, float p_i1164_2_, int width, int height) {
        super(scale, p_i1164_2_, width, height);
        this.villagerArms.isHidden = true;
        this.rightArm = (new ModelRenderer(this).setTextureSize(width, height));
        this.rightArm.setTextureOffset(44, 22).addBox(-4F, -2F, -2F, 4, 8, 4, scale);
        this.rightArm.setRotationPoint(0, 2 + p_i1164_2_, 0);
        this.rightArm.addBox(-4, 6, -2, 4, 3, 4);

        this.leftArm = new ModelRenderer(this).setTextureSize(width, height);
        this.leftArm.setTextureOffset(44, 22).addBox(0, -2, -2, 4, 8, 4, scale);
        this.leftArm.addBox(0, 6, -2, 4, 3, 4, scale);
        this.leftArm.setRotationPoint(-5, 2 + p_i1164_2_, 0);
        this.leftArm.mirror = true;

    }

    public void postRenderArm(float scale, EnumHandSide side) {
        this.getArmForSide(side).postRender(scale);

    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, p_78088_2_, limbSwing, ageInTicks, netHeadYaw, headPitch, scale);
        this.leftArm.render(scale);
        this.rightArm.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.leftArm.setRotationPoint(4, 3, -1);
        this.rightArm.setRotationPoint(-4, 3, -1);
        this.leftArm.rotateAngleX = -0.75F;
        this.rightArm.rotateAngleX = -0.75F;

        if (this.swingProgress > 0.0F) {
            EnumHandSide enumhandside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
            this.getArmForSide(enumhandside.opposite());
            float f1;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.villagerHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
        }
    }

    protected ModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.leftArm : this.rightArm;
    }

    protected EnumHandSide getMainHand(Entity entityIn) {
        return entityIn instanceof EntityLivingBase ? ((EntityLivingBase) entityIn).getPrimaryHand() : EnumHandSide.RIGHT;
    }
}
