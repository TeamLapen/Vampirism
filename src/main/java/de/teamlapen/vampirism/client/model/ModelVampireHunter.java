package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVampireHunter extends ModelBiped {
	ModelRenderer head;
	ModelRenderer body;
	ModelRenderer rightarm;
	ModelRenderer leftarm;
	ModelRenderer rightleg;
	ModelRenderer leftleg;
	ModelRenderer hatTop;
	ModelRenderer hatRim;
	ModelRenderer cape;
	ModelRenderer Piece1;

	public ModelVampireHunter() {
		textureWidth = 128;
		textureHeight = 64;

		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4F, -8F, -4F, 8, 8, 8);
		head.setRotationPoint(0F, 0F, 0F);
		head.setTextureSize(128, 64);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		body = new ModelRenderer(this, 16, 16);
		body.addBox(-4F, 0F, -2F, 8, 12, 4);
		body.setRotationPoint(0F, 0F, 0F);
		body.setTextureSize(128, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		rightarm = new ModelRenderer(this, 40, 16);
		rightarm.addBox(-3F, -2F, -2F, 4, 12, 4);
		rightarm.setRotationPoint(-5F, 2F, 0F);
		rightarm.setTextureSize(128, 64);
		rightarm.mirror = true;
		setRotation(rightarm, 0F, 0F, 0F);
		leftarm = new ModelRenderer(this, 40, 16);
		leftarm.addBox(-1F, -2F, -2F, 4, 12, 4);
		leftarm.setRotationPoint(5F, 2F, 0F);
		leftarm.setTextureSize(128, 64);
		leftarm.mirror = true;
		setRotation(leftarm, 0F, 0F, 0F);
		rightleg = new ModelRenderer(this, 0, 16);
		rightleg.addBox(-2F, 0F, -2F, 4, 12, 4);
		rightleg.setRotationPoint(-2F, 12F, 0F);
		rightleg.setTextureSize(128, 64);
		rightleg.mirror = true;
		setRotation(rightleg, 0F, 0F, 0F);
		leftleg = new ModelRenderer(this, 0, 16);
		leftleg.addBox(-2F, 0F, -2F, 4, 12, 4);
		leftleg.setRotationPoint(2F, 12F, 0F);
		leftleg.setTextureSize(128, 64);
		leftleg.mirror = true;
		setRotation(leftleg, 0F, 0F, 0F);
		hatTop = new ModelRenderer(this, 38, 0);
		hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
		hatTop.setRotationPoint(0F, 0F, 0F);
		hatTop.setTextureSize(128, 64);
		hatTop.mirror = true;
		setRotation(hatTop, 0F, 0F, 0F);
		hatRim = new ModelRenderer(this, 74, 0);
		hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
		hatRim.setRotationPoint(0F, 0F, 0F);
		hatRim.setTextureSize(128, 64);
		hatRim.mirror = true;
		setRotation(hatRim, 0F, 0F, 0F);
		cape = new ModelRenderer(this, 16, 35);
		cape.addBox(-8F, 0F, 0.1F, 16, 20, 0);
		cape.setRotationPoint(0F, 0F, 2F);
		cape.setTextureSize(128, 64);
		cape.mirror = true;
		setRotation(cape, 0F, 0F, 0F);
		Piece1 = new ModelRenderer(this, "Piece1");
		Piece1.setRotationPoint(0F, 0F, 0F);
		setRotation(Piece1, 0F, 0F, 0F);
		Piece1.mirror = true;
		// this.addChildModelRenderer(Piece1);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		head.render(f5);
		body.render(f5);
		rightarm.render(f5);
		leftarm.render(f5);
		rightleg.render(f5);
		leftleg.render(f5);
		hatTop.render(f5);
		hatRim.render(f5);
		cape.render(f5);
		Piece1.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}
}
