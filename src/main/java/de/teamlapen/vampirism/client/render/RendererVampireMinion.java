package de.teamlapen.vampirism.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBipedShrinkable;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererVampireMinion extends RenderBiped{

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire.png");
	public RendererVampireMinion(float p_i1261_2_) {
		super(new ModelBipedShrinkable(), p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		IMinion minion=(IMinion) entity;
		//Logger.i("test", ""+minion.getLord());
		IMinionLord lord=minion.getLord();
		if( lord instanceof VampirePlayer){
			AbstractClientPlayer player=((AbstractClientPlayer)((VampirePlayer) lord).getRepresentingEntity());
			ResourceLocation skin=player.getLocationSkin();
			ResourceLocation newSkin=new ResourceLocation("vampirism/temp/"+skin.hashCode());
			TextureHelper.createVampireTexture(player,skin,newSkin);
			return newSkin;
		}
		return texture;
	}
	
	protected ResourceLocation getVampireTexture(int id){
		return VampireRenderer.getTexture(id);
	}
	
	@Override
	protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_)
    {
		EntityVampireMinion m=(EntityVampireMinion)p_77036_1_;
		float size=0F;
		if(m.getOldVampireTexture()!=-1){
			size=1F-Math.min((float)m.ticksExisted/50F,1F);
		}
		((ModelBipedShrinkable)this.mainModel).setSize(size);
		
		//If either invisible or already small ->use parent method
		if(p_77036_1_.isInvisible()||m.getOldVampireTexture()==-1){
			super.renderModel(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
		}
		else{
			//firstly render own texture secondly blend old vampire texture in
			this.bindEntityTexture(p_77036_1_);
            this.mainModel.render(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1F, 1F, 1F, size);
            this.bindTexture(this.getVampireTexture(m.getOldVampireTexture()));
            this.mainModel.render(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            
		}
    }

}
