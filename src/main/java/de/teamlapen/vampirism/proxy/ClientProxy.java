package de.teamlapen.vampirism.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelVampire;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.client.render.VampireHunterRenderer;
import de.teamlapen.vampirism.client.render.VampireRenderer;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.Logger;
til.Logger;

public class ClientProxy extends CommonProxy{
	private final static String TAG="ClientProxy";

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireHunter.class,new VampireHunterRenderer(new ModelVampireHunter(),0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampire.class,new VampireRenderer(new ModelVampire(),0.5F));
		
	}

	@Override
	public void registerSounds() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void registerSubscriptions(){
		Logger.i(TAG, "Registering client subscriptions");
		super.registerSubscriptions();
		MinecraftForge.EVENT_BUS.register(new VampireHudOverlay(Minecraft.getMinecraft()));
	}

}
