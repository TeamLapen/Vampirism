package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCoffin extends ModelBase {
	// fields
	ModelRenderer leftPlate;
	ModelRenderer rightPlate;
	ModelRenderer backPlate;
	ModelRenderer topPlate;
	ModelRenderer bottomPlate;
	ModelRenderer leftLid;
	ModelRenderer rightLid;
	ModelRenderer leftHandle;
	ModelRenderer rightHandle;

	public ModelCoffin() {
		textureWidth = 256;
		textureHeight = 128;

		leftPlate = new ModelRenderer(this, 0, 64);
		leftPlate.addBox(-8F, -12F, 0F, 1, 12, 32);
		leftPlate.setRotationPoint(0F, 23F, -8F);
		leftPlate.setTextureSize(256, 128);
		leftPlate.mirror = true;
		setRotation(leftPlate, 0F, 0F, 0F);
		rightPlate = new ModelRenderer(this, 66, 64);
		rightPlate.addBox(7F, -12F, 0F, 1, 12, 32);
		rightPlate.setRotationPoint(0F, 23F, -8F);
		rightPlate.setTextureSize(256, 128);
		rightPlate.mirror = true;
		setRotation(rightPlate, 0F, 0F, 0F);
		backPlate = new ModelRenderer(this, 0, 0);
		backPlate.addBox(-8F, 0F, 0F, 16, 1, 32);
		backPlate.setRotationPoint(0F, 23F, -8F);
		backPlate.setTextureSize(256, 128);
		backPlate.mirror = true;
		setRotation(backPlate, 0F, 0F, 0F);
		topPlate = new ModelRenderer(this, 0, 0);
		topPlate.addBox(-7F, -12F, 31F, 14, 12, 1);
		topPlate.setRotationPoint(0F, 23F, -8F);
		topPlate.setTextureSize(256, 128);
		topPlate.mirror = true;
		setRotation(topPlate, 0F, 0F, 0F);
		bottomPlate = new ModelRenderer(this, 0, 15);
		bottomPlate.addBox(-7F, -11F, 0F, 14, 12, 1);
		bottomPlate.setRotationPoint(0F, 22F, -8F);
		bottomPlate.setTextureSize(256, 128);
		bottomPlate.mirror = true;
		setRotation(bottomPlate, 0F, 0F, 0F);
		leftLid = new ModelRenderer(this, 0, 33);
		leftLid.addBox(0F, 0F, 0F, 7, 1, 30);
		leftLid.setRotationPoint(-7F, 11F, -7F);
		leftLid.setTextureSize(256, 128);
		leftLid.mirror = true;
		setRotation(leftLid, 0F, 0F, 0F);
		rightLid = new ModelRenderer(this, 74, 33);
		rightLid.addBox(-7F, 0F, 0F, 7, 1, 30);
		rightLid.setRotationPoint(7F, 11F, -7F);
		rightLid.setTextureSize(256, 128);
		rightLid.mirror = true;
		setRotation(rightLid, 0F, 0F, 0F);
		leftHandle = new ModelRenderer(this, 64, 0);
		leftHandle.addBox(5.5F, -0.5F, 15F, 1, 1, 4);
		leftHandle.setRotationPoint(-7F, 11F, -7F);
		leftHandle.setTextureSize(256, 128);
		leftHandle.mirror = true;
		setRotation(leftHandle, 0F, 0F, 0F);
		rightHandle = new ModelRenderer(this, 74, 0);
		rightHandle.addBox(-6.5F, -0.5F, 15F, 1, 1, 4);
		rightHandle.setRotationPoint(7F, 11F, -7F);
		rightHandle.setTextureSize(256, 128);
		rightHandle.mirror = true;
		setRotation(rightHandle, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		leftPlate.render(f5);
		rightPlate.render(f5);
		backPlate.render(f5);
		topPlate.render(f5);
		bottomPlate.render(f5);
		leftLid.render(f5);
		rightLid.render(f5);
		leftHandle.render(f5);
		rightHandle.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void setLid(boolean open) {
		if(open) {
			leftLid.rotateAngleZ = leftHandle.rotateAngleZ = (float) (-Math.PI*0.75F);
			rightLid.rotateAngleZ = rightHandle.rotateAngleZ = (float) (Math.PI*0.75F);
		} else {
			leftLid.rotateAngleZ = leftHandle.rotateAngleZ = 0.0F;
			rightLid.rotateAngleZ = rightHandle.rotateAngleZ = 0.0F;
		}
	}

	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
