package de.teamlapen.vampirism.proxy;

public interface IProxy {

	/**
	 * Registeres all entitys
	 */
	public void registerEntitys();
	/**
	 * Registeres all renders
	 */
	public void registerRenderer();
	/**
	 * Registeres all sounds
	 */
	public void registerSounds();
	
	/**
	 * Registers all important subscriptions, which should be registered at startup (postinit)
	 */
	public void registerSubscriptions();
}
