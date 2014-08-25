package de.teamlapen.vampirism;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.playervampire.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class VampireEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if(event.entity instanceof EntityPlayer && VampirePlayer.get((EntityPlayer)event.entity)==null){
			VampirePlayer.register((EntityPlayer)event.entity);
		}
	}
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event){
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			VampirePlayer.saveProxyData((EntityPlayer)event.entity);
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			VampirePlayer.loadProxyData((EntityPlayer)event.entity);
		}
	}
}
