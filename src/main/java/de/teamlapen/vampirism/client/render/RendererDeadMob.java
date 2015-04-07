package de.teamlapen.vampirism.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.entity.EntityDeadMob;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RendererDeadMob extends Render {

	public RendererDeadMob() {
		super();
	}

	RenderBlocks render=new RenderBlocks();
	
	public void doRender(EntityDeadMob p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		 GL11.glPushMatrix();
	      GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
	        
	    this.bindTexture(new ResourceLocation("adf"));
		render.renderBlockAsItem(Blocks.tnt, 0, p_76986_1_.getBrightness(p_76986_9_));
		
        GL11.glPopMatrix();

	}
	
	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityDeadMob)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return TextureMap.locationBlocksTexture;
	}
	
	@Override
	public boolean isStaticEntity(){
		return false;
	}

}
