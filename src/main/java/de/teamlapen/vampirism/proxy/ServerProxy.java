package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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

	@Override
	public String translateToLocal(String s) {
		return s;
	}

	@Override
	public ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc) {
		return loc;
	}

}
