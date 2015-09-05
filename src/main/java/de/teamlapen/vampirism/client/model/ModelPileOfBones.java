package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.EntityDeadMob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelPileOfBones extends ModelBase {
	ModelRenderer bone1;
	ModelRenderer bone2;
	ModelRenderer bone3;
	ModelRenderer bone4;
	ModelRenderer bone5;
	ModelRenderer bone6;
	ModelRenderer bone7;
	ModelRenderer bone8;
	ModelRenderer bone9;
	ModelRenderer bone10;
	ModelRenderer skull;

	public ModelPileOfBones() {
		textureWidth = 64;
		textureHeight = 32;

		bone1 = new ModelRenderer(this, 25, 19);
		bone1.addBox(0F, 0F, -9F, 1, 1, 9);
		bone1.setRotationPoint(4F, 23F, 6F);
		bone1.setTextureSize(64, 32);
		bone1.mirror = true;
		setRotation(bone1, -0.0892287F, 1.487144F, 0F);
		bone2 = new ModelRenderer(this, 0, 19);
		bone2.addBox(0F, 0F, 0F, 1, 1, 11);
		bone2.setRotationPoint(-5F, 23F, -4F);
		bone2.setTextureSize(64, 32);
		bone2.mirror = true;
		setRotation(bone2, 0F, -0.0594858F, 0F);
		bone3 = new ModelRenderer(this, 0, 0);
		bone3.addBox(0F, 0F, 0F, 1, 1, 11);
		bone3.setRotationPoint(-2F, 23F, -6F);
		bone3.setTextureSize(64, 32);
		bone3.mirror = true;
		setRotation(bone3, 0.0594858F, 0.7138293F, 0F);
		bone4 = new ModelRenderer(this, 0, 0);
		bone4.addBox(0F, 0F, 0F, 1, 1, 11);
		bone4.setRotationPoint(5F, 23F, 0F);
		bone4.setTextureSize(64, 32);
		bone4.mirror = true;
		setRotation(bone4, 0.1487144F, -1.219458F, 0F);
		bone5 = new ModelRenderer(this, 0, 0);
		bone5.addBox(0F, 0F, -11F, 1, 1, 11);
		bone5.setRotationPoint(4F, 23F, 8F);
		bone5.setTextureSize(64, 32);
		bone5.mirror = true;
		setRotation(bone5, -0.1784573F, 0.267686F, 0F);
		bone6 = new ModelRenderer(this, 0, 13);
		bone6.addBox(-0.5F, -4F, -0.5F, 1, 4, 1);
		bone6.setRotationPoint(0F, 24F, 0F);
		bone6.setTextureSize(64, 32);
		bone6.mirror = true;
		setRotation(bone6, 0F, 0F, 0F);
		bone7 = new ModelRenderer(this, 0, 19);
		bone7.addBox(0F, 0F, -11F, 1, 1, 11);
		bone7.setRotationPoint(5F, 23F, -4F);
		bone7.setTextureSize(64, 32);
		bone7.mirror = true;
		setRotation(bone7, -0.1487144F, 1.695345F, 0F);
		bone8 = new ModelRenderer(this, 25, 19);
		bone8.addBox(0F, 0F, 0F, 1, 1, 9);
		bone8.setRotationPoint(-4F, 23F, -2F);
		bone8.setTextureSize(64, 32);
		bone8.mirror = true;
		setRotation(bone8, 0F, 0.3271718F, 0F);
		bone9 = new ModelRenderer(this, 0, 0);
		bone9.addBox(0F, 0F, 0F, 1, 1, 11);
		bone9.setRotationPoint(-2F, 23F, -6F);
		bone9.setTextureSize(64, 32);
		bone9.mirror = true;
		setRotation(bone9, 0.2379431F, -0.267686F, 0F);
		bone10 = new ModelRenderer(this, 0, 19);
		bone10.addBox(0F, 0F, 0F, 1, 1, 11);
		bone10.setRotationPoint(-5F, 23F, -4F);
		bone10.setTextureSize(64, 32);
		bone10.mirror = true;
		setRotation(bone10, 0F, 1.159973F, 0F);
		skull = new ModelRenderer(this, 26, 0);
		skull.addBox(-4F, -8F, -4F, 8, 8, 8);
		skull.setRotationPoint(0F, 22F, 0F);
		skull.setTextureSize(64, 32);
		skull.mirror = true;
		setRotation(skull, -0.2082002F, 0.5056291F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.render((EntityDeadMob) entity, f, f1, f2, f3, f4, f5);
	}

	public void render(EntityDeadMob entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		GL11.glRotatef(180, 1, 0, 0);
		GL11.glTranslatef(0, -1.5F, 0);
		bone1.render(f5);
		bone2.render(f5);
		bone3.render(f5);
		bone4.render(f5);
		bone5.render(f5);
		bone6.render(f5);
		bone7.render(f5);
		bone8.render(f5);
		bone9.render(f5);
		bone10.render(f5);
		if (entity.shouldRenderSkull()) {
			skull.render(f5);
		}

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
