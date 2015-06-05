package de.teamlapen.vampirism.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

/**
 * ModelBiped which can grow/shrink between mature and child size
 * @author Maxanier
 *
 */
public class ModelBipedShrinkable extends ModelBiped {
	
	private float size=1F;
	
	/**
	 * Sets shrink status 1 equals mature size, 0 equals child size
	 * @param f
	 */
	public void setSize(float f){
		if(f>1){
			size=1.0F;
		}
		else if(f<0){
			f=0;
		}
		else{
			size=f;
		}
		
	}
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

            float f6 = 2.0F-size;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / (1.5F+(1F-size)*0.5F), 1.5F / (1.5F+(1F-size)*0.5F), 1.5F / (1.5F+(1F-size)*0.5F));
            GL11.glTranslatef(0.0F, 16.0F * p_78088_7_ * (-(size)*(size-1F)-size+1), 0.0F);
            this.bipedHead.render(p_78088_7_);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * p_78088_7_ * (1F-size), 0.0F);
            this.bipedBody.render(p_78088_7_);
            this.bipedRightArm.render(p_78088_7_);
            this.bipedLeftArm.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);

            this.bipedHeadwear.render(p_78088_7_);
            GL11.glPopMatrix();

    }
}
