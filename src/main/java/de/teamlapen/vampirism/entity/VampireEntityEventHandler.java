package de.teamlapen.vampirism.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class VampireEntityEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if(event.entity instanceof EntityLiving && !(event.entity instanceof EntityPlayer) && VampireMob.get((EntityLiving)event.entity)==null){
			VampireMob.register((EntityLiving)event.entity);
		}
	}
}
