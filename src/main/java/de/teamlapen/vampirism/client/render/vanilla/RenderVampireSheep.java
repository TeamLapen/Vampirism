package de.teamlapen.vampirism.client.render.vanilla;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class RenderVampireSheep extends RenderSheep {

	private static final ResourceLocation vampireShearedSheepTextures = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/vampireSheep"
			+ ".png");

	public RenderVampireSheep(ModelBase p_i1266_1_, ModelBase p_i1266_2_, float p_i1266_3_) {
		super(p_i1266_1_, p_i1266_2_, p_i1266_3_);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySheep sheep) {
		if (VampireMob.get(sheep).isVampire()) {
			return vampireShearedSheepTextures;
		}
		return super.getEntityTexture(sheep);
	}

}
