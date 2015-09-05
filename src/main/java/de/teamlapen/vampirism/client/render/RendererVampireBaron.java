package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelVampireBaron;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RendererVampireBaron extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireBaron.png");

	public RendererVampireBaron(float p_i1261_2_) {
		super(new ModelVampireBaron(), p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return texture;
	}

}
