package de.teamlapen.vampirism.client.render.vanilla;

import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVampireVillager extends RenderVillager{

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID+":textures/entity/vampire.png"); //TODO modify a little bit
	
	protected ResourceLocation getEntityTexture(EntityVillager v)
    {
    	if(VampireMob.get(v).isBitten()){
    		return texture;
    	}
		return super.getEntityTexture(v);
    }
    
}
