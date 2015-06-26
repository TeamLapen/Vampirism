package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBipedCloaked;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererVampireLord extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireLord.png");

	public RendererVampireLord(float p_i1261_2_) {
		super(new ModelBipedCloaked(0.0F, 0.0F, 128, 64), p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return texture;
	}

}
