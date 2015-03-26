package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

/**
 * 
 * @author Moritz
 *
 *         A villager that has become a vampire hunter. Similar model to the
 *         villagers, but it has arms like a regular player which can hold an item
 */
public class ModelVHVillager extends ModelVillager {
	ModelRenderer leftArm, rightArm;

	public ModelVHVillager(float f1) {
		super(0.0F);

		super.villagerArms = null;

		this.rightArm = new ModelRenderer(this, 40, 16);
		this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
		this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

		this.leftArm = new ModelRenderer(this, 40, 16);
		this.leftArm.mirror = true;
		this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
		this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
	}

	public void render(Entity e, float f1, float f2, float f3, float f4,
			float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6, e);
		this.villagerHead.render(f6);
		this.villagerBody.render(f6);
		this.rightVillagerLeg.render(f6);
		this.leftVillagerLeg.render(f6);
		this.leftArm.render(f6);
		this.rightArm.render(f6);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are
	 * used for animating the movement of arms and legs, where par1 represents
	 * the time(so that arms and legs swing back and forth) and par2 represents
	 * how "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float f1, float f2, float f3, float f4,
			float f5, float f6, Entity e) {
		this.villagerHead.rotateAngleY = f4 / (180F / (float) Math.PI);
		this.villagerHead.rotateAngleX = f5 / (180F / (float) Math.PI);
		this.rightVillagerLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F)
				* 1.4F * f2 * 0.5F;
		this.leftVillagerLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F
				+ (float) Math.PI)
				* 1.4F * f2 * 0.5F;
		this.rightVillagerLeg.rotateAngleY = 0.0F;
		this.leftVillagerLeg.rotateAngleY = 0.0F;

		this.rightArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2
				* 0.5F;
		this.leftArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F
				+ (float) Math.PI)
				* 1.4F * f2 * 0.5F;
	}

}
