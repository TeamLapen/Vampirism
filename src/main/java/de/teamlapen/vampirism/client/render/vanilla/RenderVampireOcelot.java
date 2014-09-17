package de.teamlapen.vampirism.client.render.vanilla;

import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderOcelot;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;

public class RenderVampireOcelot extends RenderOcelot {

    private static final ResourceLocation vampireOcelotTextures = new ResourceLocation("textures/entity/pig/pig.png");
    
	public RenderVampireOcelot(ModelBase p_i1264_1_, float p_i1264_2_) {
		super(p_i1264_1_, p_i1264_2_);
	}
	
	@Override
    protected ResourceLocation getEntityTexture(EntityOcelot o)
    {
    	if(VampireMob.get(o).isBitten()){
    		return vampireOcelotTextures;
    	}
        return super.getEntityTexture(o);
    }

}
