package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTest extends ModelBiped {
	ModelRenderer hatTop;
	ModelRenderer hatRim;
	
	public ModelTest() {
		super(0.0F, 0.0F, 128, 64);
		hatTop = new ModelRenderer(this, 0, 45);
		hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
		hatTop.setRotationPoint(0F, 0F, 0F);
		hatTop.setTextureSize(128, 64);
		hatTop.mirror = true;
		setRotation(hatTop, 0F, 0F, 0F);
		hatRim = new ModelRenderer(this, 0, 32);
		hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
		hatRim.setRotationPoint(0F, 0F, 0F);
		hatRim.setTextureSize(128, 64);
		hatRim.mirror = true;
		setRotation(hatRim, 0F, 0F, 0F);
	}
	
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		hatTop.render(f5);
		hatRim.render(f5);
		renderCloak(f5);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
        super.bipedHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        super.bipedHead.rotateAngleX = f4 / (180F / (float)Math.PI);
	}
}
