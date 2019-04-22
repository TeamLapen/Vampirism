package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class ModelBasicHunter extends ModelBipedCloaked {
    private ModelRenderer hatTop, hatRim, axeShaft, axeBlade1, axeBlade2, stake, stakeRight, secondHead, hatTop2, hatRim2, hatRim3;
    private boolean targetingLeft = false;
    private boolean targetingRight = false;
    private float xAngle = 0;

    public ModelBasicHunter() {
        super(0.0F, 0.0F, 64, 64, 0, 32);
        this.bipedHeadwear.isHidden = true;

        hatTop2 = new ModelRenderer(this, 0, 31);
        hatTop2.addBox(-4F, -12F, -4F, 8, 3, 8);
        hatTop2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop2.setTextureSize(128, 64);
        hatTop2.mirror = true;

        hatRim2 = new ModelRenderer(this, 0, 31);
        hatRim2.addBox(-8F, -9F, -8F, 16, 1, 16);
        hatRim2.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim2.setTextureSize(128, 64);
        hatRim2.mirror = true;

        hatRim3 = new ModelRenderer(this, 0, 37);
        hatRim3.addBox(-5F, -6F, -5F, 10, 1, 10);
        hatRim3.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim3.setTextureSize(128, 64);
        hatRim3.mirror = true;

        hatTop = new ModelRenderer(this, 0, 31);
        hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
        hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatTop.setTextureSize(128, 64);
        hatTop.mirror = true;

        hatRim = new ModelRenderer(this, 0, 35);
        hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
        hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
        hatRim.setTextureSize(128, 64);
        hatRim.mirror = true;

        axeShaft = new ModelRenderer(this, 16, 48);
        axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
        axeShaft.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeShaft.setTextureSize(128, 64);
        axeShaft.mirror = true;

        axeBlade1 = new ModelRenderer(this, 0, 53);
        axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
        axeBlade1.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade1.setTextureSize(128, 64);
        axeBlade1.mirror = true;

        axeBlade2 = new ModelRenderer(this, 0, 53);
        axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
        axeBlade2.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        axeBlade2.setTextureSize(128, 64);
        axeBlade2.mirror = true;

        stake = new ModelRenderer(this, 16, 48);
        stake.addBox(1F, 8F, -8F, 1, 1, 6);
        stake.setRotationPoint(super.bipedLeftArm.rotationPointX, super.bipedLeftArm.rotationPointY, super.bipedLeftArm.rotationPointZ);
        stake.setTextureSize(128, 64);
        stake.mirror = true;

        stakeRight = new ModelRenderer(this, 16, 48);
        stakeRight.addBox(-2F, 8F, -8, 1, 1, 6);
        stakeRight.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
        stakeRight.setTextureSize(128, 64);
        stakeRight.mirror = true;

        secondHead = new ModelRenderer(this, 0, 0);
        secondHead.setTextureSize(64, 32);
        secondHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        secondHead.setRotationPoint(0.0F, 0.0F + 0.0F, 0.0F);

    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    /**
     * Renders a hat. Make sure to bind the according texture before
     *
     * @param f5
     */
    public void renderHat(float f5, int type) {
        if (type == -1 || type == 0) {
            hatTop.render(f5);
            hatRim.render(f5);
        } else if (type == 1) {
            hatTop2.render(f5);
            hatRim2.render(f5);
        } else if (type == 2 || type == 3 || type == 4) {
            hatRim3.render(f5);
        }
    }

    /**
     * Renders a second head. Thereby another face can be rendered
     *
     * @param f5
     */
    public void renderSecondHead(float f5) {
        secondHead.render(f5);
    }

    /**
     * Renders axe and stake. Make sure to bind the according texture before
     *
     * @param f5
     */
    public void renderWeapons(float f5, boolean onlyStake) {
        if (onlyStake) {

            stakeRight.render(f5);
        } else {
            axeShaft.render(f5);
            axeBlade1.render(f5);
            axeBlade2.render(f5);
            stake.render(f5);
        }

    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        this.targetingRight = false;
        this.targetingLeft = false;
        ItemStack itemStack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof VampirismItemCrossbow && entitylivingbaseIn instanceof EntityBasicHunter && ((EntityBasicHunter) entitylivingbaseIn).isSwingingArms()) {
            if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
                this.targetingRight = true;
            } else {
                this.targetingLeft = true;
            }
            xAngle = -((EntityBasicHunter) entitylivingbaseIn).getTargetAngle() - (float) Math.PI / 3;
        }

        super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);

    }

    @Override
    public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, Entity e) {
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, e);
        hatRim.rotateAngleX = super.bipedHead.rotateAngleX;
        hatRim.rotateAngleY = super.bipedHead.rotateAngleY;
        hatRim.rotateAngleZ = super.bipedHead.rotateAngleZ;
        hatTop.rotateAngleX = super.bipedHead.rotateAngleX;
        hatTop.rotateAngleY = super.bipedHead.rotateAngleY;
        hatTop.rotateAngleZ = super.bipedHead.rotateAngleZ;
        hatRim2.rotateAngleX = super.bipedHead.rotateAngleX;
        hatRim2.rotateAngleY = super.bipedHead.rotateAngleY;
        hatRim2.rotateAngleZ = super.bipedHead.rotateAngleZ;
        hatTop2.rotateAngleX = super.bipedHead.rotateAngleX;
        hatTop2.rotateAngleY = super.bipedHead.rotateAngleY;
        hatTop2.rotateAngleZ = super.bipedHead.rotateAngleZ;
        hatRim3.rotateAngleX = super.bipedHead.rotateAngleX;
        hatRim3.rotateAngleY = super.bipedHead.rotateAngleY;
        hatRim3.rotateAngleZ = super.bipedHead.rotateAngleZ;
        axeShaft.rotateAngleX = super.bipedRightArm.rotateAngleX;
        axeShaft.rotateAngleY = super.bipedRightArm.rotateAngleY;
        axeShaft.rotateAngleZ = super.bipedRightArm.rotateAngleZ;
        axeBlade1.rotateAngleX = super.bipedRightArm.rotateAngleX;
        axeBlade1.rotateAngleY = super.bipedRightArm.rotateAngleY;
        axeBlade1.rotateAngleZ = super.bipedRightArm.rotateAngleZ;
        axeBlade2.rotateAngleX = super.bipedRightArm.rotateAngleX;
        axeBlade2.rotateAngleY = super.bipedRightArm.rotateAngleY;
        axeBlade2.rotateAngleZ = super.bipedRightArm.rotateAngleZ;
        stake.rotateAngleX = super.bipedLeftArm.rotateAngleX;
        stake.rotateAngleY = super.bipedLeftArm.rotateAngleY;
        stake.rotateAngleZ = super.bipedLeftArm.rotateAngleZ;
        stakeRight.rotateAngleX = super.bipedRightArm.rotateAngleX;
        stakeRight.rotateAngleY = super.bipedRightArm.rotateAngleY;
        stakeRight.rotateAngleZ = super.bipedRightArm.rotateAngleZ;
        secondHead.rotateAngleX = super.bipedHead.rotateAngleX;
        secondHead.rotateAngleY = super.bipedHead.rotateAngleY;
        secondHead.rotateAngleZ = super.bipedHead.rotateAngleZ;

        if (targetingRight) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = xAngle;
            this.bipedLeftArm.rotateAngleX = xAngle / 2F;
        } else if (targetingLeft) {
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = xAngle / 2F;
            this.bipedLeftArm.rotateAngleX = xAngle;
        }

    }
}
