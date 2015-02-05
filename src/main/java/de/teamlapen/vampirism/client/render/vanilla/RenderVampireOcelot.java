package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderOcelot;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class RenderVampireOcelot extends RenderOcelot {

	private static final ResourceLocation vampireOcelotTextures = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/vampireOcelot.png");

	public RenderVampireOcelot(ModelBase p_i1264_1_, float p_i1264_2_) {
		super(p_i1264_1_, p_i1264_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityOcelot o) {
		if (VampireMob.get(o).isVampire()) {
			return vampireOcelotTextures;
		}
		return super.getEntityTexture(o);
	}

}
