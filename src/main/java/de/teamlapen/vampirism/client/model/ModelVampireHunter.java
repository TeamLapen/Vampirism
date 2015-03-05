package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVampireHunter extends ModelBipedCloaked {
	ModelRenderer hatTop, hatRim;

	public ModelVampireHunter() {
		super(0.0F, 0.0F, 128, 64);

		hatTop = new ModelRenderer(this, 0, 45);
		hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
		hatTop.rotationPointX = super.bipedHead.rotationPointX;
		hatTop.rotationPointY = super.bipedHead.rotationPointY;
		hatTop.rotationPointZ = super.bipedHead.rotationPointZ;
		hatTop.setTextureSize(128, 64);
		hatTop.mirror = true;
		setRotationPoints(hatTop, 0F, 0F, 0F);

		hatRim = new ModelRenderer(this, 0, 32);
		hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
		hatRim.rotationPointX = super.bipedHead.rotationPointX;
		hatRim.rotationPointY = super.bipedHead.rotationPointY;
		hatRim.rotationPointZ = super.bipedHead.rotationPointZ;
		hatRim.setTextureSize(128, 64);
		hatRim.mirror = true;
		setRotationPoints(hatRim, 0F, 0F, 0F);

		super.bipedEars = null;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		hatTop.render(f5);
		hatRim.render(f5);
	}

	@Override
	public void setRotationAngles(float f1, float f2, float f3, float f4,
			float f5, float f6, Entity e) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6, e);
		hatRim.rotateAngleX = super.bipedHead.rotateAngleX;
		hatRim.rotateAngleY = super.bipedHead.rotateAngleY;
		hatRim.rotateAngleZ = super.bipedHead.rotateAngleZ;
		hatTop.rotateAngleX = super.bipedHead.rotateAngleX;
		hatTop.rotateAngleY = super.bipedHead.rotateAngleY;
		hatTop.rotateAngleZ = super.bipedHead.rotateAngleZ;
	}
}
