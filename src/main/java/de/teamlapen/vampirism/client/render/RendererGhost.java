package de.teamlapen.vampirism.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author WILLIAM
 *
 */
@SideOnly(Side.CLIENT)
public class RendererGhost extends RenderLiving {
	private static final ResourceLocation ghostTexture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/ghost.png"); 
	
	public RendererGhost(ModelBase par1ModelBase, float par2){
		super(par1ModelBase, par2);
	}

	protected ResourceLocation getEntityTexture(EntityGhost entity){
		return ghostTexture;
	}
	
	protected ResourceLocation getEntityTexture(Entity entity){
		return this.getEntityTexture((EntityGhost) entity);
	}
}
