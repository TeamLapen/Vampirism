package de.teamlapen.vampirism.proxy;

import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.util.TickRunnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import de.teamlapen.vampirism.util.REFERENCE;

public interface IProxy {

	/**
	 * Called on client to replace the texture location of vampire entitys by the fake vampire version
	 * 
	 * @param entity
	 * @param loc
	 * @return
	 */
	public ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc);

	public void enableMaxPotionDuration(PotionEffect p);

	/**
	 * @return Clientside: thePlayer, Serverside: null
	 */
	public EntityPlayer getSPPlayer();

	public void onTick(TickEvent event);

	public void addTickRunnable(TickRunnable run);

	/**
	 * Registeres all entitys
	 */
	public void registerEntitys();

	/**
	 * Register keybindings
	 */
	public void registerKeyBindings();

	// Coffin methods
	// public void wakeAllPlayers();
	//
	// public boolean areAllPlayersAsleepCoffin();
	//
	// public void updateAllPlayersSleepingFlagCoffin();

	/**
	 * Registeres all renders
	 */
	public void registerRenderer();

	/**
	 * Registers all important subscriptions, which should be registered at startup (init)
	 */
	public void registerSubscriptions();

	public void setPlayerBat(EntityPlayer player, boolean bat);

	public String getKey(REFERENCE.KEY key);
}