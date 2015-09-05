package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@Override
	public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		BossStatus.setBossStatus((EntityDracula) p_76986_1_, true);
		super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
}