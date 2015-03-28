package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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
	
	//Coffin methods
    public void wakeAllPlayers();
    
    public boolean areAllPlayersAsleepCoffin();
    
	public void updateAllPlayersSleepingFlagCoffin();
	
	/**
	 * Called on client to replace the texture location of vampire entitys by the fake vampire version
	 * @param entity
	 * @param loc
	 * @return
	 */
	public ResourceLocation checkVampireTexture(Entity entity,ResourceLocation loc);
}