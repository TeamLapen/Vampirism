package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * 
 * @author Moritz
 *
 */
public class ModelVampireHunter extends ModelBipedCloaked {
	ModelRenderer hatTop, hatRim, axeShaft, axeBlade1, axeBlade2, stake,secondHead;

	private boolean renderWeapons;

	public ModelVampireHunter(boolean hasWeapons) {
		super(0.0F, 0.0F, 64, 64,0,25);
		this.bipedHeadwear.isHidden=true;
		renderWeapons = hasWeapons;

		hatTop = new ModelRenderer(this, 0, 24);
		hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
		hatTop.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
		hatTop.setTextureSize(128, 64);
		hatTop.mirror = true;

		hatRim = new ModelRenderer(this, 0, 32);
		hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
		hatRim.setRotationPoint(super.bipedHead.rotationPointX, super.bipedHead.rotationPointY, super.bipedHead.rotationPointZ);
		hatRim.setTextureSize(128, 64);
		hatRim.mirror = true;

		axeShaft = new ModelRenderer(this, 32, 0);
		axeShaft.addBox(-2F, 8F, -17F, 1, 1, 15);
		axeShaft.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
		axeShaft.setTextureSize(128, 64);
		axeShaft.mirror = true;

		axeBlade1 = new ModelRenderer(this, 0, 47);
		axeBlade1.addBox(-2F, 4F, -16F, 1, 4, 7);
		axeBlade1.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
		axeBlade1.setTextureSize(128, 64);
		axeBlade1.mirror = true;

		axeBlade2 = new ModelRenderer(this, 0, 47);
		axeBlade2.addBox(-2F, 9F, -16F, 1, 4, 7);
		axeBlade2.setRotationPoint(super.bipedRightArm.rotationPointX, super.bipedRightArm.rotationPointY, super.bipedRightArm.rotationPointZ);
		axeBlade2.setTextureSize(128, 64);
		axeBlade2.mirror = true;

		stake = new ModelRenderer(this, 32, 0);
		stake.addBox(1F, 8F, -8F, 1, 1, 6);
		stake.setRotationPoint(super.bipedLeftArm.rotationPointX, super.bipedLeftArm.rotationPointY, super.bipedLeftArm.rotationPointZ);
		stake.setTextureSize(128, 64);
		stake.mirror = true;

		secondHead = new ModelRenderer(this, 0, 0);
		secondHead.setTextureSize(64,32);
		secondHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		secondHead.setRotationPoint(0.0F, 0.0F + 0.0F, 0.0F);


		super.bipedEars = null;
	}

	public boolean getRenderWeapon() {
		return renderWeapons;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		hatTop.render(f5);
		hatRim.render(f5);
		if (renderWeapons) {
			axeShaft.render(f5);
			axeBlade1.render(f5);
			axeBlade2.render(f5);
			stake.render(f5);
		}
	}

	public void renderSecondHead(float f5){
		secondHead.render(f5);
	}

	public void setRenderWeapon(boolean flag) {
		renderWeapons = flag;
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
		secondHead.rotateAngleX = super.bipedHead.rotateAngleX;
		secondHead.rotateAngleY = super.bipedHead.rotateAngleY;
		secondHead.rotateAngleZ = super.bipedHead.rotateAngleZ;
	}
}
