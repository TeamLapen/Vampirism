package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.biome.BiomeVampireForest;
import de.teamlapen.vampirism.block.BlockBloodAltar4Tip.TileEntityBloodAltar4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.client.model.ModelDracula;
import de.teamlapen.vampirism.client.model.ModelGhost;
import de.teamlapen.vampirism.client.render.*;
import de.teamlapen.vampirism.client.render.particle.ParticleHandler;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.convertible.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.tileEntity.*;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.REFERENCE.KEY;
import de.teamlapen.vampirism.util.TickRunnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientProxy extends CommonProxy {
	private final static String TAG = "ClientProxy";
	private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
	public static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");

	@Override public void onServerTick(TickEvent.ServerTickEvent event) {

	}

	@Override public void addTickRunnable(TickRunnable run) {
		super.addTickRunnable(run, true);
	}

	@Override
	public void preInit() {
		super.preInit();
		registerKeyBindings();
		ModBlocks.preInitClient();
		ModItems.preInitClient();
	}

	@Override
	public void init() {
		super.init();
		registerRenderer();

		ModBlocks.initClient();
		ModItems.initClient();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc) {
		if (entity instanceof AbstractClientPlayer) {
			if (Configs.modify_vampire_player_texture&&VampirePlayer.get((EntityPlayer) entity).getLevel() > 0) {
				ResourceLocation vamp = new ResourceLocation("vampirism/temp/" + loc.getResourcePath());
				TextureHelper.createVampireTexture((EntityLivingBase) entity, loc, vamp);
				return vamp;
			}
		} else if (entity instanceof EntityCreature) {
			if (VampireMob.get((EntityCreature) entity).isVampire()) {
				ResourceLocation vamp = new ResourceLocation("vampirism/temp/" + loc.getResourceDomain()+"/"+loc.getResourcePath());
				TextureHelper.createVampireTexture((EntityLiving) entity, loc, vamp);
				return vamp;
			}
		}
		return loc;
	}

	@Override
	public void enableMaxPotionDuration(PotionEffect p) {
		p.setPotionDurationMax(true);

	}

	@Override
	public EntityPlayer getSPPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void onClientTick(ClientTickEvent event) {
		if (!event.phase.equals(TickEvent.Phase.START))
			return;
		if (OpenGlHelper.shadersSupported) {
			try {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.thePlayer == null)
					return;
				boolean active = false;
				PotionEffect pe = mc.thePlayer.getActivePotionEffect(ModPotion.saturation);
				if (pe != null && pe.getAmplifier() >= 2) {
					active = true;
				}
				EntityRenderer renderer = mc.entityRenderer;
				if (active && renderer.getShaderGroup() == null) {
					Helper.Reflection.callMethod(EntityRenderer.class,renderer,Helper.Obfuscation.getPosNames("EntityRenderer/loadShader"),new Class[]{ResourceLocation.class},saturation1);

				} else if (!active && renderer.getShaderGroup() != null && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
					renderer.getShaderGroup().deleteShaderGroup();
					Helper.Reflection.setPrivateField(EntityRenderer.class,renderer,null,Helper.Obfuscation.getPosNames("EntityRenderer/theShaderGroup"));
					renderer.switchUseShader();
				}
			} catch (Exception e) {
				if (Minecraft.getSystemTime() % 20000 == 0) {
					Logger.e(TAG, "Failed to handle saturation shader", e);
				}
			}
		}
		int i= (int) (2000*Math.random());
		if(i==0){
			Minecraft mc=Minecraft.getMinecraft();
			if(mc.theWorld!=null&&mc.thePlayer!=null&&!mc.isGamePaused()){
				if(mc.theWorld.getBiomeGenForCoords(mc.thePlayer.getPosition()) instanceof BiomeVampireForest){
					PositionedSoundRecord sound = new PositionedSoundRecord(new ResourceLocation("vampirism:ambient.vampire_biome"), 0.6F, 1F,(float)(mc.thePlayer.posX+(10*(Math.random()-0.5D))),(float)mc.thePlayer.posY,(float)(mc.thePlayer.posZ+(10*(Math.random()-0.5D))));
					mc.getSoundHandler().playSound(sound);
				}
			}
		}

	}

	public void registerKeyBindings() {
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SUCK);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.AUTO);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.SKILL);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.VISION);
		ClientRegistry.registerKeyBinding(KeyInputEventHandler.MINION_CONTROL);
	}

	public void registerRenderer() {
		RenderManager manager=Minecraft.getMinecraft().getRenderManager();
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireHunter.class, new VampireHunterRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampire.class, new VampireRenderer(manager,0.5F));
		 RenderingRegistry.registerEntityRenderingHandler(EntityDracula.class, new RendererDracula(manager,new ModelDracula(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new RendererGhost(manager,new ModelGhost(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireBaron.class, new RendererVampireBaron(manager,0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinion.class, new RendererVampireMinion(manager,0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDeadMob.class, new RendererDeadMob(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindingBat.class, new RenderBat(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityPortalGuard.class,new RendererPortalGuard(manager,0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityConvertedCreature.class, new RendererConvertedCreature(manager));


		//TODO update forge and use https://github.com/MinecraftForge/MinecraftForge/commit/b175d265b9643faf3408754f6923a8f55f6ee5d3 for rendering
		// BloodAltar
		TileEntitySpecialRenderer bloodAltar = new RendererBloodAltar1();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar1.class, bloodAltar);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar1), new RenderTileEntityItem(bloodAltar, new TileEntityBloodAltar1()));

		// BloodAltar2
		TileEntitySpecialRenderer bloodAltar2 = new RendererBloodAltar2();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar2.class, bloodAltar2);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar2), new RenderTileEntityItem(bloodAltar2, new TileEntityBloodAltar2()));

		// BloodAltar3
		// ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltarTier3.class, new RendererBloodAltarTier3());
		TileEntitySpecialRenderer tileAltar4 = new RendererBloodAltar4();

		// BloodAltar4
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar4.class, tileAltar4);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar4), new RenderTileEntityItem(tileAltar4, new TileEntityBloodAltar4()));
		// ChurchAltar
		TileEntitySpecialRenderer churchAltar = new RendererChurchAltar();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChurchAltar.class, churchAltar);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.churchAltar), new RenderTileEntityItem(churchAltar, new TileEntityChurchAltar()));

		// BloodAltar4Tip
		TileEntitySpecialRenderer altar4Tip = new RendererBloodAltar4Tip();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodAltar4Tip.class, altar4Tip);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.bloodAltar4Tip), new RenderTileEntityItem(altar4Tip, new TileEntityBloodAltar4Tip()));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoffin.class, new RendererCoffin());

		TileEntitySpecialRenderer tent = new RendererTent();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTent.class, tent);
		MinecraftForgeClient.registerItemRenderer(ModItems.tent, new RenderTileEntityItem(tent, new TileEntityTent()).setRotation(45F).setScale(0.55F));
	}

	@Override
	public void registerSubscriptions() {
		super.registerSubscriptions();
		MinecraftForge.EVENT_BUS.register(new VampireHudOverlay(Minecraft.getMinecraft()));
		Object renderHandler = new RenderHandler(Minecraft.getMinecraft());
		MinecraftForge.EVENT_BUS.register(renderHandler);
		MinecraftForge.EVENT_BUS.register(ParticleHandler.instance());
		FMLCommonHandler.instance().bus().register(renderHandler);
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
		FMLCommonHandler.instance().bus().register(ParticleHandler.instance());
	}

	@Override
	public void setPlayerBat(EntityPlayer player, boolean bat) {
		float width = bat ? BatSkill.BAT_WIDTH : BatSkill.PLAYER_WIDTH;
		float height = bat ? BatSkill.BAT_HEIGHT : BatSkill.PLAYER_HEIGHT;
		Helper.Reflection.callMethod(Entity.class, player, Helper.Obfuscation.getPosNames("Entity/setSize"), Helper.Reflection.createArray(float.class, float.class), width, height);
		player.setPosition(player.posX, player.posY + (bat ? 1F : -1F) * (BatSkill.PLAYER_HEIGHT - BatSkill.BAT_HEIGHT), player.posZ);
		// Logger.i("test3", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e1 "+player.eyeHeight);
		player.eyeHeight = (bat ? BatSkill.BAT_EYE_HEIGHT  : player.getDefaultEyeHeight());// Different from Server side
		// Logger.i("test4", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e2 "+player.eyeHeight);
	}

	@Override
	public String getKey(KEY key) {
		return GameSettings.getKeyDisplayString(KeyInputEventHandler.getBindedKey(key));
	}
}
