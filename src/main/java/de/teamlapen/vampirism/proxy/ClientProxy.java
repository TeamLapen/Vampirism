package de.teamlapen.vampirism.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelDracula;
import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.client.model.ModelVampire;
import de.teamlapen.vampirism.client.render.PitchforkRenderer;
import de.teamlapen.vampirism.client.render.RendererBloodAltar;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier2;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier3;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4;
import de.teamlapen.vampirism.client.render.RendererCoffin;
import de.teamlapen.vampirism.client.render.RendererDracula;
import de.teamlapen.vampirism.client.render.RendererGhost;
import de.teamlapen.vampirism.client.render.RendererTorch;
import de.teamlapen.vampirism.client.render.RendererVampireLord;
import de.teamlapen.vampirism.client.render.RendererVampireMinion;
import de.teamlapen.vampirism.client.render.VampireHunterRenderer;
import de.teamlapen.vampirism.client.render.VampireRenderer;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireCow;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireHorse;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireOcelot;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampirePig;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireSheep;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireVillager;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireWitch;
import de.teamlapen.vampirism.client.render.vanilla.RenderVampireWolf;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireLord;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier3;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class ClientProxy extends CommonProxy {
	private final static String TAG = "ClientProxy";
	private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");

	@Override
	public void registerKeyBindings() {
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SUCK);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.AUTO);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SKILL);
	}

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireHunter.class, new VampireHunterRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityVampire.class, new VampireRenderer(new ModelVampire(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDracula.class, new RendererDracula(new ModelDracula(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RendererGhost(new ModelGhost(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireLord.class, new RendererVampireLord(new ModelVampire(),0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinion.class, new RendererVampireMinion(new ModelVampire(),0.5F));
		MinecraftForgeClient.registerItemRenderer(ModItems.pitchfork, new PitchforkRenderer());
		MinecraftForgeClient.registerItemRenderer(ModItems.torch, new RendererTorch());
		
		// Vampire vanilla renderers
		RenderingRegistry.registerEntityRenderingHandler(EntityCow.class, new RenderVampireCow(new ModelCow(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, new RenderVampirePig(new ModelPig(), new ModelPig(0.5F), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntitySheep.class, new RenderVampireSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVillager.class, new RenderVampireVillager());
		RenderingRegistry.registerEntityRenderingHandler(EntityWolf.class, new RenderVampireWolf(new ModelWolf(), new ModelWolf(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityOcelot.class, new RenderVampireOcelot(new ModelOcelot(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityWitch.class, new RenderVampireWitch());
		RenderingRegistry.registerEntityRenderingHandler(EntityHorse.class, new RenderVampireHorse(new ModelHorse(), 0.75F));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar.class, new RendererBloodAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier2.class, new RendererBloodAltarTier2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier3.class, new RendererBloodAltarTier3());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier4.class, new RendererBloodAltarTier4());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoffin.class, new RendererCoffin());
	}

	@Override
	public void registerSounds() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerSubscriptions() {
		Logger.i(TAG, "Registering client subscriptions");
		super.registerSubscriptions();
		MinecraftForge.EVENT_BUS.register(new VampireHudOverlay(Minecraft.getMinecraft()));
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
		MinecraftForge.EVENT_BUS.register(new RendererTorch());
	}

	@Override
	public EntityPlayer getSPPlayer() {
		return (EntityPlayer)Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public String translateToLocal(String s) {
		return I18n.format(s, new Object[0]);
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event){
		if(!event.phase.equals(TickEvent.Phase.START))return;
		if(OpenGlHelper.shadersSupported){
			try {
				Minecraft mc=Minecraft.getMinecraft();
				if(mc.thePlayer==null)return;
				boolean active=mc.thePlayer.isPotionActive(ModPotion.saturation);
				EntityRenderer renderer=mc.entityRenderer;
				if(active&&renderer.theShaderGroup==null){
					try {
						renderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), saturation1);
						renderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
					} catch (JsonException e) {
						e.printStackTrace();
					}
				}
				else if(!active&&renderer.theShaderGroup!=null&&renderer.theShaderGroup.getShaderGroupName().equals(saturation1.toString())){
					renderer.theShaderGroup.deleteShaderGroup();
					renderer.theShaderGroup=null;
				}
			} catch (Exception e) {
				if(Minecraft.getSystemTime()%20000==0){
					Logger.e(TAG, "Failed to handle saturation shader",e);
				}
			}
		}

	}
}
