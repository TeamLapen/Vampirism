package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;

public class RenderVampireWitch extends RenderWitch {

	private static final ResourceLocation vampireWitchTextures = new ResourceLocation("textures/entity/chicken.png");

	@Override
	protected ResourceLocation getEntityTexture(EntityWitch w) {
		if (VampireMob.get(w).isBitten()) {
			return vampireWitchTextures;
		}
		return super.getEntityTexture(w);
	}
}
