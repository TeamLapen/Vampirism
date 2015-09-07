package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author WILLIAM
 *
 */
@SideOnly(Side.CLIENT)
public class RendererGhost extends RenderLiving {
	private static final ResourceLocation ghostTexture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/ghost.png");

	public RendererGhost(RenderManager renderManager,ModelBase par1ModelBase, float par2) {
		super(renderManager,par1ModelBase, par2);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((EntityGhost) entity);
	}

	protected ResourceLocation getEntityTexture(EntityGhost entity) {
		return ghostTexture;
	}
}
