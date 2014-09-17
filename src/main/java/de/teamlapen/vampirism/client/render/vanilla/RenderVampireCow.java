package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;

public class RenderVampireCow extends RenderCow {

	private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");
	private static final ResourceLocation vampireCowTextures = new ResourceLocation("textures/entity/pig/pig.png");

	public RenderVampireCow(ModelBase p_i1253_1_, float p_i1253_2_) {
		super(p_i1253_1_, p_i1253_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCow cow) {
		if (VampireMob.get(cow).isBitten()) {
			return vampireCowTextures;
		}
		return super.getEntityTexture(cow);
	}

}
