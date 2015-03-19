package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.player.EntityPlayer;

public interface IProxy {

	/**
	 * Registeres all entitys
	 */
	public void registerEntitys();

	/**
	 * Register keybindings
	 */
	public void registerKeyBindings();

	/**
	 * Registeres all renders
	 */
	public void registerRenderer();

	/**
	 * Registeres all sounds
	 */
	public void registerSounds();

	/**
	 * Registers all important subscriptions, which should be registered at
	 * startup (init)
	 */
	public void registerSubscriptions();
	
	/**
	 * @return Clientside: thePlayer, Serverside: null
	 */
	public EntityPlayer getSPPlayer();
	
	/**
	 * Translate the string to local language if on clientS
	 * @param s
	 * @return
	 */
	public String translateToLocal(String s);
}
