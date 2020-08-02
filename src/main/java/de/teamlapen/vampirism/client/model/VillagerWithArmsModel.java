package de.teamlapen.vampirism.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Villager Model with usable arms
 */
@OnlyIn(Dist.CLIENT)
public class VillagerWithArmsModel<T extends MobEntity> extends VillagerModel<T> implements IHasArm {
    private ModelRenderer leftArm;
    private ModelRenderer rightArm;

    public VillagerWithArmsModel(float scale) {
        this(scale, 0F, 64, 64);

    }

    public VillagerWithArmsModel(float scale, float p_i1164_2_, int width, int height) {
        super(scale, width, height);
        this.villagerArms.showModel = false;
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

    @Override
    public Iterable<ModelRenderer> getParts() {
        return Iterables.concat(super.getParts(), ImmutableList.of(leftArm, rightArm));
    }

    @Override
    public void setRotationAngles(T entityIn, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.setRotationAngles(entityIn, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
        this.leftArm.setRotationPoint(4, 3, -1);
        this.rightArm.setRotationPoint(-4, 3, -1);
        this.leftArm.rotateAngleX = -0.75F;
        this.rightArm.rotateAngleX = -0.75F;

        if (this.swingProgress > 0.0F) {
            HandSide enumhandside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
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

    @Override
    public void translateHand(HandSide handSide, MatrixStack matrixStack) {
        float f = handSide == HandSide.RIGHT ? 1.0F : -1.0F;
        ModelRenderer arm = getArmForSide(handSide);
        arm.rotationPointX += f;
        arm.translateRotate(matrixStack);
        arm.rotationPointX -= f;
    }


    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.leftArm : this.rightArm;
    }

    protected HandSide getMainHand(Entity entityIn) {
        return entityIn instanceof LivingEntity ? ((LivingEntity) entityIn).getPrimaryHand() : HandSide.RIGHT;
    }
}
