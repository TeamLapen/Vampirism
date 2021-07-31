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
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Villager Model with usable arms
 */
@OnlyIn(Dist.CLIENT)
public class VillagerWithArmsModel<T extends MobEntity> extends VillagerModel<T> implements IHasArm {
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;

    public VillagerWithArmsModel(float scale) {
        this(scale, 0F, 64, 64);

    }

    public VillagerWithArmsModel(float scale, float p_i1164_2_, int width, int height) {
        super(scale, width, height);
        this.arms.visible = false;
        this.rightArm = (new ModelRenderer(this).setTexSize(width, height));
        this.rightArm.texOffs(44, 22).addBox(-4F, -2F, -2F, 4, 8, 4, scale);
        this.rightArm.setPos(0, 2 + p_i1164_2_, 0);
        this.rightArm.addBox(-4, 6, -2, 4, 3, 4);

        this.leftArm = new ModelRenderer(this).setTexSize(width, height);
        this.leftArm.texOffs(44, 22).addBox(0, -2, -2, 4, 8, 4, scale);
        this.leftArm.addBox(0, 6, -2, 4, 3, 4, scale);
        this.leftArm.setPos(-5, 2 + p_i1164_2_, 0);
        this.leftArm.mirror = true;

    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return Iterables.concat(super.parts(), ImmutableList.of(leftArm, rightArm));
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.leftArm.setPos(4, 3, -1);
        this.rightArm.setPos(-4, 3, -1);
        this.leftArm.xRot = -0.75F;
        this.rightArm.xRot = -0.75F;

        if (this.attackTime > 0.0F) {
            HandSide enumhandside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
            float f1;
            f1 = 1.0F - this.attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.attackTime * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            modelrenderer.xRot = (float) ((double) modelrenderer.xRot - ((double) f2 * 1.2D + (double) f3));
        }
    }

    @Override
    public void translateToHand(HandSide handSide, MatrixStack matrixStack) {
        float f = handSide == HandSide.RIGHT ? 1.0F : -1.0F;
        ModelRenderer arm = getArmForSide(handSide);
        arm.x += f;
        arm.translateAndRotate(matrixStack);
        arm.x -= f;
    }


    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.leftArm : this.rightArm;
    }

    protected HandSide getMainHand(Entity entityIn) {
        return entityIn instanceof LivingEntity ? ((LivingEntity) entityIn).getMainArm() : HandSide.RIGHT;
    }
}
