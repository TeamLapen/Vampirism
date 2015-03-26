package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCoffin extends ModelBase {
	// fields
	ModelRenderer lid;
	ModelRenderer body;

	public ModelCoffin() {
		textureWidth = 128;
		textureHeight = 64;

		lid = new ModelRenderer(this, 0, 0);
		lid.addBox(0F, 0F, 0F, 16, 1, 32);
		lid.setRotationPoint(-8F, 15F, -8F);
		lid.setTextureSize(64, 32);
		lid.mirror = true;
		setRotation(lid, 0F, 0F, 0F);
		body = new ModelRenderer(this, 0, 0);
		body.addBox(-8F, 0F, 0F, 16, 8, 32);
		body.setRotationPoint(0F, 16F, -8F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		lid.render(f5);
		body.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
