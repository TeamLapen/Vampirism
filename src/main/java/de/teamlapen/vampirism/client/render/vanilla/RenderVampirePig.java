package de.teamlapen.vampirism.client.render.vanilla;

import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class RenderVampirePig extends RenderPig{

	private static final ResourceLocation vampirePigTextures = new ResourceLocation("textures/entity/cow/cow.png");
	
	public RenderVampirePig(ModelBase p_i1265_1_, ModelBase p_i1265_2_, float p_i1265_3_) {
		super(p_i1265_1_, p_i1265_2_, p_i1265_3_);
	}
	
	@Override
    protected ResourceLocation getEntityTexture(EntityPig pig)
    {
		if(VampireMob.get(pig).isBitten()){
    		return vampirePigTextures;
    	}
        return super.getEntityTexture(pig);
    }

}
