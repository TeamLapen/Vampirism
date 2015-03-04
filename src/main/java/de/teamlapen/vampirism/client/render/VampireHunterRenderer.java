package de.teamlapen.vampirism.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class VampireHunterRenderer extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunter.png");

	public VampireHunterRenderer() {
		super(new ModelVampireHunter(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return texture;
	}

}
