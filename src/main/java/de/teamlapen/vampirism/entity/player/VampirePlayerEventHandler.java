package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.castleDim.ChunkProviderCastle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.network.RequestEntityUpdatePacket;

public class VampirePlayerEventHandler {

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event) {
		if (VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		try {
			if (VampirePlayer.get(event.player).isSkillActive(Skills.batMode)|| (event.player.worldObj.provider.dimensionId==VampirismMod.castleDimensionId&&!ChunkProviderCastle.allowedToBuildHere(event.player))) {
				event.setCanceled(true);
			}
		} catch (Exception e) {
			// Added try catch to prevent any exception in case some other mod uses auto placers or so
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode)||(event.entity.worldObj.provider.dimensionId==VampirismMod.castleDimensionId&&!ChunkProviderCastle.allowedToBuildHere(event.entity))) {
			event.setCanceled(true);
		}
	}

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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemUse(PlayerUseItemEvent.Start event) {
		if (VampirePlayer.get(event.entityPlayer).isSkillActive(Skills.batMode)) {
			event.setCanceled(true);
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

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent e) {
		VampirePlayer.get(e.player).onChangedDimension(e.fromDim, e.toDim);
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		VampirePlayer.get(event.entityPlayer).copyFrom(event.original);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e) {
		VampirePlayer.get(e.player).onPlayerLoggedIn();
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent e) {
		VampirePlayer.get(e.player).onPlayerLoggedOut();
	}

}
