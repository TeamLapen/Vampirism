package de.teamlapen.vampirism.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererVampireMinion extends RenderBiped{

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire.png");
	
	public RendererVampireMinion(ModelBiped p_i1261_1_, float p_i1261_2_) {
		super(p_i1261_1_, p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		IMinion minion=(IMinion) entity;
		//Logger.i("test", ""+minion.getLord());
		if(minion.getLord() instanceof VampirePlayer){
			AbstractClientPlayer player=((AbstractClientPlayer)((VampirePlayer) minion.getLord()).getRepresentingEntity());
			ResourceLocation skin=player.getLocationSkin();
			ResourceLocation newSkin=new ResourceLocation("vampirism/temp/"+skin.hashCode());
			TextureHelper.createVampireTexture(player,skin,newSkin);
			return newSkin;
		}
		return texture;
	}

}
