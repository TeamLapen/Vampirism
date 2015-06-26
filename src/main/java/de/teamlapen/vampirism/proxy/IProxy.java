package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

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

	public void onClientTick(ClientTickEvent event);

	public void onServerTick(ServerTickEvent event);

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
	 * Registeres all sounds
	 */
	public void registerSounds();

	/**
	 * Registers all important subscriptions, which should be registered at startup (init)
	 */
	public void registerSubscriptions();

	public void setPlayerBat(EntityPlayer player, boolean bat);

	/**
	 * Translate the string to local language if on clientS
	 * 
	 * @param s
	 * @return
	 */
	public String translateToLocal(String s);
}