package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class RenderVampireWolf extends RenderWolf {

	private static final ResourceLocation angryWolfTextures = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/vampireWolf.png");

	public RenderVampireWolf(ModelBase p_i1269_1_, ModelBase p_i1269_2_, float p_i1269_3_) {
		super(p_i1269_1_, p_i1269_2_, p_i1269_3_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWolf wolf) {
		if (VampireMob.get(wolf).isBitten()) {
			return angryWolfTextures;
		}
		return super.getEntityTexture(wolf);
	}

}
