package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelChurchAltar extends ModelBase {
	// fields
	ModelRenderer top;
	ModelRenderer base;
	ModelRenderer colum;
	ModelRenderer book1;
	ModelRenderer book2;
	ModelRenderer book3;
	ModelRenderer book4;

	public ModelChurchAltar() {
		textureWidth = 64;
		textureHeight = 32;

		top = new ModelRenderer(this, 0, 0);
		top.addBox(-8F, 0F, -6F, 16, 1, 12);
		top.setRotationPoint(0F, 10F, 0F);
		top.setTextureSize(64, 32);
		top.mirror = true;
		setRotation(top, 0.2617994F, 0F, 0F);
		base = new ModelRenderer(this, 12, 14);
		base.addBox(-5F, 0F, -3F, 10, 1, 6);
		base.setRotationPoint(0F, 23F, 0F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		colum = new ModelRenderer(this, 0, 14);
		colum.addBox(-1F, 0F, -2F, 2, 13, 4);
		colum.setRotationPoint(0F, 10F, 0F);
		colum.setTextureSize(64, 32);
		colum.mirror = true;
		setRotation(colum, 0F, 0F, 0F);
		book1 = new ModelRenderer(this, 13, 23);
		book1.addBox(0F, 0F, -4F, 5, 0, 7);
		book1.setRotationPoint(0F, 10F, -1F);
		book1.setTextureSize(64, 32);
		book1.mirror = true;
		setRotation(book1, 0.2617994F, 0.0349066F, -0.2617994F);
		book2 = new ModelRenderer(this, 39, 23);
		book2.addBox(-5F, 0F, -4F, 5, 0, 7);
		book2.setRotationPoint(0F, 10F, -1F);
		book2.setTextureSize(64, 32);
		book2.mirror = true;
		setRotation(book2, 0.2617994F, -0.1047198F, 0.2617994F);
		book3 = new ModelRenderer(this, 41, 14);
		book3.addBox(-5F, 0F, -4F, 5, 0, 7);
		book3.setRotationPoint(0F, 10F, -1F);
		book3.setTextureSize(64, 32);
		book3.mirror = true;
		setRotation(book3, 0.2617994F, -0.1047198F, 0.270526F);
		book4 = new ModelRenderer(this, 41, 14);
		book4.addBox(0F, 0F, -4F, 5, 0, 7);
		book4.setRotationPoint(0F, 10F, -1F);
		book4.setTextureSize(64, 32);
		book4.mirror = true;
		setRotation(book4, 0.2617994F, 0.0349066F, -0.270526F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		top.render(f5);
		base.render(f5);
		colum.render(f5);
		book1.render(f5);
		book2.render(f5);
		book3.render(f5);
		book4.render(f5);
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