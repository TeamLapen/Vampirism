package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.RequestEntityUpdatePacket;

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
				VampirePlayer.loadProxyData((EntityPlayer) event.entity);
				VampirePlayer.get((EntityPlayer) event.entity).sync(true);
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

}
