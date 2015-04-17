package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.network.RequestEntityUpdatePacket;
import de.teamlapen.vampirism.util.Logger;

public class VampirePlayerEventHandler {

	@SubscribeEvent(receiveCanceled = true)
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && VampirePlayer.get((EntityPlayer) event.entity) == null) {
			VampirePlayer.register((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer) {
			if (event.entity.worldObj.isRemote) {
				VampirismMod.modChannel.sendToServer(new RequestEntityUpdatePacket(event.entity));
			} else {
				VampirePlayer.onPlayerJoinWorld((EntityPlayer) event.entity);
			}

		}
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			if (VampirePlayer.get((EntityPlayer) event.entityLiving).onEntityAttacked(event.source, event.ammount)) {
				event.setCanceled(true);
			}

		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		if (event.entity instanceof EntityPlayer) {
			VampirePlayer.get((EntityPlayer) event.entity).onDeath(event.source);
			if (!event.entity.worldObj.isRemote) {
				VampirePlayer.saveProxyData((EntityPlayer) event.entity, true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) event.entity;
			PlayerModifiers.addJumpBoost(VampirePlayer.get(p).getLevel(), p);

		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			VampirePlayer.get((EntityPlayer) event.entity).onUpdate();
			;
		}
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onItemUse(PlayerUseItemEvent.Start event){
		if(VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode)){
			event.setCanceled(true);
			Logger.i("tes", "a");
		}
	}
	
	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event){
		if(VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode)){
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event){
		try {
			if(VampirePlayer.get(event.player).isSkillActive(Skills.batMode)){
				event.setCanceled(true);
			}
		} catch (Exception e) {
			//Added try catch to prevent any exception in case some other mod uses auto placers or so
		}
	}

}
