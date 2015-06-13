package de.teamlapen.vampirism.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderBat;
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
import net.minecraft.potion.PotionEffect;
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
import de.teamlapen.vampirism.block.BlockBloodAltar4Tip.TileEntityBloodAltar4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.client.render.PitchforkRenderer;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.client.render.RenderTileEntityItem;
import de.teamlapen.vampirism.client.render.RendererBloodAltar1;
import de.teamlapen.vampirism.client.render.RendererBloodAltar2;
import de.teamlapen.vampirism.client.render.RendererBloodAltar4;
import de.teamlapen.vampirism.client.render.RendererBloodAltar4Tip;
import de.teamlapen.vampirism.client.render.RendererChurchAltar;
import de.teamlapen.vampirism.client.render.RendererCoffin;
import de.teamlapen.vampirism.client.render.RendererDeadMob;
import de.teamlapen.vampirism.client.render.RendererGhost;
import de.teamlapen.vampirism.client.render.RendererTorch;
import de.teamlapen.vampirism.client.render.RendererVampireLord;
import de.teamlapen.vampirism.client.render.RendererVampireMinion;
import de.teamlapen.vampirism.client.render.TextureHelper;
import de.teamlapen.vampirism.client.render.VampireHunterRenderer;
import de.teamlapen.vampirism.client.render.VampireRenderer;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityDeadMob;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireLord;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class ClientProxy extends CommonProxy {
	private final static String TAG = "ClientProxy";
	private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
	public static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");
	

	@Override
	public void registerKeyBindings() {
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SUCK);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.AUTO);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SKILL);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.VISION);
	}

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireHunter.class, new VampireHunterRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityVampire.class, new VampireRenderer(0.5F));
		//RenderingRegistry.registerEntityRenderingHandler(EntityDracula.class, new RendererDracula(new ModelDracula(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RendererGhost(new ModelGhost(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireLord.class, new RendererVampireLord(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinion.class, new RendererVampireMinion(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDeadMob.class, new RendererDeadMob());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindingBat.class, new RenderBat());
		MinecraftForgeClient.registerItemRenderer(ModItems.pitchfork, new PitchforkRenderer());
		//MinecraftForgeClient.registerItemRenderer(ModItems.torch, new RendererTorch());
		
		

		//BloodAltar
		TileEntitySpecialRenderer bloodAltar = new RendererBloodAltar1();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar1.class, bloodAltar);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar1), new RenderTileEntityItem(bloodAltar, new TileEntityBloodAltar1()));
		
		//BloodAltar2
		TileEntitySpecialRenderer bloodAltar2 = new RendererBloodAltar2();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar2.class, bloodAltar2);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar2), new RenderTileEntityItem(bloodAltar2, new TileEntityBloodAltar2()));
		
		
		//BloodAltar3
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier3.class, new RendererBloodAltarTier3());
		TileEntitySpecialRenderer tileAltar4=new RendererBloodAltar4();
		
		//BloodAltar4
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar4.class, tileAltar4);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar4), new RenderTileEntityItem(tileAltar4,new TileEntityBloodAltar4()));
		//ChurchAltar
		TileEntitySpecialRenderer churchAltar=new RendererChurchAltar();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChurchAltar.class, churchAltar);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.churchAltar), new RenderTileEntityItem(churchAltar,new TileEntityChurchAltar()));
		
		//BloodAltar4Tip
		TileEntitySpecialRenderer altar4Tip=new RendererBloodAltar4Tip();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar4Tip.class,altar4Tip);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar4Tip), new RenderTileEntityItem(altar4Tip,new TileEntityBloodAltar4Tip()));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoffin.class, new RendererCoffin());	
	}

	@Override
	public void registerSounds() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerSubscriptions() {
		super.registerSubscriptions();
		MinecraftForge.EVENT_BUS.register(new VampireHudOverlay(Minecraft.getMinecraft()));
		Object renderHandler=new RenderHandler(Minecraft.getMinecraft());
		MinecraftForge.EVENT_BUS.register(renderHandler);
		FMLCommonHandler.instance().bus().register(renderHandler);
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
	}

	@Override
	public EntityPlayer getSPPlayer() {
		return (EntityPlayer)Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public String translateToLocal(String s) {
		return I18n.format(s, new Object[0]);
	}
	
	
	public void onClientTick(ClientTickEvent event){
		if(!event.phase.equals(TickEvent.Phase.START))return;
		if(OpenGlHelper.shadersSupported){
			try {
				Minecraft mc=Minecraft.getMinecraft();
				if(mc.thePlayer==null)return;
				boolean active=false;
				PotionEffect pe=mc.thePlayer.getActivePotionEffect(ModPotion.saturation);
				if(pe!=null&&pe.getAmplifier()>=2){
					active=true;
				}
				EntityRenderer renderer=mc.entityRenderer;
				if(active&&renderer.theShaderGroup==null){
					try {
						renderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), saturation1);
						renderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
					} catch (JsonException e) {
						
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


	@Override
	public void setPlayerBat(EntityPlayer player, boolean bat) {
		float width=bat?BatSkill.BAT_WIDTH:BatSkill.PLAYER_WIDTH;
		float height=bat?BatSkill.BAT_HEIGHT:BatSkill.PLAYER_HEIGHT;
		Helper.Reflection.callMethod(Entity.class, player, Helper.Obfuscation.getPosNames("Entity/setSize"),Helper.Reflection.createArray(float.class,float.class),width,height);
		player.setPosition(player.posX,player.posY+(bat?1F:-1F)*(BatSkill.PLAYER_HEIGHT-BatSkill.BAT_HEIGHT),player.posZ);
        //Logger.i("test3", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e1 "+player.eyeHeight);
		player.eyeHeight = (bat ?BatSkill.BAT_EYE_HEIGHT - player.yOffset: player.getDefaultEyeHeight());//Different from Server side
		//Logger.i("test4", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e2 "+player.eyeHeight);
	}

	@Override
	public void enableMaxPotionDuration(PotionEffect p) {
		p.setPotionDurationMax(true);
		
	}
}
