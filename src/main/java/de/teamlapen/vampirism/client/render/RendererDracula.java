package de.teamlapen.vampirism.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelDracula;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererDracula extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/dracula.png");

	public RendererDracula(ModelBiped model, float f) {
		super(model, f);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return texture;
	}
}