package de.teamlapen.vampirism.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelVHVillager;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class VampireHunterRenderer extends RenderBiped {
	
	protected static class VampireHunterRenderer2 extends RenderBiped {

		private static final ResourceLocation texture3 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunter.png");



		public VampireHunterRenderer2() {
			super(new ModelVampireHunter(true), 0.5F);
		}
		

		@Override
		protected ResourceLocation getEntityTexture(Entity entity) {
			return texture3;
		}

	}
	
	protected static class RendererCustomVillager extends RenderLiving {

		private static final ResourceLocation villagerTexture = new ResourceLocation("textures/entity/villager/villager.png");
		
		public RendererCustomVillager(ModelBase p_i1262_1_, float p_i1262_2_) {
			super(p_i1262_1_, p_i1262_2_);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
			return villagerTexture;
		}
		
		@Override
	    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
	    {
	        float f1 = 0.9375F;


	        GL11.glScalef(f1, f1, f1);
	    }

	}


	private static final ResourceLocation textureNormal = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunter.png");

	
	private final RendererCustomVillager rendererVillager;
	private final RenderBiped rendererLevel3;

	public VampireHunterRenderer() {
		super(new ModelVampireHunter(false), 0.5F);
		rendererLevel3=new VampireHunterRenderer2();
		rendererVillager=new RendererCustomVillager(new ModelVHVillager(0.0F),0.0F);
	}
	

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return textureNormal;
	}
	
	@Override
	 public void doRender(EntityLiving entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_){
		int level=((EntityVampireHunter)entity).getLevel();
		if(level==3){
			rendererLevel3.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		}
		if(level==2){
			super.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		}
		if(level==1){
			rendererVillager.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		}
	 }
	
	@Override
	public void setRenderManager(RenderManager manager){
		super.setRenderManager(manager);
		rendererLevel3.setRenderManager(manager);
		rendererVillager.setRenderManager(manager);
	}

}
