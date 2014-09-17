package de.teamlapen.vampirism.client.render.vanilla;

import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderVampireHorse extends RenderHorse{

	private static final ResourceLocation vampireHorseTextures = new ResourceLocation("textures/entity/cow/cow.png"); //TODO modify white horse
	
	public RenderVampireHorse(ModelBase p_i1256_1_, float p_i1256_2_) {
		super(p_i1256_1_, p_i1256_2_);
	}
	
	@Override
    protected ResourceLocation getEntityTexture(EntityHorse horse)
    {
    	if(VampireMob.get(horse).isBitten()){
    		return vampireHorseTextures;
    	}
        return super.getEntityTexture(horse);
    }

}
