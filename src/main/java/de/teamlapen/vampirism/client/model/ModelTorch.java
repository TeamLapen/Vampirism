package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTorch extends ModelBase {
	// fields
	ModelRenderer rod;
	ModelRenderer frame1;
	ModelRenderer frame2;
	ModelRenderer frame3;
	ModelRenderer frame4;
	ModelRenderer frame5;
	ModelRenderer frame6;
	ModelRenderer frame7;
	ModelRenderer frame8;
	ModelRenderer frame9;
	ModelRenderer frame10;
	ModelRenderer frame11;
	ModelRenderer frame12;

	public ModelTorch() {
		textureWidth = 64;
		textureHeight = 32;

		rod = new ModelRenderer(this, 0, 0);
		rod.addBox(0F, -7F, 0F, 1, 8, 1);
		rod.setRotationPoint(0F, 0F, 0F);
		rod.setTextureSize(64, 32);
		rod.mirror = true;
		setRotation(rod, 0F, 0F, 0F);
		frame1 = new ModelRenderer(this, 4, 0);
		frame1.addBox(-1F, -5.5F, 0F, 1, 1, 1);
		frame1.setRotationPoint(0F, 0F, 0F);
		frame1.setTextureSize(64, 32);
		frame1.mirror = true;
		setRotation(frame1, 0F, 0F, 0F);
		frame2 = new ModelRenderer(this, 4, 0);
		frame2.addBox(1F, -5.5F, 0F, 1, 1, 1);
		frame2.setRotationPoint(0F, 0F, 0F);
		frame2.setTextureSize(64, 32);
		frame2.mirror = true;
		setRotation(frame2, 0F, 0F, 0F);
		frame3 = new ModelRenderer(this, 4, 0);
		frame3.addBox(0F, -5.5F, -1F, 1, 1, 1);
		frame3.setRotationPoint(0F, 0F, 0F);
		frame3.setTextureSize(64, 32);
		frame3.mirror = true;
		setRotation(frame3, 0F, 0F, 0F);
		frame4 = new ModelRenderer(this, 4, 0);
		frame4.addBox(0F, -5.5F, 1F, 1, 1, 1);
		frame4.setRotationPoint(0F, 0F, 0F);
		frame4.setTextureSize(64, 32);
		frame4.mirror = true;
		setRotation(frame4, 0F, 0F, 0F);
		frame5 = new ModelRenderer(this, 4, 0);
		frame5.addBox(2F, -6.5F, 0F, 1, 1, 1);
		frame5.setRotationPoint(0F, 0F, 0F);
		frame5.setTextureSize(64, 32);
		frame5.mirror = true;
		setRotation(frame5, 0F, 0F, 0F);
		frame6 = new ModelRenderer(this, 4, 0);
		frame6.addBox(1F, -6.5F, 1F, 1, 1, 1);
		frame6.setRotationPoint(0F, 0F, 0F);
		frame6.setTextureSize(64, 32);
		frame6.mirror = true;
		setRotation(frame6, 0F, 0F, 0F);
		frame7 = new ModelRenderer(this, 4, 0);
		frame7.addBox(0F, -6.5F, 2F, 1, 1, 1);
		frame7.setRotationPoint(0F, 0F, 0F);
		frame7.setTextureSize(64, 32);
		frame7.mirror = true;
		setRotation(frame7, 0F, 0F, 0F);
		frame8 = new ModelRenderer(this, 4, 0);
		frame8.addBox(-1F, -6.5F, 1F, 1, 1, 1);
		frame8.setRotationPoint(0F, 0F, 0F);
		frame8.setTextureSize(64, 32);
		frame8.mirror = true;
		setRotation(frame8, 0F, 0F, 0F);
		frame9 = new ModelRenderer(this, 4, 0);
		frame9.addBox(-2F, -6.5F, 0F, 1, 1, 1);
		frame9.setRotationPoint(0F, 0F, 0F);
		frame9.setTextureSize(64, 32);
		frame9.mirror = true;
		setRotation(frame9, 0F, 0F, 0F);
		frame10 = new ModelRenderer(this, 4, 0);
		frame10.addBox(-1F, -6.5F, -1F, 1, 1, 1);
		frame10.setRotationPoint(0F, 0F, 0F);
		frame10.setTextureSize(64, 32);
		frame10.mirror = true;
		setRotation(frame10, 0F, 0F, 0F);
		frame11 = new ModelRenderer(this, 4, 0);
		frame11.addBox(0F, -6.5F, -2F, 1, 1, 1);
		frame11.setRotationPoint(0F, 0F, 0F);
		frame11.setTextureSize(64, 32);
		frame11.mirror = true;
		setRotation(frame11, 0F, 0F, 0F);
		frame12 = new ModelRenderer(this, 4, 0);
		frame12.addBox(1F, -6.5F, -1F, 1, 1, 1);
		frame12.setRotationPoint(0F, 0F, 0F);
		frame12.setTextureSize(64, 32);
		frame12.mirror = true;
		setRotation(frame12, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		rod.render(f5);
		frame1.render(f5);
		frame2.render(f5);
		frame3.render(f5);
		frame4.render(f5);
		frame5.render(f5);
		frame6.render(f5);
		frame7.render(f5);
		frame8.render(f5);
		frame9.render(f5);
		frame10.render(f5);
		frame11.render(f5);
		frame12.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
