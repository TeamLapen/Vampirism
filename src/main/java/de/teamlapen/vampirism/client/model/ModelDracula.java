package de.teamlapen.vampirism.client.model;

import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelDracula extends ModelBipedCloaked {
	public ModelDracula() {
		super(0.0F, 0.0F, 128, 64);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		GL11.glPushMatrix();
		super.render(entity, f, f1, f2, f3, f4, f5);
		GL11.glPopMatrix();
	}
}
