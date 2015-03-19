package de.teamlapen.vampirism.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererVampireLord extends RenderBiped{

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire.png");

	public RendererVampireLord(ModelBiped p_i1261_1_, float p_i1261_2_) {
		super(p_i1261_1_, p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return texture;
	}

}
