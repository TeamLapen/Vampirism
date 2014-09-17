package de.teamlapen.vampirism.client.render.vanilla;

import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderVampireZombie extends RenderZombie{

    private static final ResourceLocation vampireZombiePigmanTextures = new ResourceLocation("textures/entity/cow/cow.png");
    private static final ResourceLocation vampireZombieTextures = new ResourceLocation("textures/entity/pig/pig.png");
    private static final ResourceLocation vampireZombieVillagerTextures = new ResourceLocation("textures/entity/pig/pig.png");
    
	@Override
    protected ResourceLocation getEntityTexture(EntityZombie z)
    {
    	if(VampireMob.get(z).isBitten()){
    		return z instanceof EntityPigZombie ? vampireZombiePigmanTextures : (z.isVillager() ? vampireZombieVillagerTextures : vampireZombieTextures);
    	}
        return super.getEntityTexture(z);
    }
}
