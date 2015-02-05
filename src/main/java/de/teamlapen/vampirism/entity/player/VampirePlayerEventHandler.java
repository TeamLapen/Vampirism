package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.BALANCE;

public class VampirePlayerEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && VampirePlayer.get((EntityPlayer) event.entity) == null) {
			VampirePlayer.register((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
			VampirePlayer.loadProxyData((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {

			if (BALANCE.VAMPIRE_PLAYER_LOOSE_LEVEL && event.source.damageType.equals("mob") && event.source instanceof EntityDamageSource) {
				if (event.source.getEntity() instanceof EntityVampireHunter) {
					VampirePlayer.get((EntityPlayer) event.entity).looseLevel();
				}
			}
			VampirePlayer.saveProxyData((EntityPlayer) event.entity, true);
		}
	}

}
