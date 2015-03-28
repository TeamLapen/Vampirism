package de.teamlapen.vampirism.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Tip.TileEntityBloodAltarTier4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelBipedCloaked;
import de.teamlapen.vampirism.client.model.ModelDracula;
import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.client.model.ModelVampire;
import de.teamlapen.vampirism.client.render.PitchforkRenderer;
import de.teamlapen.vampirism.client.render.RenderTileEntityItem;
import de.teamlapen.vampirism.client.render.RendererBloodAltar;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier2;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier3;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4;
import de.teamlapen.vampirism.client.render.RendererBloodAltarTier4Tip;
import de.teamlapen.vampirism.client.render.RendererChurchAltar;
import de.teamlapen.vampirism.client.render.RendererCoffin;
import de.teamlapen.vampirism.client.render.RendererDracula;
import de.teamlapen.vampirism.client.render.RendererGhost;
import de.teamlapen.vampirism.client.render.RendererTorch;
import de.teamlapen.vampirism.client.render.RendererVampireLord;
import de.teamlapen.vampirism.client.render.RendererVampireMinion;
import de.teamlapen.vampirism.client.render.TextureHelper;
import de.teamlapen.vampirism.client.render.VampireHunterRenderer;
import de.teamlapen.vampirism.client.render.VampireRenderer;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireLord;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
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
	private final ResourceLocation vampire_overlay = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire_cover.png");
	public static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");

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
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireLord.class, new RendererVampireLord(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinion.class, new RendererVampireMinion(new ModelVampire(),0.5F));
		MinecraftForgeClient.registerItemRenderer(ModItems.pitchfork, new PitchforkRenderer());
		MinecraftForgeClient.registerItemRenderer(ModItems.torch, new RendererTorch());
		
		

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar.class, new RendererBloodAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier2.class, new RendererBloodAltarTier2());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier3.class, new RendererBloodAltarTier3());
		TileEntitySpecialRenderer tier4=new RendererBloodAltarTier4();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier4.class, tier4);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltarTier4), new RenderTileEntityItem(tier4,new TileEntityBloodAltarTier4()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoffin.class, new RendererCoffin());
		TileEntitySpecialRenderer churchAltar=new RendererChurchAltar();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChurchAltar.class, churchAltar);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.churchAltar), new RenderTileEntityItem(churchAltar,new TileEntityChurchAltar()));
		TileEntitySpecialRenderer tier4Tip=new RendererBloodAltarTier4Tip();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier4Tip.class,tier4Tip);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltarTier4Tip), new RenderTileEntityItem(tier4Tip,new TileEntityBloodAltarTier4Tip()));
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
	

	@Override
	public ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc) {
		if(entity instanceof AbstractClientPlayer){
			if(VampirePlayer.get((EntityPlayer)entity).getLevel()>0){
				ResourceLocation vamp=new ResourceLocation("vampirism/temp/"+loc.hashCode());
				TextureHelper.createVampireTexture((EntityLivingBase)entity,loc,vamp);
				return vamp;
			}
		}
		else if(entity instanceof EntityCreature){
			if(VampireMob.get((EntityCreature) entity).isVampire()){
				ResourceLocation vamp=new ResourceLocation("vampirism/temp/"+loc.hashCode());
				TextureHelper.createVampireTexture((EntityLiving)entity,loc,vamp);
				return vamp;
			}
		}
		return loc;
	}
}
