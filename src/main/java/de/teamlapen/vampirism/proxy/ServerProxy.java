package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy extends CommonProxy {

	@Override
	public void registerKeyBindings() {
		// Client side only

	}

	@Override
	public void registerRenderer() {
		// Client side only

	}

	@Override
	public void registerSounds() {
		// Client side only

	}

	@Override
	public void registerSubscriptions() {
		super.registerSubscriptions();

	}

	@Override
	public EntityPlayer getSPPlayer() {
		return null;
	}

}
