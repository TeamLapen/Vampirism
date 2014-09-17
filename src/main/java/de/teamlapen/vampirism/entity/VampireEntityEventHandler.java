package de.teamlapen.vampirism.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class VampireEntityEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if(event.entity instanceof EntityLiving && VampireMob.get((EntityLiving)event.entity)==null){
			VampireMob.register((EntityLiving)event.entity);
		}
	}
	
}
