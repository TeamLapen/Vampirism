package de.teamlapen.vampirism.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.passive.EntityCow;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelVampire;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.client.render.RendererBloodAltar;
import de.teamlapen.vampirism.client.render.VampireHunterRenderer;
import de.teamlapen.vampirism.client.render.VampireRenderer;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireCow;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class ClientProxy extends CommonProxy{
	private final static String TAG="ClientProxy";
	
	

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireHunter.class,new VampireHunterRenderer(new ModelVampireHunter(),0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampire.class,new VampireRenderer(new ModelVampire(),0.5F));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCow.class, new RenderVampireCow(new ModelCow(), 0.7F));
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar.class, new RendererBloodAltar());
		
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
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
	}

	@Override
	public void registerKeyBindings() {
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SUCK);
		
	}

}
