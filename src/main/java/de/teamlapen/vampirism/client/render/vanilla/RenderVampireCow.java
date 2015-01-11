package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class RenderVampireCow extends RenderCow {

	private static final ResourceLocation vampireCowTextures = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/vampireCow.png");

	public RenderVampireCow(ModelBase p_i1253_1_, float p_i1253_2_) {
		super(p_i1253_1_, p_i1253_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCow cow) {
		if (VampireMob.get(cow).isVampire()) {
			return vampireCowTextures;
		}
		return super.getEntityTexture(cow);
	}

}
