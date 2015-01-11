package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class RenderVampirePig extends RenderPig {

	private static final ResourceLocation vampirePigTextures = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/vampirePig.png");

	public RenderVampirePig(ModelBase p_i1265_1_, ModelBase p_i1265_2_, float p_i1265_3_) {
		super(p_i1265_1_, p_i1265_2_, p_i1265_3_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPig pig) {
		if (VampireMob.get(pig).isVampire()) {
			return vampirePigTextures;
		}
		return super.getEntityTexture(pig);
	}

}
